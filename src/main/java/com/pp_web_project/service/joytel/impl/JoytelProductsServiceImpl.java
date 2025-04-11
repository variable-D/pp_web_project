package com.pp_web_project.service.joytel.impl;

import com.pp_web_project.domain.JoytelProduct;
import com.pp_web_project.repository.JoytelProductRepository;
import com.pp_web_project.service.joytel.interfaces.JoytelProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JoytelProductsServiceImpl implements JoytelProductsService {
    private final JoytelProductRepository joytelProductRepository;


    @Override
    public List<JoytelProduct> getProducts(String products) {
        return joytelProductRepository.findByProductCodeAndSellFalseAndRefundFalse(products);
    }

    @Override
    public List<JoytelProduct> getInventory() {
        return joytelProductRepository.findBySellFalseAndRefundFalse();
    }

    @Override
    public Page<JoytelProduct> getSoldAndProductCode(Pageable pageable, String productCode) {
        return joytelProductRepository.findByProductCodeAndSellTrueAndRefundFalseOrderByIdDesc(productCode,pageable);
    }


    @Override
    public List<JoytelProduct> getSoldAndOrderNum(String orderNum) {
        return joytelProductRepository.findByOrderNumAndSellTrueAndRefundFalse(orderNum);
    }

    @Override
    public List<JoytelProduct> getSoldAndProductCodeAndOrderNum(String productCode, String orderNum) {
        return joytelProductRepository.findByProductCodeAndOrderNumAndSellTrueAndRefundFalse(productCode, orderNum);
    }

    @Override
    public Page<JoytelProduct> getSoldProductsPage(Pageable pageable) {
        return joytelProductRepository.findBySellTrueAndRefundFalseOrderByIdDesc(pageable);
    }

    @Override
    public List<JoytelProduct> getExpiredUnsoldProducts(String productCode) {
        return joytelProductRepository.findExpiredUnsoldProducts(productCode);
    }

    @Override
    public List<JoytelProduct> getAllExpiredUnsoldProducts() {
        return joytelProductRepository.findAllExpiredUnsoldProducts();
    }

    @Override
    public List<JoytelProduct> getExpiredUnsoldNation(String nation) {
        return joytelProductRepository.findExpiredUnsoldNation(nation);
    }

    @Override
    public int updateRefundStatusByIds(List<Long> ids) {
        return joytelProductRepository.updateRefundStatusByIds(ids);
    }

    @Override
    public List<JoytelProduct> getNationAndSellFalseAndRefundFalse(String nation) {
        return joytelProductRepository.findByNationAndSellFalseAndRefundFalse(nation);
    }

    @Override
    public Page<JoytelProduct> getNationAndSellTrueAndRefundFalse(String nation, Pageable pageable) {
        return joytelProductRepository.findByNationAndSellTrueAndRefundFalse(nation, pageable);
    }
}
