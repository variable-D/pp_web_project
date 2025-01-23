package com.pp_web_project.repository;

import com.pp_web_project.domain.TcpResponseData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TcpResponseDataRepository extends JpaRepository<TcpResponseData, Long> {
    // 특정 기간 동안의 점포별 조회
    @Query("SELECT t FROM TcpResponseData t WHERE t.storeNo = :storeNo AND t.saleDate BETWEEN :saleDateAfter AND :saleDateBefore AND t.resCd = '0000'")
    List<TcpResponseData> findStoreData(
            @Param("storeNo") String storeNo,
            @Param("saleDateAfter") String saleDateAfter,
            @Param("saleDateBefore") String saleDateBefore
    );

    // 특정 기간 동안의 상품별 조회
    @Query("SELECT t FROM TcpResponseData t WHERE t.plunm = :plunm AND t.saleDate BETWEEN :saleDateAfter AND :saleDateBefore AND t.resCd = '0000'")
    List<TcpResponseData> findProductData(
            @Param("plunm") String plunm,
            @Param("saleDateAfter") String saleDateAfter,
            @Param("saleDateBefore") String saleDateBefore
    );

    // 특정 기간 동안의 전체 데이터 조회
    @Query("SELECT t FROM TcpResponseData t WHERE t.saleDate BETWEEN :saleDateAfter AND :saleDateBefore AND t.resCd = '0000'")
    List<TcpResponseData> findAllData(
            @Param("saleDateAfter") String saleDateAfter,
            @Param("saleDateBefore") String saleDateBefore
    );

    // 주문번호로 조회
    @Query("SELECT t FROM TcpResponseData t WHERE t.manageNo = :manageNo AND t.resCd = '0000'")
    TcpResponseData findByManageNo(
            @Param("manageNo") String manageNo
    );


}
