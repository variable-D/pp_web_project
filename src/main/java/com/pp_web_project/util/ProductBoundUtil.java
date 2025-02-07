package com.pp_web_project.util;

import org.springframework.stereotype.Component;

@Component
public class ProductBoundUtil {
    public String getInboundOrOutbound(String product) {
        return ProductAndInBoundOrOutBoundEnum.getInboundOrOutbound(product);
    }
}
