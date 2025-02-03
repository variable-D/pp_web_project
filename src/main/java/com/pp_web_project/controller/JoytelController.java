package com.pp_web_project.controller;

import com.pp_web_project.domain.JoytelProduct;
import com.pp_web_project.service.joytel.interfaces.JoytelProductsService;
import com.pp_web_project.util.ExcelExportUtil;
import com.pp_web_project.util.JoytelProductCodeAndProductNameEum;
import com.pp_web_project.util.JoytelProductCodeAndProductNameUtil;
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
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/joytel/products")
public class JoytelController {

    private final JoytelProductsService joytelProductsService;
    private final JoytelProductCodeAndProductNameUtil joytelProductCodeAndProductNameUtil;


    @GetMapping("/inventory")
    public String inventory(
            @RequestParam(value = "product", required = false) String products,
            Model model
    ) {

        String logo = "eSIM";

        String title = "JOYTEL 재고 현황";
        Map<String, String> categoryList = new LinkedHashMap<>();
        categoryList.put("재고 현황", "/admin/joytel/products/inventory");
        categoryList.put("판매 완료 현황", "/admin/joytel/products/sold");
        categoryList.put("환불 상품 현황", "/admin/joytel/products/expiring");
        List<JoytelProduct> inventory = null;


        if (products != null && !products.isEmpty()) {
            inventory = joytelProductsService.getProducts(products);
        } else {
            inventory = joytelProductsService.getInventory();
        }


        List<JoytelProductCodeAndProductNameEum> joytelProductNameList = Arrays.asList(JoytelProductCodeAndProductNameEum.values());


        model.addAttribute("logo", logo);
        model.addAttribute("joytelProductAndNameList", joytelProductCodeAndProductNameUtil);
        model.addAttribute("joytelProductNameList", joytelProductNameList);
        model.addAttribute("products", products);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("title", title);
        model.addAttribute("inventory", inventory);
        return "admin/joytel/inventory";
    }

