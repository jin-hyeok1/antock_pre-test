package com.antock.pretest.property;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@ConfigurationProperties("api.sale.csv")
@Setter
public class SalesCsvApiProperty {

    private String uri;

    public String getUriWithRegion(String city, String district){
        return uri +
                URLEncoder.encode("통신판매사업자", StandardCharsets.UTF_8) +
                "_" +
                URLEncoder.encode(city, StandardCharsets.UTF_8) +
                "_" +
                URLEncoder.encode(district, StandardCharsets.UTF_8) +
                ".csv";
    }
}
