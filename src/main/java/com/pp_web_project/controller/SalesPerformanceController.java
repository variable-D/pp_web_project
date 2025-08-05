package com.pp_web_project.controller;

import com.pp_web_project.domain.SftpData;
import com.pp_web_project.service.sftp.impl.SalesPerformanceServiceImpl;
import com.pp_web_project.util.ExcelExportUtil;
import com.pp_web_project.util.ProductEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
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
import java.util.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class SalesPerformanceController {

    private final SalesPerformanceServiceImpl salesPerformanceService;
    private final MessageSource messageSource;

    @GetMapping("/admin/sales/performance")
    public String adminSalesPerformance(
            @RequestParam(value = "store", required = false) String storeNumber,
            @RequestParam(value = "barcode", required = false) String barcode,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "transactionType", required = false) String transactionType,
            Locale locale,
            Model model) {




        String title = "eSIM 정산 실적 조회";
        Map<String, String> categoryList = new LinkedHashMap<>();
        categoryList.put("정산 기준", "/admin/sales/performance");
        categoryList.put("판매 기준", "/admin/sales/retail");



        // 1) 날짜 기본값 처리
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        // LocalDate를 LocalDateTime으로 변환하여 시간 설정
        LocalDateTime startDateTime = startDate.atStartOfDay(); // 00:00:00
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59); // 23:59:59

        // 2) 조건 체크를 위한 boolean 변수
        boolean hasStore = storeNumber != null && !storeNumber.isEmpty();
        boolean hasBarcode = barcode != null && !barcode.isEmpty();
        boolean hasTransactionType = transactionType != null && !transactionType.isEmpty();

        log.info("🕒 [조회 시작] 엑셀: {}, barcode: {}, startDate: {}, endDate: {}", storeNumber, barcode, startDate, endDate);

        // 3) DB 조회
        List<SftpData> salesData;
        if (hasStore && hasBarcode) {
            // ✅ 점포번호 + 바코드 조회
            salesData = salesPerformanceService.getSalesByStoreNumberAndBarcode(storeNumber, barcode, startDateTime, endDateTime);
        } else if (hasStore) {
            // ✅ 점포번호만
            salesData = salesPerformanceService.getSalesByStoreNumber(storeNumber, startDateTime, endDateTime);
        } else if (hasBarcode) {
            // ✅ 바코드만
            salesData = salesPerformanceService.getSalesByBarcode(barcode, startDateTime, endDateTime);
        }else if (hasTransactionType) {
            // ✅ 거래 유형만
            salesData = salesPerformanceService.getSalesByTransactionType(transactionType, startDateTime, endDateTime);
        } else {
            // ✅ 둘 다 없으면 전체 조회
            salesData = salesPerformanceService.getFindByTransactionDateBetween(startDateTime, endDateTime);
        }


        if (salesData == null) {
            salesData = List.of();
        }

        String logo = messageSource.getMessage("messages.logo", null, locale);
        List<ProductEnum> productList = Arrays.asList(ProductEnum.values());

        BigDecimal totalAmount = salesData.stream()
                .map(sale -> new BigDecimal(sale.getTransactionAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long inboundCount = salesData.stream()
                .filter(data -> "02".equals(data.getTransactionType()))
                .count();

        long outboundCount = salesData.stream()
                .filter(data -> "01".equals(data.getTransactionType()))
                .count();

        model.addAttribute("inboundCount", inboundCount);
        model.addAttribute("outboundCount", outboundCount);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("title", title);
        model.addAttribute("logo", logo);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("storeNumber", storeNumber);
        model.addAttribute("barcode", barcode);
        model.addAttribute("productList", productList);
        model.addAttribute("salesData", salesData);
        model.addAttribute("totalAmount", totalAmount);

        return "admin/salesperformance/salesPerformance";
    }


    @GetMapping("/admin/sales/performance/download")
    public ResponseEntity<byte[]> adminDownloadSalesReport(
            @RequestParam(value = "store", required = false) String storeNumber,
            @RequestParam(value = "barcode", required = false) String barcode,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "transactionType", required = false) String transactionType) {


        try {

            // 1) 날짜 기본값 처리
            if (startDate == null) {
                startDate = LocalDate.now().minusDays(1);
            }
            if (endDate == null) {
                endDate = LocalDate.now();
            }

            // LocalDate를 LocalDateTime으로 변환하여 시간 설정
            LocalDateTime startDateTime = startDate.atStartOfDay(); // 00:00:00
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59); // 23:59:59

            // 2) 조건 체크를 위한 boolean 변수
            boolean hasStore = storeNumber != null && !storeNumber.isEmpty();
            boolean hasBarcode = barcode != null && !barcode.isEmpty();
            boolean hasTransactionType = transactionType != null && !transactionType.isEmpty();


            // 3) DB 조회
            List<SftpData> salesData;
            if (hasStore && hasBarcode) {
                // ✅ 점포번호 + 바코드 조회
                salesData = salesPerformanceService.getSalesByStoreNumberAndBarcode(storeNumber, barcode, startDateTime, endDateTime);
            } else if (hasStore) {
                // ✅ 점포번호만
                salesData = salesPerformanceService.getSalesByStoreNumber(storeNumber, startDateTime, endDateTime);
            } else if (hasBarcode) {
                // ✅ 바코드만
                salesData = salesPerformanceService.getSalesByBarcode(barcode, startDateTime, endDateTime);
            }else if (hasTransactionType) {
                // ✅ 거래 유형만
                salesData = salesPerformanceService.getSalesByTransactionType(transactionType, startDateTime, endDateTime);
            } else {
                // ✅ 둘 다 없으면 전체 조회
                salesData = salesPerformanceService.getFindByTransactionDateBetween(startDateTime, endDateTime);
            }


            if (salesData == null) {
                salesData = List.of();
            }

            // 4) 엑셀 변환
            byte[] excelData = ExcelExportUtil.exportSalesDataToExcel(salesData);

            // 5) 파일명 생성 및 헤더 설정
            String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String fileName = "프리피아_eSIM_정산실적_" + timestamp + ".xlsx";
            String encodedFileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);

        } catch (IOException e) {
            log.error("🚨 [오류] Excel 파일 생성 중 오류 발생!", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }



    @GetMapping("/user/sales/performance")
    public String userSalesPerformance(
            @RequestParam(value = "store", required = false) String storeNumber,
            @RequestParam(value = "barcode", required = false) String barcode,

            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "transactionType", required = false) String transactionType,
            Locale locale,
            Model model) {




        String title = "eSIM 정산 실적 조회";
        Map<String, String> categoryList = new LinkedHashMap<>();
        categoryList.put("정산 기준", "/user/sales/performance");
        categoryList.put("판매 기준", "/user/sales/retail");



        // 1) 날짜 기본값 처리
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        // LocalDate를 LocalDateTime으로 변환하여 시간 설정
        LocalDateTime startDateTime = startDate.atStartOfDay(); // 00:00:00
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59); // 23:59:59

        // 2) 조건 체크를 위한 boolean 변수
        boolean hasStore = storeNumber != null && !storeNumber.isEmpty();
        boolean hasBarcode = barcode != null && !barcode.isEmpty();
        boolean hasTransactionType = transactionType != null && !transactionType.isEmpty();

        log.info("🕒 [조회 시작] 엑셀: {}, barcode: {}, startDate: {}, endDate: {}", storeNumber, barcode, startDate, endDate);

        List<SftpData> salesData;
        if (hasStore && hasBarcode) {
            // ✅ 점포번호 + 바코드 조회
            salesData = salesPerformanceService.getSalesByStoreNumberAndBarcode(storeNumber, barcode, startDateTime, endDateTime);
        } else if (hasStore) {
            // ✅ 점포번호만
            salesData = salesPerformanceService.getSalesByStoreNumber(storeNumber, startDateTime, endDateTime);
        } else if (hasBarcode) {
            // ✅ 바코드만
            salesData = salesPerformanceService.getSalesByBarcode(barcode, startDateTime, endDateTime);
        }else if (hasTransactionType) {
            // ✅ 거래 유형만
            salesData = salesPerformanceService.getSalesByTransactionType(transactionType, startDateTime, endDateTime);
        } else {
            // ✅ 둘 다 없으면 전체 조회
            salesData = salesPerformanceService.getFindByTransactionDateBetween(startDateTime, endDateTime);
        }


        if (salesData == null) {
            salesData = List.of();
        }

        String logo = messageSource.getMessage("messages.logo", null, locale);
        List<ProductEnum> productList = Arrays.asList(ProductEnum.values());

        BigDecimal totalAmount = salesData.stream()
                .map(sale -> new BigDecimal(sale.getTransactionAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long inboundCount = salesData.stream()
                .filter(data -> "02".equals(data.getTransactionType()))
                .count();

        long outboundCount = salesData.stream()
                .filter(data -> "01".equals(data.getTransactionType()))
                .count();

        model.addAttribute("inboundCount", inboundCount);
        model.addAttribute("outboundCount", outboundCount);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("title", title);
        model.addAttribute("logo", logo);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("storeNumber", storeNumber);
        model.addAttribute("barcode", barcode);
        model.addAttribute("productList", productList);
        model.addAttribute("salesData", salesData);
        model.addAttribute("totalAmount", totalAmount);

        return "user/salesperformance/salesPerformance";
    }

    /**
     * ✅ 엑셀 다운로드 엔드포인트
     */
    @GetMapping("/user/sales/performance/download")
    public ResponseEntity<byte[]> userDownloadSalesReport(
            @RequestParam(value = "store", required = false) String storeNumber,
            @RequestParam(value = "barcode", required = false) String barcode,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "transactionType", required = false) String transactionType) {


        try {

            // 1) 날짜 기본값 처리
            if (startDate == null) {
                startDate = LocalDate.now().minusDays(1);
            }
            if (endDate == null) {
                endDate = LocalDate.now();
            }

            // LocalDate를 LocalDateTime으로 변환하여 시간 설정
            LocalDateTime startDateTime = startDate.atStartOfDay(); // 00:00:00
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59); // 23:59:59

            // 2) 조건 체크를 위한 boolean 변수
            boolean hasStore = storeNumber != null && !storeNumber.isEmpty();
            boolean hasBarcode = barcode != null && !barcode.isEmpty();
            boolean hasTransactionType = transactionType != null && !transactionType.isEmpty();

            // 3) DB 조회
            List<SftpData> salesData;
            if (hasStore && hasBarcode) {
                // ✅ 점포번호 + 바코드 조회
                salesData = salesPerformanceService.getSalesByStoreNumberAndBarcode(storeNumber, barcode, startDateTime, endDateTime);
            } else if (hasStore) {
                // ✅ 점포번호만
                salesData = salesPerformanceService.getSalesByStoreNumber(storeNumber, startDateTime, endDateTime);
            } else if (hasBarcode) {
                // ✅ 바코드만
                salesData = salesPerformanceService.getSalesByBarcode(barcode, startDateTime, endDateTime);
            }else if (hasTransactionType) {
                // ✅ 거래 유형만
                salesData = salesPerformanceService.getSalesByTransactionType(transactionType, startDateTime, endDateTime);
            } else {
                // ✅ 둘 다 없으면 전체 조회
                salesData = salesPerformanceService.getFindByTransactionDateBetween(startDateTime, endDateTime);
            }


            if (salesData == null) {
                salesData = List.of();
            }

            // 4) 엑셀 변환
            byte[] excelData = ExcelExportUtil.exportSalesDataToExcel(salesData);

            // 5) 파일명 생성 및 헤더 설정
            String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String fileName = "프리피아_eSIM_정산실적_" + timestamp + ".xlsx";
            String encodedFileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);

        } catch (IOException e) {
            log.error("🚨 [오류] Excel 파일 생성 중 오류 발생!", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }


}
