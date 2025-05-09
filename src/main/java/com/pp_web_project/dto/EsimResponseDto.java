package com.pp_web_project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class EsimResponseDto {

    @JsonProperty("CUST_NM")
    private String custNm;

    @JsonProperty("EMAIL_ADDR")
    private String emailAddr;

    @JsonProperty("DOM_CNTC_NUM")
    private String domCntcNum;

    @JsonProperty("ROMING_PASSPORT_NUM")
    private String romingPassportNum;

    @JsonProperty("RCMNDR_ID")
    private String rcmndrId;

    @JsonProperty("RENTAL_MGMT_NUM")
    private String rentalMgmtNum;

    @JsonProperty("RENTAL_SCHD_STA_DTM")
    private String rentalSchdStaDtm;

    @JsonProperty("RENTAL_SCHD_END_DTM")
    private String rentalSchdEndDtm;

    @JsonProperty("RENTAL_SALE_ORG_ID")
    private String rentalSaleOrgId;

    @JsonProperty("RENTAL_SALE_ORG_NM")
    private String rentalSaleOrgNm;

    @JsonProperty("TOTAL_CNT")
    private int totalCnt;

    @JsonProperty("OUT1")
    private List<EsimDetailItem> out1;

    @Data
    public static class EsimDetailItem {

        @JsonProperty("RENTAL_MST_NUM")
        private String rentalMstNum;

        @JsonProperty("RENTAL_FEE_PROD_NM")
        private String rentalFeeProdNm;

        @JsonProperty("RENTAL_FEE_PROD_ID")
        private String rentalFeeProdId;

        @JsonProperty("ROMING_TYP_CD")
        private String romingTypCd;

        @JsonProperty("RSV_VOU_NUM")
        private String rsvVouNum;

        @JsonProperty("ROMING_PHON_NUM")
        private String romingPhonNum;

        @JsonProperty("USE_STA_DTM")
        private String useStaDtm;

        @JsonProperty("USE_END_DTM")
        private String useEndDtm;

        @JsonProperty("ICCID_NUM")
        private String iccidNum;
    }
}
