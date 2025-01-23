package com.pp_web_project.controller;

import com.pp_web_project.domain.TcpResponseData;
import com.pp_web_project.service.tcp.impl.RetailServceImpl;
import com.pp_web_project.utill.ProductAndNameEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/retail")
public class RetailController {

    private final RetailServceImpl retailService;  // ✅ RetailServiceImpl 추가

    @GetMapping
    public String retail(
            @RequestParam(value = "store", required = false) String storeNumber,
            @RequestParam(value = "orderNumber", required = false) String orderNumber,
            @RequestParam(value = "product", required = false) String product,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Model model) {

        List<TcpResponseData> salesData = null;
        String title = "eSIM 판매 실적 조회";
        Map<String, String> categoryList = new HashMap<>();
        categoryList.put("정산 기준", "/salesPerformance");
        categoryList.put("판매 기준", "/retail");

        // ✅ 기본값 설정 (오늘 날짜)
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(1).atStartOfDay();
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        // ✅ 날짜를 문자열로 변환 (DB 검색을 위해)
        String saleDateAfter = startDate.toString();
        String saleDateBefore = endDate.toString();

        // ✅ 사용자 입력 값에 따라 데이터 조회
        if (storeNumber != null && !storeNumber.isEmpty()) {
            salesData = retailService.getStoreData(storeNumber, saleDateAfter, saleDateBefore);
        } else if (product != null && !product.isEmpty()) {
            salesData = retailService.getProductData(product, saleDateAfter, saleDateBefore);
        } else if (orderNumber != null && !orderNumber.isEmpty()) {
            TcpResponseData data = retailService.getManageNo(orderNumber);
            if (data != null) {
                salesData = Collections.singletonList(data);
            }
        } else {
            salesData = retailService.getAllData(saleDateAfter, saleDateBefore);
        }

        String logo = "eSIM";
        List<ProductAndNameEnum> productNameList = Arrays.asList(ProductAndNameEnum.values());

        model.addAttribute("categoryList", categoryList);
        model.addAttribute("title", title);
        model.addAttribute("logo", logo);
        model.addAttribute("productName", productNameList);
        model.addAttribute("salesData", salesData);

        return "html/retail/retail";
    }
}
