package com.pp_web_project.repository;

import com.pp_web_project.domain.SftpData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SftpDataRepository extends JpaRepository<SftpData, Long> {

    // 특정 기간 동안의 점포별 조회
    List<SftpData> findByStoreNumberAndTransactionDateBetween(String storeNumber, LocalDateTime startDate, LocalDateTime endDate);

    // 특정 기간 동안의 상품별 조회
    List<SftpData> findByBarcodeAndTransactionDateBetween(String barcode, LocalDateTime startDate, LocalDateTime endDate);

    // 특정 기간 동안의 전체 데이터 조회
    List<SftpData> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
