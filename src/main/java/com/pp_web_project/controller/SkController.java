package com.pp_web_project.controller;

import com.pp_web_project.domain.SkProductDetalis;
import com.pp_web_project.dto.EsimResponseDto;
import com.pp_web_project.dto.RentalMgmtNumDto;
import com.pp_web_project.service.sk.interfaces.SkProductService;
import com.pp_web_project.util.ExcelExportUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.pp_web_project.util.PlanNameMapper;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/sk/products")
public class SkController {

    private final SkProductService skProductService;

    private final MessageSource messageSource;

    @GetMapping("/sold")
    public String sold(
            @RequestParam(value = "orderNumber", required = false) String orderNumber,
            @RequestParam(value = "serviceNumber", required = false) String serviceNumber,
            @RequestParam(value = "mgmtNumber", required = false) String mgmtNumber,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            Locale locale,
            Model model
    ) {
        // ✅ 기본값: 오늘 00:00 ~ 23:59 조회
        if (startDate == null) {
            startDate = LocalDate.now(); // 기본: 오늘 날짜
        }
        if (endDate == null) {
            endDate = LocalDate.now(); // 기본: 오늘 날짜
        }

        // ✅ JPA에서 `endDate`의 23:59:59까지 포함되도록 +1일 적용
        LocalDate adjustedEndDate = endDate.plusDays(1);

        String logo = messageSource.getMessage("messages.logo", null, locale);
        List<SkProductDetalis> soldItems = null;

        if (orderNumber != null && !orderNumber.isEmpty()) {
            soldItems = skProductService.findByOrderNum(orderNumber);
        } else if (serviceNumber != null && !serviceNumber.isEmpty()) {
            soldItems = skProductService.findByRomingPhoneNum(serviceNumber);
        } else if (mgmtNumber != null && !mgmtNumber.isEmpty()) {
            soldItems = skProductService.findByRentalMgmtNum(mgmtNumber);
        } else {
            // ✅ `LocalDate` 그대로 사용, JPA에서 23:59까지 포함
            soldItems = skProductService.findBySkProductAll(startDate, adjustedEndDate);
        }

        Map<String, String> categoryList = new LinkedHashMap<>();
        categoryList.put("판매된 상품", "/admin/sk/products/sold");
        categoryList.put("상세 조회", "/admin/sk/products/eSIMDetails");

        model.addAttribute("categoryList", categoryList);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("logo", logo);
        model.addAttribute("soldItems", soldItems);
        model.addAttribute("orderNumber", orderNumber);
        model.addAttribute("serviceNumber", serviceNumber);
        model.addAttribute("mgmtNumber", mgmtNumber);

        return "admin/sk/sold";
    }


    @GetMapping("/sold/download")
    public ResponseEntity<byte[]> adminDownloadSoldSkItemExcel(
            @RequestParam(value = "orderNumber", required = false) String orderNumber,
            @RequestParam(value = "serviceNumber", required = false) String serviceNumber,
            @RequestParam(value = "mgmtNumber", required = false) String mgmtNumber,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate) {

        try {

            // ✅ 기본값: 오늘 00:00 ~ 23:59 조회
            if (startDate == null) {
                startDate = LocalDate.now(); // 기본: 오늘 날짜
            }
            if (endDate == null) {
                endDate = LocalDate.now(); // 기본: 오늘 날짜
            }

            // ✅ JPA에서 `endDate`의 23:59:59까지 포함되도록 +1일 적용
            LocalDate adjustedEndDate = endDate.plusDays(1);
            List<SkProductDetalis> soldItems = null;

            if (orderNumber != null && !orderNumber.isEmpty()) {
                soldItems = skProductService.findByOrderNum(orderNumber);
            } else if (serviceNumber != null && !serviceNumber.isEmpty()) {
                soldItems = skProductService.findByRomingPhoneNum(serviceNumber);
            } else if (mgmtNumber != null && !mgmtNumber.isEmpty()) {
                soldItems = skProductService.findByRentalMgmtNum(mgmtNumber);
            } else {
                // ✅ `LocalDate` 그대로 사용, JPA에서 23:59까지 포함
                soldItems = skProductService.findBySkProductAll(startDate, adjustedEndDate);
            }

            // Excel 데이터 생성
            byte[] excelData = ExcelExportUtil.exportSoldSkItemsToExcel(soldItems);

            // 파일명 설정 (YYYYMMDD 형식)
            String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String fileName = "SK_판매완료_상품_" + timestamp + ".xlsx";

            // 한글 파일명 깨짐 방지 (브라우저별 처리)
            String encodedFileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            // 파일 다운로드 응답 생성
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);

        } catch (IOException e) {
            log.error("🚨 Excel 파일 생성 중 오류 발생!", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            log.error("🚨 알 수 없는 오류 발생!", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PatchMapping("/sold/update")
    public ResponseEntity<String> updateCodeOneStatus(
            @RequestParam(name = "ids") List<Long> ids,
            @RequestParam(name = "status") boolean status) {

        int updatedCount = skProductService.updateIsCodeOneStatusByIds(ids, status);
        return ResponseEntity.ok("✅ " + updatedCount + "개의 코드원이 " + (status ? "활성화" : "비활성화") + "되었습니다.");
    }


    @GetMapping("/eSIMDetails")
    public String eSIMDetails(Model model , Locale locale) {
        // eSIM 상세 페이지로 이동

        Map<String, String> categoryList = new LinkedHashMap<>();
        categoryList.put("상세 조회", "/admin/sk/products/eSIMDetails");
        categoryList.put("판매된 상품", "/admin/sk/products/sold");

        String logo = messageSource.getMessage("messages.logo", null, locale);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("logo", logo);
        return "admin/sk/eSIMDetails";
    }

    @PostMapping("/eSIMDetails")
    public String eSIMDetails(@Valid @ModelAttribute RentalMgmtNumDto rentalMgmtNumDto,
                              BindingResult bindingResult,
                              Model model, Locale locale) {

        Map<String, String> categoryList = new LinkedHashMap<>();
        categoryList.put("상세 조회", "/admin/sk/products/eSIMDetails");
        categoryList.put("판매된 상품", "/admin/sk/products/sold");
        String logo = messageSource.getMessage("messages.logo", null, locale);

        // 유효성 검사 실패 시
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", bindingResult.getFieldError().getDefaultMessage());
            model.addAttribute("categoryList", categoryList);
            model.addAttribute("logo", logo);

            log.info("bindingResult: {}", bindingResult);
            return "admin/sk/eSIMDetails";
        }

        // 실제 로직 처리
        EsimResponseDto rentalData = skProductService.fetchEsimDetailFromApi(rentalMgmtNumDto.getRentalMgmtNum());

        log.info("rentalData: {}", rentalData);

        if (rentalData == null || rentalData.getRentalMgmtNum() == null || rentalData.getRentalMgmtNum().isEmpty()) {
            String errorMessage = messageSource.getMessage("error.no.data", null, locale);
            model.addAttribute("message", errorMessage);
        }

        model.addAttribute("categoryList", categoryList);
        model.addAttribute("logo", logo);
        model.addAttribute("rentalData", rentalData);
        model.addAttribute("planMap", PlanNameMapper.PLAN_MAP);

        return "admin/sk/eSIMDetails";
    }
}
