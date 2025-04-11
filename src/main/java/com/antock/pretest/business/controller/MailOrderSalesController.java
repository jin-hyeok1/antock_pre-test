package com.antock.pretest.business.controller;

import com.antock.pretest.business.service.MailOrderSalesService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/mail-order-sales")
@RequiredArgsConstructor
public class MailOrderSalesController {

    private final MailOrderSalesService mailOrderSalesService;

    @PostMapping
    public Mono<ResponseEntity<Void>> saveDataFromOpenApi(
            @RequestParam String city,
            @RequestParam String district
    ) {
        if (Strings.isBlank(city) || Strings.isBlank(district)) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        return mailOrderSalesService.saveDataFromOpenApi(city, district)
                .then(Mono.just(ResponseEntity.ok().build()));
    }
}
