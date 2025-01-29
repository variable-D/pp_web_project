package com.pp_web_project.repository;

import com.pp_web_project.domain.SkProductDetalis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface SkProductDatailsRepository extends JpaRepository<SkProductDetalis, Long> {
    // ✅ 최신 데이터부터 정렬하여 조회 (최근 50개)
    @Query("SELECT s FROM SkProductDetalis s WHERE s.orderNum = :orderNum ORDER BY s.id DESC")
    Page<SkProductDetalis> findByOrderNum(@Param("orderNum") String orderNum, Pageable pageable);

    // ✅ 최신 데이터부터 정렬하여 조회 (최근 50개)
    @Query("SELECT s FROM SkProductDetalis s WHERE s.romingPhoneNum = :romingPhoneNum ORDER BY s.id DESC")
    Page<SkProductDetalis> findByRomingPhoneNum(@Param("romingPhoneNum") String romingPhoneNum, Pageable pageable);

    // ✅ 최신 데이터부터 정렬하여 조회 (최근 50개)
    @Query("SELECT s FROM SkProductDetalis s WHERE s.rentalMgmtNum = :rentalMgmtNum ORDER BY s.id DESC")
    Page<SkProductDetalis> findByRentalMgmtNum(@Param("rentalMgmtNum") String rentalMgmtNum, Pageable pageable);

    @Query("SELECT s FROM SkProductDetalis s ORDER BY s.id DESC")
    Page<SkProductDetalis> findByAll(Pageable pageable);
}
