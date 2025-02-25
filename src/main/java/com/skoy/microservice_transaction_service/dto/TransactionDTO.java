package com.skoy.microservice_transaction_service.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private String id;
    private String accountId;
    private String customerId;
    private String type; // DEPOSIT, WITHDRAWAL
    private BigDecimal amount;
    private String status; // PENDING, APPROVED, REJECTED
}
