package com.antock.pretest.business.api.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SalesDetailResponse {

    private String resultCode;
    private String resultMsg;
    private String numOfRows;
    private String totalCount;
    private List<SalesDetailData> items;

    public SalesDetailResponse() {
        this.items = new ArrayList<>();
    }

    @Data
    public static class SalesDetailData {
        private String crno;
    }

    public SalesDetailData getSalesDetailData() {
        return this.items.stream().findFirst().orElse(new SalesDetailData());
    }

}
