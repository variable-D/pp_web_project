package com.pp_web_project.util;


import org.springframework.stereotype.Component;

@Component
public class ProductNameUtil {
    public String getProductName(String product) {
        return ProductAndNameEnum.getProductByName(product);
    }
}
