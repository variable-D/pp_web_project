package com.pp_web_project.utill;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductNameAndPriceEnum {
    JP_1GB_3D("eSIM-JP1G-03", 3800L),
    JP_1GB_5D("eSIM-JP1G-05",5300L),
    JP_1GB_7D("eSIM-JP1G-07", 6900L),
    JP_1GB_10D("eSIM-JP1G-10", 10200L),
    JP_MAX_3D("eSIM-JPMAX-03", 12000L),
    JP_MAX_5D("eSIM-JPMAX-05", 17000L),
    JP_MAX_7D("eSIM-JPMAX-07", 22500L),
    JP_MAX_10D("eSIM-JPMAX-10", 30000L),
    SKT_3D("SKT-eSIM-03D", 18000L),
    SKT_5D("SKT-eSIM-05D", 27500L),
    SKT_7D("SKT-eSIM-07D", 35000L),
    SKT_10D("SKT-eSIM-10D", 38500L),
    SKT_15D("SKT-eSIM-15D", 55000L),
    SKT_20D("SKT-eSIM-20D", 60500L),
    SKT_30D("SKT-eSIM-30D", 71500L),
    SKT_60D("SKT-eSIM-60D", 107200L),
    SKT_90D("SKT-eSIM-90D", 143000L);

    private final String name;
    private final Long price;

    // ✅ 상품 명으로 제품 찾기 (조회 메서드 추가)
    public static Long getProductNameByPrice(String productName) {
        for (ProductNameAndPriceEnum product : values()) {
            if (product.getName().equals(productName)) {
                return product.getPrice();
            }
        }
        return 0L; // 매칭되는 상품이 없을 경우, 원래 0L 값 반환
    }

}
