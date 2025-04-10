package com.antock.pretest.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("api.sale.detail")
@Getter @Setter
public class SalesDetailApiProperty {

    private String baseUrl;
    private String uri;
    private String key;

}
