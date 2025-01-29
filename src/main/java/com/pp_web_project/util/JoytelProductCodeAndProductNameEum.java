package com.pp_web_project.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JoytelProductCodeAndProductNameEum {
    JP_1GB_3D("eSIM-JP1G-03", "일본 1GB 3일"),
    JP_1GB_5D("eSIM-JP1G-05", "일본 1GB 5일"),
    JP_1GB_7D("eSIM-JP1G-07", "일본 1GB 7일"),
    JP_1GB_10D("eSIM-JP1G-10", "일본 1GB 10일"),
    JP_MAX_3D("eSIM-JPMAX-03", "일본 MAX 3일"),
    JP_MAX_5D("eSIM-JPMAX-05", "일본 MAX 5일"),
    JP_MAX_7D("eSIM-JPMAX-07", "일본 MAX 7일"),
    JP_MAX_10D("eSIM-JPMAX-10", "일본 MAX 10일");

    private final String product;
    private final String productName;

    // ✅ 바코드로 제품 찾기 (조회 메서드 추가)
    public static String getJoytelProductByName(String product) {
        for (JoytelProductCodeAndProductNameEum products : values()) {
            if (products.getProduct().equals(product)) {
                return products.getProductName();
            }
        }
        return product; // 매칭되는 상품이 없을 경우, 원래 바코드 값 반환
    }
}
