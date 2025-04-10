package com.antock.pretest.business.api;

import com.antock.pretest.business.api.response.AddressResponse;
import com.antock.pretest.business.api.response.SalesDetailResponse;
import com.antock.pretest.business.dto.CsvData;
import com.antock.pretest.common.CustomWebClient;
import com.antock.pretest.property.AddressApiProperty;
import com.antock.pretest.property.SalesCsvApiProperty;
import com.antock.pretest.property.SalesDetailApiProperty;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OpenApi {

    private final CustomWebClient webClient;
    private final SalesDetailApiProperty salesDetailApiProperty;
    private final SalesCsvApiProperty salesCsvApiProperty;
    private final AddressApiProperty addressApiProperty;

    public List<CsvData> getMailOrderDataFromOpenApi(
            String city,
            String district
    ) {
        try {
            URL url = new URL(salesCsvApiProperty.getUriWithRegion(city, district));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            InputStream is = connection.getInputStream();
            Reader reader = new InputStreamReader(is, Charset.forName("euc-kr"));
            return new CsvToBeanBuilder<CsvData>(reader)
                    .withIgnoreQuotations(true)
                    .withType(CsvData.class)
                    .build()
                    .parse();
        } catch (MalformedURLException e) {
            throw new RuntimeException("wrong url data. check url first.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Mono<SalesDetailResponse> getSalesDetailDataFromOpenApi(String taxPayerNumber) {
        if (taxPayerNumber == null || taxPayerNumber.isBlank()) {
            return Mono.just(new SalesDetailResponse());
        }

        URI uri = UriComponentsBuilder
                .fromUriString(String.format("%s%s", salesDetailApiProperty.getBaseUrl(), salesDetailApiProperty.getUri()))
                .queryParam("serviceKey", URLEncoder.encode(salesDetailApiProperty.getKey(), StandardCharsets.UTF_8))
                .queryParam("pageNo", "1")
                .queryParam("numOfRows", "10")
                .queryParam("resultType", "json")
                .queryParam("brno", taxPayerNumber.replace("-", ""))
                .build(true)
                .toUri();

        return webClient.get(
                salesDetailApiProperty.getBaseUrl(),
                uri,
                new ParameterizedTypeReference<>() {
                });
    }

    public Mono<AddressResponse> getDistrictCode(String address) {
        if (address == null || address.isBlank()) {
            return Mono.just(new AddressResponse());
        }

        return webClient.get(
                addressApiProperty.getBaseUrl(),
                uriBuilder -> uriBuilder
                        .path(addressApiProperty.getUri())
                        .queryParam("confmKey", addressApiProperty.getKey())
                        .queryParam("currentPage", 1)
                        .queryParam("countPerPage", 10)
                        .queryParam("keyword", address)
                        .queryParam("resultType", "json")
                        .build(),
                new ParameterizedTypeReference<>() {
                });
    }

}
