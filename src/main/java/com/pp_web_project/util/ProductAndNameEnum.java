package com.pp_web_project.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductAndNameEnum {
    JP_1GB_3D("eSIM-JP1G-03", "일본 1GB 3일"),
    JP_1GB_5D("eSIM-JP1G-05", "일본 1GB 5일"),
    JP_1GB_7D("eSIM-JP1G-07", "일본 1GB 7일"),
    JP_1GB_10D("eSIM-JP1G-10", "일본 1GB 10일"),
    JP_MAX_3D("eSIM-JPMAX-03", "일본 MAX 3일"),
    JP_MAX_5D("eSIM-JPMAX-05", "일본 MAX 5일"),
    JP_MAX_7D("eSIM-JPMAX-07", "일본 MAX 7일"),
    JP_MAX_10D("eSIM-JPMAX-10", "일본 MAX 10일"),
    KR_MAX_3D("eSIM-KRMAX-03", "한국 MAX 3일"),
    KR_MAX_5D("eSIM-KRMAX-05", "한국 MAX 5일"),
    KR_MAX_7D("eSIM-KRMAX-07", "한국 MAX 7일"),
    KR_MAX_10D("eSIM-KRMAX-10", "한국 MAX 10일"),
    KR_MAX_15D("eSIM-KRMAX-15", "한국 MAX 15일"),
    KR_MAX_20D("eSIM-KRMAX-20", "한국 MAX 20일"),
    SKT_2D("SKT-eSIM-02D", "SKT 2일"),
    SKT_3D("SKT-eSIM-03D", "SKT 3일"),
    SKT_4D("SKT-eSIM-04D", "SKT 4일"),
    SKT_5D("SKT-eSIM-05D", "SKT 5일"),
    SKT_6D("SKT-eSIM-06D", "SKT 6일"),
    SKT_7D("SKT-eSIM-07D", "SKT 7일"),
    SKT_8D("SKT-eSIM-08D", "SKT 8일"),
    SKT_9D("SKT-eSIM-09D", "SKT 9일"),
    SKT_10D("SKT-eSIM-10D", "SKT 10일"),
    SKT_15D("SKT-eSIM-15D", "SKT 15일"),
    SKT_20D("SKT-eSIM-20D", "SKT 20일"),
    SKT_30D("SKT-eSIM-30D", "SKT 30일"),
    SKT_60D("SKT-eSIM-60D", "SKT 60일"),
    SKT_90D("SKT-eSIM-90D", "SKT 90일");

    private final String product;
    private final String productName;

    // ✅ 바코드로 제품 찾기 (조회 메서드 추가)
    public static String getProductByName(String product) {
        for (ProductAndNameEnum products : values()) {
            if (products.getProduct().equals(product)) {
                return products.getProductName();
            }
        }
        return product; // 매칭되는 상품이 없을 경우, 원래 바코드 값 반환
    }
}
