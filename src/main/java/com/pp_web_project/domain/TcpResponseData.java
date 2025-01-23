package com.pp_web_project.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TcpResponseData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 공통 헤더 필드
    private String length;       // 전문 길이
    private String jobGubun;     // 거래구분
    private String storeNo;      // 점포번호
    private String saleDate;     // 거래일자
    private String saleTime;     // 거래시간
    private String posNo;        // POS 번호
    private String dealDatDom;   // 전문관리번호

    private String resCd;  //응답코드 0000: 성공, 9999: 실패 9000: 판매불가 점포
    private String resMsg; //응답메시지 0000: 성공, 9999: 실패 9000: 판매불가 점포{국내용 eSIM은 특수 입지에서만 판매 됩니다. (해외용 eSIM은 판매 가능)}
    @Column(columnDefinition = "0")
    private String plunm; //상품명
    @Column(columnDefinition = "0")
    private String pinNo1; //LPA 번호
    @Column(columnDefinition = "0")
    private String manageNo; //관리번호 YYYYMMDD+CU+6자리 누적값+랜덤 1자리
    @Column(columnDefinition = "0")
    private String validTerm; //유효기간
    @Column(columnDefinition = "0")
    private String format; //포맷 길이: 270
    @Column(columnDefinition = "0")
    private String phoneNum; //휴대폰 번호
    @Column(columnDefinition = "0")
    private String mgmtNum; //렌탈 관리번호
    @Column(columnDefinition = "0")
    private String okNo; //승인번호
    @Column(columnDefinition = "0")
    private String ext1; //예비필드1 길이:1000
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE") // 데이터베이스 레벨에서 기본값 설정
    private Boolean isProcessed; // 처리 여부
}
