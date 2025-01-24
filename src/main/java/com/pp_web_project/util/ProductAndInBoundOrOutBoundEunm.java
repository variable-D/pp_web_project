package com.pp_web_project.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductAndInBoundOrOutBoundEunm {
    JP_1GB_3D("eSIM-JP1G-03", "아웃바운드"),
    JP_1GB_5D("eSIM-JP1G-05", "아웃바운드"),
    JP_1GB_7D("eSIM-JP1G-07", "아웃바운드"),
    JP_1GB_10D("eSIM-JP1G-10", "아웃바운드"),
    JP_MAX_3D("eSIM-JPMAX-03", "아웃바운드"),
    JP_MAX_5D("eSIM-JPMAX-05", "아웃바운드"),
    JP_MAX_7D("eSIM-JPMAX-07", "아웃바운드"),
    JP_MAX_10D("eSIM-JPMAX-10", "아웃바운드"),
    SKT_3D("SKT-eSIM-03D", "인바운드"),
    SKT_5D("SKT-eSIM-05D", "인바운드"),
    SKT_7D("SKT-eSIM-07D", "인바운드"),
    SKT_10D("SKT-eSIM-10D", "인바운드"),
    SKT_15D("SKT-eSIM-15D", "인바운드"),
    SKT_20D("SKT-eSIM-20D", "인바운드"),
    SKT_30D("SKT-eSIM-30D", "인바운드"),
    SKT_60D("SKT-eSIM-60D", "인바운드"),
    SKT_90D("SKT-eSIM-90D", "인바운드");

    private final String product;
    private final String AnyBound;

    // ✅ 바코드로 제품 찾기 (조회 메서드 추가)
    public static String getInboundOrOutbound(String product) {
        for (ProductAndInBoundOrOutBoundEunm products : values()) {
            if (products.getProduct().equals(product)) {
                return products.getAnyBound();
            }
        }
        return product; // 매칭되는 상품이 없을 경우, 원래 바코드 값 반환
    }
}
