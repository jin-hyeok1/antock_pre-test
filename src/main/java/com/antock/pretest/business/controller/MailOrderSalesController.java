package com.antock.pretest.business.controller;

import com.antock.pretest.business.service.MailOrderSalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail-order-sales")
@RequiredArgsConstructor
public class MailOrderSalesController {

    private final MailOrderSalesService mailOrderSalesService;

    @PostMapping
    public ResponseEntity<?> saveDataFromOpenApi(
            @RequestParam String city,
            @RequestParam String district
    ) {
        mailOrderSalesService.saveDataFromOpenApi(city, district);
       return ResponseEntity.ok().build();
    }
}
