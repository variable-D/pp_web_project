package com.pp_web_project.controller;

import com.pp_web_project.domain.SftpData;
import com.pp_web_project.service.sftp.impl.SalesPerformanceServiceImpl;
import com.pp_web_project.utill.ExcelExportUtil;
import com.pp_web_project.utill.ProductEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/salesPerformance")
public class SalesPerformanceController {

    private final SalesPerformanceServiceImpl salesPerformanceService;

    @GetMapping
    public String salesPerformance(
            @RequestParam(value = "store", required = false) String storeNumber,
            @RequestParam(value = "barcode", required = false) String barcode,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Model model) {

        List<SftpData> salesData = null;
        String title = "eSIM 실적 조회";

        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(1);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        if ((storeNumber != null && !storeNumber.isEmpty()) || (barcode != null && !barcode.isEmpty())) {
            if (storeNumber != null && !storeNumber.isEmpty()) {
                salesData = salesPerformanceService.getSalesByStoreNumber(storeNumber, startDate, endDate);
            }
            if (barcode != null && !barcode.isEmpty()) {
                salesData = salesPerformanceService.getSalesByBarcode(barcode, startDate, endDate);
            }
        } else {
            salesData = salesPerformanceService.getFindByTransactionDateBetween(startDate, endDate);
        }

        if (salesData == null) {
            salesData = List.of();
        }

        String logo = "eSIM";
        List<ProductEnum> productList = Arrays.asList(ProductEnum.values());

        BigDecimal totalAmount = salesData.stream()
                .map(sale -> new BigDecimal(sale.getTransactionAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("title", title);
        model.addAttribute("logo", logo);
        model.addAttribute("productList", productList);
        model.addAttribute("salesData", salesData);
        model.addAttribute("totalAmount", totalAmount);

        return "html/salesperformance/salesPerformance";
    }

    /**
     * ✅ 엑셀 다운로드 엔드포인트
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadSalesReport(
            @RequestParam(value = "store", required = false) String storeNumber,
            @RequestParam(value = "barcode", required = false) String barcode,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        try {
            List<SftpData> salesData = null;
            if (startDate == null) {
                startDate = LocalDateTime.now().minusDays(1);
            }
            if (endDate == null) {
                endDate = LocalDateTime.now();
            }

            if ((storeNumber != null && !storeNumber.isEmpty()) || (barcode != null && !barcode.isEmpty())) {
                if (storeNumber != null && !storeNumber.isEmpty()) {
                    salesData = salesPerformanceService.getSalesByStoreNumber(storeNumber, startDate, endDate);
                }
                if (barcode != null && !barcode.isEmpty()) {
                    salesData = salesPerformanceService.getSalesByBarcode(barcode, startDate, endDate);
                }
            } else {
                salesData = salesPerformanceService.getFindByTransactionDateBetween(startDate, endDate);
            }
            // ✅ 엑셀 데이터 생성
            byte[] excelData = ExcelExportUtil.exportSalesDataToExcel(salesData);

// ✅ 파일명에 날짜 추가 (프리피아_eSIM_실적_YYYYMMDD.xlsx)
            String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String fileName = "프리피아_eSIM_실적_" + timestamp + ".xlsx";

// ✅ 한글 파일명 깨짐 방지 (UTF-8 -> ISO-8859-1)
            String encodedFileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

// ✅ HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

// ✅ 파일 다운로드 응답 생성
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);

        } catch (IOException e) {
            log.error("🚨 Excel 파일 생성 중 오류 발생!", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
