package com.antock.pretest.business.api.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AddressResponse {
    private Result results;

    public AddressResponse() {
        results = new Result();
    }

    @Data
    public static class Result {

        private Common common;
        private List<Juso> juso;

        public Result() {
            this.juso = new ArrayList<>();
        }
        @Data
        public static class Common {
            private String errorMessage;
            private String countPerPage;
            private String totalCount;
            private String errorCode;
            private String currentPage;
        }

        @Data
        public static class Juso {
            private String admCd;
        }

        public Juso getJuso() {
            return this.juso.stream().findFirst().orElse(new Juso());
        }
    }
}
