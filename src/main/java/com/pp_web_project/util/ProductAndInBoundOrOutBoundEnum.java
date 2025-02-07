package com.pp_web_project.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductAndInBoundOrOutBoundEnum {
    JP_1GB_3D("eSIM-JP1G-03", "OUT"),
    JP_1GB_5D("eSIM-JP1G-05", "OUT"),
    JP_1GB_7D("eSIM-JP1G-07", "OUT"),
    JP_1GB_10D("eSIM-JP1G-10", "OUT"),
    JP_MAX_3D("eSIM-JPMAX-03", "OUT"),
    JP_MAX_5D("eSIM-JPMAX-05", "OUT"),
    JP_MAX_7D("eSIM-JPMAX-07", "OUT"),
    JP_MAX_10D("eSIM-JPMAX-10", "OUT"),
    SKT_3D("SKT-eSIM-03D", "IN"),
    SKT_5D("SKT-eSIM-05D", "IN"),
    SKT_7D("SKT-eSIM-07D", "IN"),
    SKT_10D("SKT-eSIM-10D", "IN"),
    SKT_15D("SKT-eSIM-15D", "IN"),
    SKT_20D("SKT-eSIM-20D", "IN"),
    SKT_30D("SKT-eSIM-30D", "IN"),
    SKT_60D("SKT-eSIM-60D", "IN"),
    SKT_90D("SKT-eSIM-90D", "IN");

    private final String product;
    private final String AnyBound;

    // ✅ 바코드로 제품 찾기 (조회 메서드 추가)
    public static String getInboundOrOutbound(String product) {
        for (ProductAndInBoundOrOutBoundEnum products : values()) {
            if (products.getProduct().equals(product)) {
                return products.getAnyBound();
            }
        }
        return product; // 매칭되는 상품이 없을 경우, 원래 바코드 값 반환
    }
}
