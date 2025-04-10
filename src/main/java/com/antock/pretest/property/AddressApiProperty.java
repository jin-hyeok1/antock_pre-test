package com.antock.pretest.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("api.sale.address")
@Getter @Setter
public class AddressApiProperty {
    private String baseUrl;
    private String uri;
    private String key;
}
