package com.antock.pretest.business.repository;

import com.antock.pretest.business.repository.entity.MailOrderSales;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailOrderSalesRepository extends R2dbcRepository<MailOrderSales, String> {
}
