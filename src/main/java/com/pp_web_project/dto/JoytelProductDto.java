package com.pp_web_project.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class JoytelProductDto {

    private Long id;
    private String productCode;
    private Integer days;
    private Boolean sell;
    private LocalDate validity;
    private LocalDate inputDate;
    private String lpa;
    private Boolean refund;
    private String coupon;
    private String transId;
    private String orderNum = "0";
    private String nation;
}
