package com.pp_web_project.repository;

import com.pp_web_project.domain.JoytelProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface JoytelProductRepository extends JpaRepository<JoytelProduct, Long> {
    List<JoytelProduct> findByProductCodeAndSellFalseAndRefundFalse(String products);
    List<JoytelProduct> findBySellFalseAndRefundFalse();
    Page<JoytelProduct> findByProductCodeAndSellTrueAndRefundFalseOrderByIdDesc(String products, Pageable pageable);

    List<JoytelProduct> findByProductCodeAndOrderNumAndSellTrueAndRefundFalse(String products, String orderNum);
    List<JoytelProduct> findByOrderNumAndSellTrueAndRefundFalse(String orderNum);
    Page<JoytelProduct> findBySellTrueAndRefundFalseOrderByIdDesc(Pageable pageable);

    @Query("SELECT p FROM JoytelProduct p WHERE p.productCode = :productCode AND p.sell = false AND p.refund = false AND DATEDIFF(CURRENT_DATE, p.inputDate) >= 20")
    List<JoytelProduct> findExpiredUnsoldProducts(@Param("productCode") String productCode);

    @Query("SELECT p FROM JoytelProduct p WHERE p.sell = false AND p.refund = false AND DATEDIFF(CURRENT_DATE, p.inputDate) >= 20")
    List<JoytelProduct> findAllExpiredUnsoldProducts();

    @Transactional
    @Modifying
    @Query("UPDATE JoytelProduct jp SET jp.refund = true WHERE jp.id IN :ids")
    int updateRefundStatusByIds(@Param("ids") List<Long> ids);

}
