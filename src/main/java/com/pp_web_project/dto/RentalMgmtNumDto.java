package com.pp_web_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RentalMgmtNumDto {
    @NotBlank(message = "MGMT 번호는 필수 입력 값입니다.")
    @Pattern(regexp = "\\d{10}", message = "MGMT 번호는 숫자 10자리여야 합니다.")
    private String rentalMgmtNum;
}
