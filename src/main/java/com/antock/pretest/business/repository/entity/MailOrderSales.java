package com.antock.pretest.business.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
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
