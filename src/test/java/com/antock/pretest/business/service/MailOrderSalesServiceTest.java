package com.antock.pretest.business.service;

import com.antock.pretest.business.api.OpenApi;
import com.antock.pretest.business.api.response.AddressResponse;
import com.antock.pretest.business.api.response.SalesDetailResponse;
import com.antock.pretest.business.dto.CsvData;
import com.antock.pretest.business.repository.MailOrderSalesRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class MailOrderSalesServiceTest {

    @MockitoBean
    private OpenApi openApi;
    @Autowired
    private MailOrderSalesRepository mailOrderSalesRepository;
    @Autowired
    private MailOrderSalesService mailOrderSalesService;

    @Test
    @DisplayName("법인으로 필터링 하기")
    void test_only_corporation_is_processed() {

        //given
        BDDMockito.when(openApi.getMailOrderDataFromOpenApi("서울특별시", "양천구"))
                .thenReturn(List.of(CsvData.builder()
                                .mailOrderSalesNumber("1234")
                                .name("대박통신")
                                .taxPayerNumber("123-456-1234")
                                .corporationFlag("법인")
                                .address("서울특별시 양천구 목동 802-7")
                                .build(),
                        CsvData.builder()
                                .mailOrderSalesNumber("5678")
                                .name("마음통신")
                                .taxPayerNumber("456-789-1234")
                                .corporationFlag("개인")
                                .address("서울특별시 양천구 신정동 ***-**")
                                .build(),
                        CsvData.builder()
                                .mailOrderSalesNumber("8412")
                                .name("개미통신")
                                .taxPayerNumber("456-912-1234")
                                .corporationFlag("법인")
                                .address("서울특별시 양천구 신정동 903-15")
                                .build()
                ));

        SalesDetailResponse salesDetailResponse = new SalesDetailResponse();
        SalesDetailResponse.SalesDetailData salesDetailData = new SalesDetailResponse.SalesDetailData();
        salesDetailData.setCrno("NumberForTest");
        salesDetailResponse.setItems(List.of(salesDetailData));

        BDDMockito.when(openApi.getSalesDetailDataFromOpenApi(ArgumentMatchers.anyString()))
                .thenReturn(Mono.just(salesDetailResponse));

        AddressResponse addressResponse = new AddressResponse();
        AddressResponse.Result results = new AddressResponse.Result();
        AddressResponse.Result.Juso juso = new AddressResponse.Result.Juso();
        juso.setAdmCd("districtCodeForTest");
        results.setJuso(List.of(juso));
        addressResponse.setResults(results);

        BDDMockito.when(openApi.getDistrictCode(ArgumentMatchers.anyString()))
                .thenReturn(Mono.just(addressResponse));

        //when
        mailOrderSalesService.saveDataFromOpenApi("서울특별시", "양천구").block();

        //then
        StepVerifier.create(mailOrderSalesRepository.findAll())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    @DisplayName("예외 사항으로 인한 Mono.just(emptyData)를 받았을 시 정상적으로 빈 칸으로 값 저장")
    void test_not_provided_tax_payer_number() {
        //when
        BDDMockito.when(openApi.getMailOrderDataFromOpenApi("제주특별자취도", "서귀포시"))
                .thenReturn(List.of(CsvData.builder()
                                .mailOrderSalesNumber("1234")
                                .name("미나투어(주)")
                                .taxPayerNumber(null)
                                .corporationFlag("법인")
                                .address("제주특별자치도 태평로")
                                .build(),
                        CsvData.builder()
                                .mailOrderSalesNumber("5678")
                                .name("(주)아쿠아랜드")
                                .taxPayerNumber(" ")
                                .corporationFlag("법인")
                                .address("제주특별자치도 서귀포시 법환동")
                                .build()
                ));

        BDDMockito.when(openApi.getSalesDetailDataFromOpenApi(ArgumentMatchers.anyString()))
                .thenReturn(Mono.just(new SalesDetailResponse()));

        BDDMockito.when(openApi.getDistrictCode(ArgumentMatchers.anyString()))
                .thenReturn(Mono.just(new AddressResponse()));

        //when
        mailOrderSalesService.saveDataFromOpenApi("제추특별자치도", "서귀포시").block();

        //then
        StepVerifier.create(mailOrderSalesRepository.findAll().collectList())
                .assertNext(all -> assertThat(all).allSatisfy(mailOrderSale -> {
                    assertThat(mailOrderSale.getCorporationNumber()).isEqualTo("");
                    assertThat(mailOrderSale.getDistrictCode()).isEqualTo("");
                }))
                .verifyComplete();
    }

}