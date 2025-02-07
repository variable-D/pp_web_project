package com.pp_web_project.controller;

import com.pp_web_project.domain.TcpResponseData;
import com.pp_web_project.service.tcp.impl.RetailServceImpl;
import com.pp_web_project.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
@Controller
public class RetailController {

    private final RetailServceImpl retailService;
    private final ProductBoundUtil productBoundUtil;
    private final ProductNameUtil productNameUtil;
    private final ProductAmountUtil productAmountUtil;
    private final ProductPlusSpaceUtil productPlusSpaceUtil;
    private final ProductAndInBoundOrOutBoundEnumUtil productAndInBoundOrOutBoundEnumUtil;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @GetMapping("/admin/sales/retail")
    public String AdminRetail(
            @RequestParam(value = "store", required = false) String storeNumber,
            @RequestParam(value = "orderNumber", required = false) String orderNumber,
            @RequestParam(value = "product", required = false) String product,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            Model model) {


        List<TcpResponseData> salesData = null;
        String title = "eSIM 판매 실적 조회";
        Map<String, String> categoryList = new LinkedHashMap<>();
        categoryList.put("판매 기준", "/admin/sales/retail");
        categoryList.put("정산 기준", "/admin/sales/performance");


        // ✅ 기본값: 오늘 00:00 ~ 23:59 조회
        if (startDate == null) {
            startDate = LocalDate.now(); // 기본: 오늘 날짜
        }
        if (endDate == null) {
            endDate = LocalDate.now(); // 기본: 오늘 날짜
        }

// ✅ 00:00:00 ~ 23:59:59로 조회되도록 변환
        LocalDateTime startDateTime = startDate.atStartOfDay(); // 00:00:00
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59); // 23:59:59

        String reformOrderNum = (orderNumber != null) ? orderNumber + "  " : null;
        String reformProduct = productPlusSpaceUtil.getProductPlusSpace(product);

        // ✅ yyyyMMdd 형식으로 변환
        String saleDateAfter = startDateTime.format(DATE_FORMATTER);
        String saleDateBefore = endDateTime.format(DATE_FORMATTER);
        // ✅ 사용자 입력 값에 따라 데이터 조회
        if (storeNumber != null && !storeNumber.isEmpty() && product != null && !product.isEmpty()) {
            // 점포번호 + 상품 조회
            salesData = retailService.getStoreProductData(storeNumber, reformProduct, saleDateAfter, saleDateBefore);
        } else if (storeNumber != null && !storeNumber.isEmpty()) {
            // 점포번호만 조회
            salesData = retailService.getStoreData(storeNumber, saleDateAfter, saleDateBefore);
        } else if (product != null && !product.isEmpty()) {
            // 상품명만 조회
            salesData = retailService.getProductData(reformProduct, saleDateAfter, saleDateBefore);
        } else if (orderNumber != null && !orderNumber.isEmpty()) {
            // 주문번호 조회
            salesData = retailService.getManageNo(reformOrderNum);
        } else {
            // 기본 조회 (날짜 기준)
            salesData = retailService.getAllData(saleDateAfter, saleDateBefore);
        }

        Integer totalAmount = 0;
        if (salesData != null) {
            totalAmount = salesData.stream()
                    .mapToInt(data -> productAmountUtil.getProductAmount(data.getPlunm().trim())) // ✅ 상품 가격 조회
                    .sum();
        }

        int inboundCount = 0;
        int outboundCount = 0;

        if (salesData != null) {
            for (TcpResponseData data : salesData) {
                String productCode = data.getPlunm().trim();
                String boundType = ProductAndInBoundOrOutBoundEnum.getInboundOrOutbound(productCode);
                if ("IN".equals(boundType)) {
                    inboundCount++;
                } else if ("OUT".equals(boundType)) {
                    outboundCount++;
                }
            }
        }

        String logo = "eSIM";
        List<ProductAndNameEnum> productNameList = Arrays.asList(ProductAndNameEnum.values());

        model.addAttribute("categoryList", categoryList);
        model.addAttribute("title", title);
        model.addAttribute("logo", logo);
        model.addAttribute("productName", productNameList);
        model.addAttribute("salesData", salesData);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("orderNumber", orderNumber);
        model.addAttribute("product", product);
        model.addAttribute("storeNumber", storeNumber);
        model.addAttribute("productInBoundOrOutBound",productBoundUtil);
        model.addAttribute("productNameUtil",productNameUtil);
        model.addAttribute("productAmountUtil",productAmountUtil);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("inboundCount", inboundCount);
        model.addAttribute("outboundCount", outboundCount);



