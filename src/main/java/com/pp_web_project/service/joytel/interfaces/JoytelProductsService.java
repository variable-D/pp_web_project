package com.pp_web_project.service.joytel.interfaces;

import com.pp_web_project.domain.JoytelProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JoytelProductsService {
    List<JoytelProduct> getProducts(String products);
    List<JoytelProduct> getInventory();
    Page<JoytelProduct> getSoldAndProductCode(Pageable pageable,String productCode);
    List<JoytelProduct> getSoldAndProductCodeAndOrderNum(String productCode, String orderNum);
    List<JoytelProduct> getSoldAndOrderNum(String orderNum);
    Page<JoytelProduct> getSoldProductsPage(Pageable pageable);
    // ✅ 특정 상품(productCode) 중에서 20일 이상 지난 데이터 조회
    List<JoytelProduct> getExpiredUnsoldProducts(String productCode);

    // ✅ 모든 데이터 중에서 20일 이상 지난 데이터 조회
    List<JoytelProduct> getAllExpiredUnsoldProducts();
    int updateRefundStatusByIds(List<Long> ids);

    List<JoytelProduct> getNationAndSellFalseAndRefundFalse(String nation);
    Page<JoytelProduct> getNationAndSellTrueAndRefundFalse(String nation, Pageable pageable);

    List<JoytelProduct> getExpiredUnsoldNation(String nation);
}
