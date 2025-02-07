package com.pp_web_project.util;

import org.springframework.stereotype.Component;

@Component
public class ProductAndInBoundOrOutBoundEnumUtil {

    public String getProductAndInBoundOrOutBound(String productName) {
        return ProductAndInBoundOrOutBoundEnum.getInboundOrOutbound(productName);
    }
}
