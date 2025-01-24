package com.pp_web_project.util;

import org.springframework.stereotype.Component;

@Component
public class ProductAmountUtil {
    public Integer getProductAmount(String productName) {
        return ProductNameAndAmountEnum.getProductNameByAmount(productName);
    }
}
