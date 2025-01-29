package com.pp_web_project.service.sk.impl;

import com.pp_web_project.domain.SkProductDetalis;
import com.pp_web_project.repository.SkProductDatailsRepository;
import com.pp_web_project.service.sk.interfaces.SkProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SkProductServiceImpl implements SkProductService {

    private final SkProductDatailsRepository skProductDatailsRepository;

    @Override
    public Page<SkProductDetalis> findByOrderNum(String orderNum, Pageable pageable) {
        return skProductDatailsRepository.findByOrderNum(orderNum, pageable);
    }

    @Override
    public Page<SkProductDetalis> findByRomingPhoneNum(String romingPhoneNum, Pageable pageable) {
        return skProductDatailsRepository.findByRomingPhoneNum(romingPhoneNum, pageable);
    }

    @Override
    public Page<SkProductDetalis> findByRentalMgmtNum(String rentalMgmtNum, Pageable pageable) {
        return skProductDatailsRepository.findByRentalMgmtNum(rentalMgmtNum, pageable);
    }

    @Override
    public Page<SkProductDetalis> findBySkProductAll(Pageable pageable) {
        return skProductDatailsRepository.findByAll(pageable);
    }
}
