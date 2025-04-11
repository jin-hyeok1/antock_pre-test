package com.antock.pretest.business.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("MAIL_ORDER_SALES")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MailOrderSales {
    @Id
    private String mailOrderSalesNumber;
    private String name;
    private String taxPayerNumber;
    private String corporationNumber;
    private String districtCode;
}
