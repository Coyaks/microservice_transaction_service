package com.skoy.microservice_transaction_service.mapper;

import com.skoy.microservice_transaction_service.dto.TransactionDTO;
import com.skoy.microservice_transaction_service.model.Transaction;

public class TransactionMapper {

    public static Transaction toEntity(TransactionDTO dto) {
        Transaction item = new Transaction();
        item.setId(dto.getId());
        item.setAccountId(dto.getAccountId());
        item.setCustomerId(dto.getCustomerId());
        item.setType(dto.getType());
        item.setAmount(dto.getAmount());
        item.setStatus(dto.getStatus());
        return item;
    }

    public static TransactionDTO toDto(Transaction item) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(item.getId());
        dto.setAccountId(item.getAccountId());
        dto.setCustomerId(item.getCustomerId());
        dto.setType(item.getType());
        dto.setAmount(item.getAmount());
        dto.setStatus(item.getStatus());
        return dto;
    }
}
