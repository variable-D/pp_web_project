package com.pp_web_project.controller;

import com.pp_web_project.domain.SkProductDetalis;
import com.pp_web_project.dto.EsimResponseDto;
import com.pp_web_project.service.sk.interfaces.SkProductService;
import com.pp_web_project.util.ExcelExportUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.pp_web_project.util.PlanNameMapper;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/sk/products")
public class SkController {

    private final SkProductService skProductService;

    @GetMapping("/sold")
    public String sold(
            @RequestParam(value = "orderNumber", required = false) String orderNumber,
            @RequestParam(value = "serviceNumber", required = false) String serviceNumber,
            @RequestParam(value = "mgmtNumber", required = false) String mgmtNumber,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
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

        String logo = "eSIM";
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
    public String eSIMDetails(Model model) {
        // eSIM 상세 페이지로 이동

        Map<String, String> categoryList = new LinkedHashMap<>();
        categoryList.put("판매된 상품", "/admin/sk/products/sold");
        categoryList.put("상세 조회", "/admin/sk/products/eSIMDetails");

        String logo = "eSIM";
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("logo", logo);
        return "admin/sk/eSIMDetails";
    }

    @PostMapping("/eSIMDetails")
    public String eSIMDetails(@RequestParam("rental_mgmt_num") String rentalMgmtNum,
                              Model model) {

        // ✅ 1. SK API 호출 (서비스 계층)
        EsimResponseDto rentalData = skProductService.fetchEsimDetailFromApi(rentalMgmtNum);

        // ✅ 2. 카테고리 메뉴 구성
        Map<String, String> categoryList = new LinkedHashMap<>();
        categoryList.put("판매된 상품", "/admin/sk/products/sold");
        categoryList.put("상세 조회", "/admin/sk/products/eSIMDetails");
        String logo = "eSIM";
        // ✅ 3. Model에 데이터 담기
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("logo", logo);
        model.addAttribute("rentalData", rentalData);
        model.addAttribute("planMap", PlanNameMapper.PLAN_MAP); // ✅ 여기!

        // ✅ 4. Thymeleaf 템플릿으로 이동
        return "admin/sk/eSIMDetails";  // 👉 eSIM 상세 페이지 (POST 결과 포함)
    }
}
