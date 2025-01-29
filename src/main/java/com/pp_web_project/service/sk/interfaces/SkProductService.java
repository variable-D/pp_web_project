package com.pp_web_project.service.sk.interfaces;

import com.pp_web_project.domain.SkProductDetalis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SkProductService {
    Page<SkProductDetalis> findByOrderNum(String orderNum, Pageable pageable);
    Page<SkProductDetalis> findByRomingPhoneNum(String romingPhoneNum, Pageable pageable);
    Page<SkProductDetalis> findByRentalMgmtNum(String rentalMgmtNum, Pageable pageable);
    Page<SkProductDetalis> findBySkProductAll(Pageable pageable);
}