    @GetMapping("/inventory/download")
    public ResponseEntity<byte[]> adminDownloadExcel(
            @RequestParam(value = "product", required = false) String products) {

        try {
            log.info("products: {}", products);

            List<JoytelProduct> inventory = null;
            if (products != null && !products.isEmpty()) {
                inventory = joytelProductsService.getProducts(products);
            } else {
                inventory = joytelProductsService.getInventory();
            }


            // Excel 데이터 생성
            byte[] excelData = ExcelExportUtil.exportJoytelInventoryListExcel(inventory, joytelProductCodeAndProductNameUtil);

            // 파일명 설정 (YYYYMMDD 형식)
            String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String fileName = "JOYTEL_재고현황_" + timestamp + ".xlsx";

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

    @GetMapping("/sold")
    public String sold(
            @RequestParam(value = "product", required = false) String products,
            @RequestParam(value = "orderNumber", required = false) String orderNumber,
            Model model
    ) {
        String logo = "eSIM";

        String title = "JOYTEL 판매 완료된 상품";
        Map<String, String> categoryList = new LinkedHashMap<>();
        categoryList.put("판매 완료 현황", "/admin/joytel/products/sold");
        categoryList.put("재고 현황", "/admin/joytel/products/inventory");
        categoryList.put("환불 상품 현황", "/admin/joytel/products/expiring");
        List<JoytelProduct> soldItems = null;

        Page<JoytelProduct> item = null;
        Pageable pageable = PageRequest.of(0, 50, Sort.by("id").descending());

        if (products != null && !products.isEmpty() && orderNumber != null && !orderNumber.isEmpty()) {
            soldItems = joytelProductsService.getSoldAndProductCodeAndOrderNum(products, orderNumber);
        } else if (orderNumber != null && !orderNumber.isEmpty()) {
            soldItems = joytelProductsService.getSoldAndOrderNum(orderNumber);
        } else if (products != null && !products.isEmpty()) {
            item = joytelProductsService.getSoldAndProductCode(pageable, products);
            soldItems = item.getContent();
        } else {
            item = joytelProductsService.getSoldProductsPage(pageable);
            soldItems = item.getContent();
        }


        List<JoytelProductCodeAndProductNameEum> joytelProductNameList = Arrays.asList(JoytelProductCodeAndProductNameEum.values());


        model.addAttribute("logo", logo);
        model.addAttribute("joytelProductAndNameList", joytelProductCodeAndProductNameUtil);
        model.addAttribute("soldItems", soldItems);
        model.addAttribute("joytelProductNameList", joytelProductNameList);
        model.addAttribute("orderNumber", orderNumber);
        model.addAttribute("products", products);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("title", title);
        return "admin/joytel/sold";
    }

    @GetMapping("/sold/download")
    public ResponseEntity<byte[]> adminDownloadSoldItemExcel(
            @RequestParam(value = "product", required = false) String products
            , @RequestParam(value = "orderNumber", required = false) String orderNumber) {

        try {
            log.info("products: {}", products);

            List<JoytelProduct> soldItems = null;
            Page<JoytelProduct> item = null;


            Pageable pageable = PageRequest.of(0, 50, Sort.by("id").descending());

            if (products != null && !products.isEmpty() && orderNumber != null && !orderNumber.isEmpty()) {
                soldItems = joytelProductsService.getSoldAndProductCodeAndOrderNum(products, orderNumber);
            } else if (orderNumber != null && !orderNumber.isEmpty()) {
                soldItems = joytelProductsService.getSoldAndOrderNum(orderNumber);
            } else if (products != null && !products.isEmpty()) {
                item = joytelProductsService.getSoldAndProductCode(pageable, products);
                soldItems = item.getContent();
            } else {
                item = joytelProductsService.getSoldProductsPage(pageable);
                soldItems = item.getContent();
            }

            // Excel 데이터 생성
            byte[] excelData = ExcelExportUtil.exportJoytelSoldItemListExcel(soldItems, joytelProductCodeAndProductNameUtil);

            // 파일명 설정 (YYYYMMDD 형식)
            String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String fileName = "JOYTEL_판매완료_상품_" + timestamp + ".xlsx";

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

    @GetMapping("/expiring")
    public String expiring(
            @RequestParam(value = "product", required = false) String products,
            Model model
    ) {

        String logo = "eSIM";

        String title = "JOYTEL 환불 상품";
        Map<String, String> categoryList = new LinkedHashMap<>();
        categoryList.put("환불 상품 현황", "/admin/joytel/products/expiring");
        categoryList.put("재고 현황", "/admin/joytel/products/inventory");
        categoryList.put("판매 완료 현황", "/admin/joytel/products/sold");
        List<JoytelProduct> refundItems = null;


        if (products != null && !products.isEmpty()) {
            refundItems = joytelProductsService.getExpiredUnsoldProducts(products);
        } else {
            refundItems = joytelProductsService.getAllExpiredUnsoldProducts();
        }


        List<JoytelProductCodeAndProductNameEum> joytelProductNameList = Arrays.asList(JoytelProductCodeAndProductNameEum.values());


        model.addAttribute("logo", logo);
        model.addAttribute("joytelProductAndNameList", joytelProductCodeAndProductNameUtil);
        model.addAttribute("joytelProductNameList", joytelProductNameList);
        model.addAttribute("products", products);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("title", title);
        model.addAttribute("refundItems", refundItems);
        return "admin/joytel/expiring";
    }

    @PatchMapping("/expiring/update")
    public ResponseEntity<Map<String, Object>> updateRefundStatus(@RequestBody List<Long> ids) {
        Map<String, Object> response = new HashMap<>();

        if (ids == null || ids.isEmpty()) {
            response.put("status", "error");
            response.put("message", "✅ 업데이트할 항목을 선택하세요!");
            return ResponseEntity.badRequest().body(response); // 400 Bad Request
        }

        int updatedCount = joytelProductsService.updateRefundStatusByIds(ids);

        if (updatedCount > 0) {
            response.put("status", "success");
            response.put("message", "✅ " + updatedCount + "개 항목이 성공적으로 업데이트되었습니다!");
            response.put("updatedCount", updatedCount);
            return ResponseEntity.ok(response); // 200 OK
        } else {
            response.put("status", "error");
            response.put("message", "🚨 해당 ID를 찾을 수 없습니다!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 404 Not Found
        }
    }


    @GetMapping("/expiring/download")
    public ResponseEntity<byte[]> adminDownloadRefundItemsExcel(
            @RequestParam(value = "product", required = false) String products) {

        try {
            log.info("products: {}", products);

            List<JoytelProduct> refundItems = null;


            if (products != null && !products.isEmpty()) {
                refundItems = joytelProductsService.getExpiredUnsoldProducts(products);
            } else {
                refundItems = joytelProductsService.getAllExpiredUnsoldProducts();
            }

            // Excel 데이터 생성
            byte[] excelData = ExcelExportUtil.exportJoytelRefundItemListExcel(refundItems, joytelProductCodeAndProductNameUtil);

            // 파일명 설정 (YYYYMMDD 형식)
            String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String fileName = "JOYTEL_환불 상품_" + timestamp + ".xlsx";

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