        return "admin/retail/retail";
    }

    @GetMapping("/admin/sales/retail/download")
    public ResponseEntity<byte[]> adminDownloadExcel(
            @RequestParam(value = "store", required = false) String storeNumber,
            @RequestParam(value = "orderNumber", required = false) String orderNumber,
            @RequestParam(value = "product", required = false) String product,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate) {

        try {
            // 기본값 설정 (오늘 날짜)
            if (startDate == null) {
                startDate = LocalDate.now();
            }
            if (endDate == null) {
                endDate = LocalDate.now();
            }

            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

            String reformProduct = productPlusSpaceUtil.getProductPlusSpace(product);
            String reformOrderNum = (orderNumber != null) ? orderNumber + "  " : null;

            String saleDateAfter = startDateTime.format(DATE_FORMATTER);
            String saleDateBefore = endDateTime.format(DATE_FORMATTER);

            log.info("Sale Date After: {}, Sale Date Before: {}", saleDateAfter, saleDateBefore);


            List<TcpResponseData> salesData = null;

            // ✅ 사용자 입력 값에 따라 데이터 조회
            if (storeNumber != null && !storeNumber.isEmpty() && product != null && !product.isEmpty()) {
                // 점포번호 + 상품 조회
                salesData = retailService.getStoreProductData(storeNumber, reformProduct, saleDateAfter, saleDateBefore);
            } else if (storeNumber != null && !storeNumber.isEmpty()) {
                // 점포번호만 조회
                salesData = retailService.getStoreData(storeNumber, saleDateAfter, saleDateBefore);
            } else if (product != null && !product.isEmpty()) {
                // 상품명만 조회
                salesData = retailService.getProductData(reformProduct, saleDateAfter, saleDateBefore);
            } else if (orderNumber != null && !orderNumber.isEmpty()) {
                // 주문번호 조회
                salesData = retailService.getManageNo(reformOrderNum);

            } else {
                // 기본 조회 (날짜 기준)
                salesData = retailService.getAllData(saleDateAfter, saleDateBefore);
            }
            // Excel 데이터 생성
            byte[] excelData = ExcelExportUtil.exportRetailSalesDataToExcel(salesData, productBoundUtil, productNameUtil, productAmountUtil);

            // 파일명 설정 (YYYYMMDD 형식)
            String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String fileName = "프리피아_eSIM_판매실적_" + timestamp + ".xlsx";

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

    @GetMapping("/user/sales/retail")
    public String UserRetail(
            @RequestParam(value = "store", required = false) String storeNumber,
            @RequestParam(value = "orderNumber", required = false) String orderNumber,
            @RequestParam(value = "product", required = false) String product,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            Model model) {


        List<TcpResponseData> salesData = null;
        String title = "eSIM 판매 실적 조회";
        Map<String, String> categoryList = new LinkedHashMap<>();
        categoryList.put("판매 기준", "/user/sales/retail");
        categoryList.put("정산 기준", "/user/sales/performance");

        // ✅ 기본값: 오늘 00:00 ~ 23:59 조회
        if (startDate == null) {
            startDate = LocalDate.now(); // 기본: 오늘 날짜
        }
        if (endDate == null) {
            endDate = LocalDate.now(); // 기본: 오늘 날짜
        }

// ✅ 00:00:00 ~ 23:59:59로 조회되도록 변환
        LocalDateTime startDateTime = startDate.atStartOfDay(); // 00:00:00
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59); // 23:59:59

        String reformOrderNum = (orderNumber != null) ? orderNumber + "  " : null;
        String reformProduct = productPlusSpaceUtil.getProductPlusSpace(product);

        // ✅ yyyyMMdd 형식으로 변환
        String saleDateAfter = startDateTime.format(DATE_FORMATTER);
        String saleDateBefore = endDateTime.format(DATE_FORMATTER);
        // ✅ 사용자 입력 값에 따라 데이터 조회
        if (storeNumber != null && !storeNumber.isEmpty() && product != null && !product.isEmpty()) {
            // 점포번호 + 상품 조회
            salesData = retailService.getStoreProductData(storeNumber, reformProduct, saleDateAfter, saleDateBefore);
        } else if (storeNumber != null && !storeNumber.isEmpty()) {
            // 점포번호만 조회
            salesData = retailService.getStoreData(storeNumber, saleDateAfter, saleDateBefore);
        } else if (product != null && !product.isEmpty()) {
            // 상품명만 조회
            salesData = retailService.getProductData(reformProduct, saleDateAfter, saleDateBefore);
        } else if (orderNumber != null && !orderNumber.isEmpty()) {
            // 주문번호 조회
            salesData = retailService.getManageNo(reformOrderNum);
        } else {
            // 기본 조회 (날짜 기준)
            salesData = retailService.getAllData(saleDateAfter, saleDateBefore);
        }

        Integer totalAmount = 0;
        if (salesData != null) {
            totalAmount = salesData.stream()
                    .mapToInt(data -> productAmountUtil.getProductAmount(data.getPlunm().trim())) // ✅ 상품 가격 조회
                    .sum();
        }

        int inboundCount = 0;
        int outboundCount = 0;

        if (salesData != null) {
            for (TcpResponseData data : salesData) {
                String productCode = data.getPlunm().trim();
                String boundType = ProductAndInBoundOrOutBoundEnum.getInboundOrOutbound(productCode);
                if ("IN".equals(boundType)) {
                    inboundCount++;
                } else if ("OUT".equals(boundType)) {
                    outboundCount++;
                }
            }
        }



        String logo = "eSIM";
        List<ProductAndNameEnum> productNameList = Arrays.asList(ProductAndNameEnum.values());

        model.addAttribute("categoryList", categoryList);
        model.addAttribute("title", title);
        model.addAttribute("logo", logo);
        model.addAttribute("productName", productNameList);
        model.addAttribute("salesData", salesData);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("orderNumber", orderNumber);
        model.addAttribute("product", product);
        model.addAttribute("storeNumber", storeNumber);
        model.addAttribute("productInBoundOrOutBound",productBoundUtil);
        model.addAttribute("productNameUtil",productNameUtil);
        model.addAttribute("productAmountUtil",productAmountUtil);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("inboundCount", inboundCount);
        model.addAttribute("outboundCount", outboundCount);


        return "user/retail/retail";
    }

    @GetMapping("/user/sales/retail/download")
    public ResponseEntity<byte[]> userDownloadExcel(
            @RequestParam(value = "store", required = false) String storeNumber,
            @RequestParam(value = "orderNumber", required = false) String orderNumber,
            @RequestParam(value = "product", required = false) String product,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate) {

        try {
            // ✅ 기본값 설정 (오늘 날짜)
            if (startDate == null) {
                startDate = LocalDate.now();
            }
            if (endDate == null) {
                endDate = LocalDate.now();
            }

            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

            String reformProduct = productPlusSpaceUtil.getProductPlusSpace(product);
            String reformOrderNum = (orderNumber != null) ? orderNumber + "  " : null;

            String saleDateAfter = startDateTime.format(DATE_FORMATTER);
            String saleDateBefore = endDateTime.format(DATE_FORMATTER);

            List<TcpResponseData> salesData;

            // ✅ 데이터 조회
            if (storeNumber != null && !storeNumber.isEmpty() && product != null && !product.isEmpty()) {
                salesData = retailService.getStoreProductData(storeNumber, reformProduct, saleDateAfter, saleDateBefore);
            } else if (storeNumber != null && !storeNumber.isEmpty()) {
                salesData = retailService.getStoreData(storeNumber, saleDateAfter, saleDateBefore);
            } else if (product != null && !product.isEmpty()) {
                salesData = retailService.getProductData(reformProduct, saleDateAfter, saleDateBefore);
            } else if (orderNumber != null && !orderNumber.isEmpty()) {
                salesData = retailService.getManageNo(reformOrderNum);

            } else {
                salesData = retailService.getAllData(saleDateAfter, saleDateBefore);
            }

            // ✅ Excel 데이터 생성
            byte[] excelData = ExcelExportUtil.exportRetailSalesDataToExcel(salesData, productBoundUtil, productNameUtil, productAmountUtil);

            // ✅ 파일명 설정 (YYYYMMDD 형식)
            String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String fileName = "프리피아_eSIM_판매실적_" + timestamp + ".xlsx";

            // ✅ 한글 파일명 깨짐 방지 (브라우저별 처리)
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            log.error("🚨 알 수 없는 오류 발생!", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}

