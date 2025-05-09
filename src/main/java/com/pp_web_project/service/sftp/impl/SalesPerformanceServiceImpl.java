package com.pp_web_project.service.sftp.impl;

import com.pp_web_project.domain.SftpData;
import com.pp_web_project.repository.SftpDataRepository;
import com.pp_web_project.service.sftp.interfaces.SalesPerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class SalesPerformanceServiceImpl implements SalesPerformanceService {

    private final SftpDataRepository sftpDataRepository;

    @Override
    public List<SftpData> getSalesByStoreNumber(String storeNumber, LocalDateTime startDate, LocalDateTime endDate) {
        return sftpDataRepository.findByStoreNumberAndTransactionDateBetween(storeNumber, startDate, endDate);
    }

    @Override
    public List<SftpData> getSalesByBarcode(String barcode, LocalDateTime startDate, LocalDateTime endDate) {
        return sftpDataRepository.findByBarcodeAndTransactionDateBetween(barcode, startDate, endDate);
    }

    @Override
    public List<SftpData> getSalesByStoreNumberAndBarcode(String storeNumber, String barcode, LocalDateTime startDate, LocalDateTime endDate) {
        return sftpDataRepository.findByStoreNumberAndBarcodeAndTransactionDateBetween(storeNumber, barcode, startDate, endDate);
    }

    @Override
    public List<SftpData> getFindByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return sftpDataRepository.findByTransactionDateBetween(startDate, endDate);
    }


    @Override
    public List<SftpData> getSalesByTransactionType(String transactionType, LocalDateTime startDate, LocalDateTime endDate) {
        return sftpDataRepository.findByTransactionTypeAndDateRange(transactionType, startDate, endDate);
    }
}
