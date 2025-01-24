package com.pp_web_project.util;

import org.springframework.stereotype.Component;

@Component
public class ProductPlusSpaceUtil {
    public String getProductPlusSpace(String product) {
        return ProductPlusSpace.getProductPlusSpaceByName(product);
    }
}
