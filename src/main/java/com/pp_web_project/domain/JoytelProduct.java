package com.pp_web_project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "joytel_product")
@Getter
@Setter
@NoArgsConstructor
public class JoytelProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "days")
    private Integer days;

    @Column(name = "sell")
    private Boolean sell;

    @Column(name = "validity")
    private LocalDate validity;

    @Column(name = "input_date")
    private LocalDate inputDate;

    @Column(name = "lpa")
    private String lpa;

    @Column(name = "refund")
    private Boolean refund;

    @Column(name = "coupon")
    private String coupon;

    @Column(name = "trans_id")
    private String transId;

    @Column(name = "order_num")
    private String orderNum = "0";

}
