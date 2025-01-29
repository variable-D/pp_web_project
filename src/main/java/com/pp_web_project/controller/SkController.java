package com.pp_web_project.controller;

import com.pp_web_project.domain.JoytelProduct;
import com.pp_web_project.domain.SkProductDetalis;
import com.pp_web_project.service.sk.interfaces.SkProductService;
import com.pp_web_project.util.ExcelExportUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
            Model model
    ) {

        String logo = "eSIM";

        List<SkProductDetalis> soldItems = null;
        Page<SkProductDetalis> soldItemsPage = null;
        Pageable pageable = PageRequest.of(0, 50, Sort.by("id").descending());

        if(orderNumber != null && !orderNumber.isEmpty()) {
            soldItemsPage = skProductService.findByOrderNum(orderNumber, pageable);
            soldItems = soldItemsPage.getContent();
        } else if(serviceNumber != null && !serviceNumber.isEmpty()) {
            soldItemsPage = skProductService.findByRomingPhoneNum(serviceNumber, pageable);
            soldItems = soldItemsPage.getContent();
        } else if(mgmtNumber != null && !mgmtNumber.isEmpty()) {
            soldItemsPage = skProductService.findByRentalMgmtNum(mgmtNumber, pageable);
            soldItems = soldItemsPage.getContent();
        }else{
            soldItemsPage = skProductService.findBySkProductAll(pageable);
            soldItems = soldItemsPage.getContent();
        }


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
            @RequestParam(value = "mgmtNumber", required = false) String mgmtNumber) {

        try {

            List<SkProductDetalis> soldItems = null;
            Page<SkProductDetalis> soldItemsPage = null;
            Pageable pageable = PageRequest.of(0, 50, Sort.by("id").descending());

            if(orderNumber != null && !orderNumber.isEmpty()) {
                soldItemsPage = skProductService.findByOrderNum(orderNumber, pageable);
                soldItems = soldItemsPage.getContent();
            } else if(serviceNumber != null && !serviceNumber.isEmpty()) {
                soldItemsPage = skProductService.findByRomingPhoneNum(serviceNumber, pageable);
                soldItems = soldItemsPage.getContent();
            } else if(mgmtNumber != null && !mgmtNumber.isEmpty()) {
                soldItemsPage = skProductService.findByRentalMgmtNum(mgmtNumber, pageable);
                soldItems = soldItemsPage.getContent();
            }else{
                soldItemsPage = skProductService.findBySkProductAll(pageable);
                soldItems = soldItemsPage.getContent();
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
}
