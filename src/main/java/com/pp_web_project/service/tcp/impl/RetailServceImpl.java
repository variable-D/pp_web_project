package com.pp_web_project.service.tcp.impl;

import com.pp_web_project.domain.TcpResponseData;
import com.pp_web_project.repository.TcpResponseDataRepository;
import com.pp_web_project.service.tcp.interfaces.RetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RetailServceImpl implements RetailService {

    private final TcpResponseDataRepository tcpResponseDataRepository;

    @Override
    public List<TcpResponseData> getStoreData(String storeNo, String saleDateAfter, String saleDateBefore) {
        return tcpResponseDataRepository.findStoreData(storeNo, saleDateAfter, saleDateBefore);
    }

    @Override
    public List<TcpResponseData> getProductData(String plunm, String saleDateAfter, String saleDateBefore) {
        return tcpResponseDataRepository.findProductData(plunm, saleDateAfter, saleDateBefore);
    }

    @Override
    public List<TcpResponseData> getAllData(String saleDateAfter, String saleDateBefore) {
        return tcpResponseDataRepository.findAllData(saleDateAfter, saleDateBefore);
    }

    @Override
    public TcpResponseData getManageNo(String manageNo) {
        return tcpResponseDataRepository.findByManageNo(manageNo);
    }
}
