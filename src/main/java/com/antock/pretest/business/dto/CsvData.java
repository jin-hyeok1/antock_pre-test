package com.antock.pretest.business.dto;

import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CsvData{

//    @CsvBindByName(column = "통신판매번호")
    @CsvBindByPosition(position = 0)
    private String mailOrderSalesNumber;
//    @CsvBindByName(column = "상호")
    @CsvBindByPosition(position = 2)
    private String name;
//    @CsvBindByName(column = "사업자등록번호")
    @CsvBindByPosition(position = 3)
    private String taxPayerNumber;
//    @CsvBindByName(column = "법인여부")
    @CsvBindByPosition(position = 4)
    private String corporationFlag;
//    @CsvBindByName(column = "사업장소재지")
    @CsvBindByPosition(position = 9)
    private String address;

}
