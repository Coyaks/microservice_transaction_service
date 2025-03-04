package com.skoy.microservice_transaction_service.dto;

import com.skoy.microservice_transaction_service.enums.ProductTypeEnum;
import com.skoy.microservice_transaction_service.enums.TransactionStatusEnum;
import com.skoy.microservice_transaction_service.enums.TransactionTypeEnum;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private String id;
    private String customerId;
    private ProductTypeEnum productType;
    private String productTypeId;
    private TransactionTypeEnum transactionType;
    private BigDecimal amount;
    private TransactionStatusEnum status;
}
