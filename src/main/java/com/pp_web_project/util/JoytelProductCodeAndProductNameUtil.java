package com.pp_web_project.util;


import org.springframework.stereotype.Component;

@Component
public class JoytelProductCodeAndProductNameUtil {
    public String getJoytelPoructName(String product) {
        return JoytelProductCodeAndProductNameEum.getJoytelProductByName(product);
    }
}
