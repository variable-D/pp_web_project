package com.pp_web_project.service.sftp.interfaces;

import com.pp_web_project.domain.SftpData;

import java.time.LocalDateTime;
import java.util.List;

public interface SalesPerformanceService {
    List<SftpData> getSalesByStoreNumber(String storeNumber, LocalDateTime startDate, LocalDateTime endDate);
    List<SftpData> getSalesByBarcode(String barcode, LocalDateTime startDate, LocalDateTime endDate);
    List<SftpData> getSalesByStoreNumberAndBarcode(String storeNumber, String barcode, LocalDateTime startDate, LocalDateTime endDate);
    List<SftpData> getFindByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<SftpData> getSalesByTransactionType(String transactionType, LocalDateTime startDate, LocalDateTime endDate);
}

