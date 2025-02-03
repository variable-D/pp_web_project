package com.pp_web_project.service.sk.impl;

import com.pp_web_project.domain.SkProductDetalis;
import com.pp_web_project.repository.SkProductDatailsRepository;
import com.pp_web_project.service.sk.interfaces.SkProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SkProductServiceImpl implements SkProductService {

    private final SkProductDatailsRepository skProductDatailsRepository;

    @Override
    public List<SkProductDetalis> findByOrderNum(String orderNum) {
        return skProductDatailsRepository.findByOrderNum(orderNum);
    }

    @Override
    public List<SkProductDetalis> findByRomingPhoneNum(String romingPhoneNum) {
        return skProductDatailsRepository.findByRomingPhoneNum(romingPhoneNum);
    }

    @Override
    public List<SkProductDetalis> findByRentalMgmtNum(String rentalMgmtNum) {
        return skProductDatailsRepository.findByRentalMgmtNum(rentalMgmtNum);
    }

    @Override
    public List<SkProductDetalis> findBySkProductAll(LocalDate startDate, LocalDate endDate) {
        return skProductDatailsRepository.findBySellDateBetween(startDate, endDate);
    }

    @Transactional
    @Override
    public int updateIsCodeOneStatusByIds(List<Long> ids, boolean status) {
        return skProductDatailsRepository.updateIsCodeOneStatusByIds(ids, status);
    }
}
