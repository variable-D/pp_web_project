package com.pp_web_project.service.sk.impl;

import com.pp_web_project.domain.SkProductDetalis;
import com.pp_web_project.dto.EsimResponseDto;
import com.pp_web_project.repository.SkProductDatailsRepository;
import com.pp_web_project.service.sk.interfaces.SkProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkProductServiceImpl implements SkProductService {

    private final SkProductDatailsRepository skProductDatailsRepository;
    private final RestTemplate restTemplate;

    @Override
    public List<SkProductDetalis> findByOrderNum(String orderNum) {
        return skProductDatailsRepository.findByOrderNum(orderNum);
    }

    @Override
    public List<SkProductDetalis> findByRomingPhoneNum(String romingPhoneNum) {
        return skProductDatailsRepository.findByRomingPhoneNum(romingPhoneNum);
    }

    @Override
    public List<SkProductDetalis> findByRentalMgmtNum(String rentalMgmtNum) {
        return skProductDatailsRepository.findByRentalMgmtNum(rentalMgmtNum);
    }

    @Override
    public List<SkProductDetalis> findBySkProductAll(LocalDate startDate, LocalDate endDate) {
        return skProductDatailsRepository.findBySellDateBetween(startDate, endDate);
    }

    @Transactional
    @Override
    public int updateIsCodeOneStatusByIds(List<Long> ids, boolean status) {
        return skProductDatailsRepository.updateIsCodeOneStatusByIds(ids, status);
    }

    @Override
    public EsimResponseDto fetchEsimDetailFromApi(String rentalMgmtNum) {
        String apiUrl = "https://www.skroaming.com/api/swinghub";

        Map<String, Object> payload = Map.of(
                "company", "프리피아",
                "apiType", "api5",
                "RENTAL_MGMT_NUM", rentalMgmtNum
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<EsimResponseDto> response = restTemplate.postForEntity(apiUrl, request, EsimResponseDto.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("eSIM 상세조회 API 호출 실패", e);
            return null;
        }
    }

}
