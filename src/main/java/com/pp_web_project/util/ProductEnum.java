package com.pp_web_project.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductEnum {
        JP_1GB_3D("8809365670444", "일본 1GB 3일"),
        JP_1GB_5D("8809365670451", "일본 1GB 5일"),
        JP_1GB_7D("8809365670468", "일본 1GB 7일"),
        JP_1GB_10D("8809365670475", "일본 1GB 10일"),
        JP_MAX_3D("8809365670482", "일본 MAX 3일"),
        JP_MAX_5D("8809365670499", "일본 MAX 5일"),
        JP_MAX_7D("8809365670505", "일본 MAX 7일"),
        JP_MAX_10D("8809365670512", "일본 MAX 10일"),
        SKT_3D("8809365670529", "SKT 3일"),
        SKT_5D("8809365670536", "SKT 5일"),
        SKT_7D("8809365670543", "SKT 7일"),
        SKT_10D("8809365670550", "SKT 10일"),
        SKT_15D("8809365670567", "SKT 15일"),
        SKT_20D("8809365670574", "SKT 20일"),
        SKT_30D("8809365670581", "SKT 30일"),
        SKT_60D("8809365670598", "SKT 60일"),
        SKT_90D("8809365670604", "SKT 90일");

        private final String code;
        private final String name;

        // ✅ 바코드로 제품 찾기 (조회 메서드 추가)
        public static String getProductNameByCode(String barcode) {
            for (ProductEnum product : values()) {
                if (product.getCode().equals(barcode)) {
                    return product.getName();
                }
            }
            return barcode; // 매칭되는 상품이 없을 경우, 원래 바코드 값 반환
        }
}
