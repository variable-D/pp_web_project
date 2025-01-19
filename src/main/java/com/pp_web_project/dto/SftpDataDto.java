package com.pp_web_project.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SftpDataDto {
    private Long id; // 조회 및 수정 시 필요
    private String dataType;
    private String serialNumber;
    private String barcode;
    private String storeNumber;
    private LocalDateTime transactionDate; // LocalDateTime -> String 변환이 필요할 경우 사용
    private String transactionType;
    private String transactionAmount;
}
