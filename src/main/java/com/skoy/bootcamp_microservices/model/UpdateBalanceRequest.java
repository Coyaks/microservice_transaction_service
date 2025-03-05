package com.skoy.bootcamp_microservices.model;

import com.skoy.bootcamp_microservices.enums.TransactionTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateBalanceRequest {
    private String productTypeId;
    private TransactionTypeEnum transactionType;
    private BigDecimal amount;

    public UpdateBalanceRequest(String productTypeId, TransactionTypeEnum transactionType, BigDecimal amount) {
        this.productTypeId = productTypeId;
        this.transactionType = transactionType;
        this.amount = amount;
    }
}
