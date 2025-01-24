package com.pp_web_project.dto;

import lombok.Data;

@Data
public class UsersDto {
    private Long id;
    private String userId; // ✅ username → userId로 변경
    private String password;
}
