package com.pp_web_project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SkproductDetalisDto {
    @JsonProperty("TOTAL_CNT")
    private String totalCnt;

    @JsonProperty("RENTAL_MGMT_NUM")
    private String rentalMgmtNum;

    @JsonProperty("OUT1")
    private List<Item> out1;

    @Data
    public static class Item {
        @JsonProperty("RENTAL_MST_NUM")
        private String rentalMstNum;

        @JsonProperty("EQP_MDL_CD")
        private String eqpMdlCd;

        @JsonProperty("ESIM_MAPPING_ID")
        private String esimMappingId;

        @JsonProperty("EQP_SER_NUM")
        private String eqpSerNum;

        @JsonProperty("ROMING_PHON_NUM") // JSON의 "ROMING_PHON_NUM"과 매핑
        private String romingPhoneNum;

        @JsonProperty("ROMING_NUM")
        private String romingNum;
    }
}
