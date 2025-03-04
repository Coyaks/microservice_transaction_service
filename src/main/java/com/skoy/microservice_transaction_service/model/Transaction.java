package com.skoy.microservice_transaction_service.model;

import com.skoy.microservice_transaction_service.enums.ProductTypeEnum;
import com.skoy.microservice_transaction_service.enums.TransactionStatusEnum;
import com.skoy.microservice_transaction_service.enums.TransactionTypeEnum;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    private String customerId;
    private ProductTypeEnum productType;
    private String productTypeId;
    private TransactionTypeEnum transactionType; // DEPOSIT, WITHDRAWAL, TRANSFER
    private BigDecimal amount;
    private TransactionStatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}