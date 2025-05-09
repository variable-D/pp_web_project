package com.pp_web_project.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductPlusSpace {
    JP_1GB_3D("eSIM-JP1G-03"),
    JP_1GB_5D("eSIM-JP1G-05"),
    JP_1GB_7D("eSIM-JP1G-07"),
    JP_1GB_10D("eSIM-JP1G-10"),
    JP_MAX_3D("eSIM-JPMAX-03"),
    JP_MAX_5D("eSIM-JPMAX-05"),
    JP_MAX_7D("eSIM-JPMAX-07"),
    JP_MAX_10D("eSIM-JPMAX-10"),
    KR_MAX_3D("eSIM-KRMAX-03"),
    KR_MAX_5D("eSIM-KRMAX-05"),
    KR_MAX_7D("eSIM-KRMAX-07"),
    KR_MAX_10D("eSIM-KRMAX-10"),
    KR_MAX_15D("eSIM-KRMAX-15"),
    KR_MAX_20D("eSIM-KRMAX-20"),
    SKT_3D("SKT-eSIM-03D"),
    SKT_5D("SKT-eSIM-05D"),
    SKT_7D("SKT-eSIM-07D"),
    SKT_10D("SKT-eSIM-10D"),
    SKT_15D("SKT-eSIM-15D"),
    SKT_20D("SKT-eSIM-20D"),
    SKT_30D("SKT-eSIM-30D"),
    SKT_60D("SKT-eSIM-60D"),
    SKT_90D("SKT-eSIM-90D");

    private final String product;

    // ✅ 전체 길이를 30자로 맞춤
    private static final int SPACE_LENGTH = 30;

    // ✅ 공백을 추가한 문자열 반환 (총 30자리로 맞춤)
    public String getProductPlusSpace() {
        return String.format("%-" + SPACE_LENGTH + "s", product);
    }

    // ✅ 바코드로 제품 찾기 (조회 메서드)
    public static String getProductPlusSpaceByName(String product) {
        for (ProductPlusSpace products : values()) {
            if (products.getProduct().equals(product)) {
                return products.getProductPlusSpace();
            }
        }
        return String.format("%-" + SPACE_LENGTH + "s", product); // 매칭되지 않으면 원래 값 + 공백 반환
    }
}
