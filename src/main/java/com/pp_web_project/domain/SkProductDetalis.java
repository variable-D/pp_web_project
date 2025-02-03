package com.pp_web_project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@Getter
@Setter
@Table(name = "SkproductDetalis")
public class SkProductDetalis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_num")
    private String orderNum;

    @Column(name = "total_cnt")
    private String totalCnt;

    @Column(name = "rental_mgmt_num")
    private String rentalMgmtNum;

    @Column(name = "rental_mst_num")
    private String rentalMstNum;

    @Column(name = "eqp_mdl_cd")
    private String eqpMdlCd;

    @Column(name = "esim_mapping_id")
    private String esimMappingId;

    @Column(name = "eqp_ser_num")
    private String eqpSerNum;

    @Column(name = "roming_phone_num")
    private String romingPhoneNum;

    @Column(name = "roming_num")
    private String romingNum;

    @Column(name = "sell_date", insertable = false, updatable = false)
    private LocalDate sellDate;

    @Column(name = "is_code_one", nullable = false)
    private Boolean isCodeOne = false;
}
