package com.pp_web_project.repository;

import com.pp_web_project.domain.SkProductDetalis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


public interface SkProductDatailsRepository extends JpaRepository<SkProductDetalis, Long> {
    List<SkProductDetalis> findByOrderNum(String orderNum);
    List<SkProductDetalis> findByRomingPhoneNum(String romingPhoneNum);
    List<SkProductDetalis> findByRentalMgmtNum(String rentalMgmtNum);
    @Query("SELECT s FROM SkProductDetalis s WHERE s.sellDate >= :startDate AND s.sellDate < :endDate ORDER BY s.id DESC")
    List<SkProductDetalis> findBySellDateBetween(@Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);


    @Transactional
    @Modifying
    @Query("UPDATE SkProductDetalis sk SET sk.isCodeOne = :status WHERE sk.id IN :ids")
    int updateIsCodeOneStatusByIds(@Param("ids") List<Long> ids, @Param("status") boolean status);

}
