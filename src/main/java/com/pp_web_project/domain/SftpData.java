package com.pp_web_project.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "sftp_data")
public class SftpData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "data_type", nullable = false)
    private String dataType;

    @Column(name = "serial_number", nullable = false)
    private String serialNumber;

    @Column(name = "barcode", nullable = false)
    private String barcode;

    @Column(name = "store_number", nullable = false)
    private String storeNumber;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Column(name = "transaction_amount", nullable = false)
    private String transactionAmount;
}
