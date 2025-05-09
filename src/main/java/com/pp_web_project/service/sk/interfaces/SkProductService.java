package com.pp_web_project.service.sk.interfaces;

import com.pp_web_project.domain.SkProductDetalis;
import com.pp_web_project.dto.EsimResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface SkProductService {

    // ✅ 주문번호로 검색
    List<SkProductDetalis> findByOrderNum(String orderNum);

    // ✅ 서비스 번호로 검색
    List<SkProductDetalis> findByRomingPhoneNum(String romingPhoneNum);

    // ✅ MGMT 번호로 검색
    List<SkProductDetalis> findByRentalMgmtNum(String rentalMgmtNum);

    // ✅ 특정 기간 내 전체 데이터 조회
    List<SkProductDetalis> findBySkProductAll(LocalDate startDate, LocalDate endDate);

    // ✅ 코드원 상태 변경 (true/false 가능)
    int updateIsCodeOneStatusByIds(List<Long> ids, boolean status);

    EsimResponseDto fetchEsimDetailFromApi(String rentalMgmtNum);

}
