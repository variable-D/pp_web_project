package com.pp_web_project.util;


import com.pp_web_project.domain.Admin;
import com.pp_web_project.domain.JoytelProduct;
import com.pp_web_project.domain.SftpData;
import com.pp_web_project.domain.Users;
import com.pp_web_project.dto.AdminDto;
import com.pp_web_project.dto.JoytelProductDto;
import com.pp_web_project.dto.SftpDataDto;
import com.pp_web_project.dto.UsersDto;
import org.modelmapper.ModelMapper;

public class MapperUtill {
    private static final ModelMapper modelMapper = new ModelMapper();

    // JoytelProduct -> JoytelProductDto
    public static JoytelProductDto JoytelProductToDto(JoytelProduct joytelProduct) {
        return modelMapper.map(joytelProduct, JoytelProductDto.class);
    }

    // JoytelProductDto -> JoytelProduct
    public static JoytelProduct JoytelProductToDomain(JoytelProductDto joytelProductDto) {
        return modelMapper.map(joytelProductDto, JoytelProduct.class);
    }

    public static AdminDto adminToDto(Admin admin) {
        return modelMapper.map(admin, AdminDto.class);
    }

    public static Admin adminToDomain(AdminDto adminDto) {
        return modelMapper.map(adminDto, Admin.class);
    }

    public static SftpDataDto sftpToDto(SftpData sftpData) {
        return modelMapper.map(sftpData, SftpDataDto.class);
    }

    public static SftpData sftpToDomain(SftpDataDto sftpDataDto) {
        return modelMapper.map(sftpDataDto, SftpData.class);
    }
    public static Users userToDomain(UsersDto userDto) {
        return modelMapper.map(userDto, Users.class);
    }

    public static UsersDto userToDto(Users user) {
        return modelMapper.map(user, UsersDto.class);
    }
}
