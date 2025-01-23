package com.pp_web_project.service.tcp.interfaces;

import com.pp_web_project.domain.TcpResponseData;

import java.util.List;

public interface RetailService {
    List<TcpResponseData> getStoreData(String storeNo, String saleDateAfter, String saleDateBefore);
    List<TcpResponseData> getProductData(String plunm, String saleDateAfter, String saleDateBefore);
    List<TcpResponseData> getAllData(String saleDateAfter, String saleDateBefore);
    TcpResponseData getManageNo(String manageNo);
}
