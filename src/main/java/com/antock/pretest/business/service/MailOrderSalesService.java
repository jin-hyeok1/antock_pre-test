package com.antock.pretest.business.service;

import com.antock.pretest.business.api.OpenApi;
import com.antock.pretest.business.api.response.AddressResponse;
import com.antock.pretest.business.api.response.SalesDetailResponse;
import com.antock.pretest.business.dto.CsvData;
import com.antock.pretest.business.repository.entity.MailOrderSales;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MailOrderSalesService {

    private final OpenApi openApi;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    public Mono<Void> saveDataFromOpenApi(String city, String district) {
        List<CsvData> mailOrderData = openApi.getMailOrderDataFromOpenApi(city, district);
        return Flux.fromIterable(mailOrderData)
                .filter(row -> "법인".equals(row.getCorporationFlag()))
                .flatMap(this::processRowAsync)
                .flatMap(this::saveToRepository)
                .then();
    }

    private Mono<MailOrderSales> processRowAsync(CsvData row) {
        Mono<SalesDetailResponse> salesDetailMono = openApi.getSalesDetailDataFromOpenApi(row.getTaxPayerNumber())
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(e -> Mono.just(new SalesDetailResponse()));

        Mono<AddressResponse> districtCodeMono = openApi.getDistrictCode(row.getAddress())
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(e -> Mono.just(new AddressResponse()));

        return Mono.zip(salesDetailMono, districtCodeMono)
                .map(tuple -> {
                    SalesDetailResponse sales = tuple.getT1();
                    AddressResponse addr = tuple.getT2();

                    return MailOrderSales.builder()
                            .mailOrderSalesNumber(row.getMailOrderSalesNumber())
                            .name(row.getName())
                            .taxPayerNumber(row.getTaxPayerNumber())
                            .corporationNumber(Optional.ofNullable(sales.getSalesDetailData().getCrno()).orElse(""))
                            .districtCode(Optional.ofNullable(addr.getResults().getJuso().getAdmCd()).orElse(""))
                            .build();
                });
    }

    private Mono<MailOrderSales> saveToRepository(MailOrderSales mailOrderSales) {
        return r2dbcEntityTemplate.insert(mailOrderSales);
    }
}
