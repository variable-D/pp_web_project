package com.pp_web_project.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductNameAndAmountEnum {
    JP_1GB_3D("eSIM-JP1G-03", 3800),
    JP_1GB_5D("eSIM-JP1G-05",5300),
    JP_1GB_7D("eSIM-JP1G-07", 6900),
    JP_1GB_10D("eSIM-JP1G-10", 10200),
    JP_MAX_3D("eSIM-JPMAX-03", 12000),
    JP_MAX_5D("eSIM-JPMAX-05", 17500),
    JP_MAX_7D("eSIM-JPMAX-07", 22500),
    JP_MAX_10D("eSIM-JPMAX-10", 30000),
    KR_MAX_3D("eSIM-KRMAX-03", 12000),
    KR_MAX_5D("eSIM-KRMAX-05", 17500),
    KR_MAX_7D("eSIM-KRMAX-07", 22500),
    KR_MAX_10D("eSIM-KRMAX-10", 30000),
    KR_MAX_15D("eSIM-KRMAX-15", 42000),
    KR_MAX_20D("eSIM-KRMAX-20", 53000),
    SKT_3D("SKT-eSIM-03D", 18000),
    SKT_5D("SKT-eSIM-05D", 27500),
    SKT_7D("SKT-eSIM-07D", 35000),
    SKT_10D("SKT-eSIM-10D", 38500),
    SKT_15D("SKT-eSIM-15D", 55000),
    SKT_20D("SKT-eSIM-20D", 60500),
    SKT_30D("SKT-eSIM-30D", 71500),
    SKT_60D("SKT-eSIM-60D", 107200),
    SKT_90D("SKT-eSIM-90D", 143000);

    private final String name;
    private final Integer price;

    // ✅ 상품 명으로 제품 찾기 (조회 메서드 추가)
    public static Integer getProductNameByAmount(String productName) {
        for (ProductNameAndAmountEnum product : values()) {
            if (product.getName().equals(productName)) {
                return product.getPrice();
            }
        }
        return 0; // 매칭되는 상품이 없을 경우, 원래 0L 값 반환
    }

}
