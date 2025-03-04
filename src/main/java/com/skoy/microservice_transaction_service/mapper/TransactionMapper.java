package com.skoy.microservice_transaction_service.mapper;

import com.skoy.microservice_transaction_service.dto.TransactionDTO;
import com.skoy.microservice_transaction_service.model.Transaction;

public class TransactionMapper {

    public static Transaction toEntity(TransactionDTO dto) {
        Transaction item = new Transaction();
        item.setId(dto.getId());
        item.setProductTypeId(dto.getProductTypeId());
        item.setProductType(dto.getProductType());
        item.setCustomerId(dto.getCustomerId());
        item.setTransactionType(dto.getTransactionType());
        item.setAmount(dto.getAmount());
        item.setStatus(dto.getStatus());
        return item;
    }

    public static TransactionDTO toDto(Transaction item) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(item.getId());
        dto.setProductTypeId(item.getProductTypeId());
        dto.setProductType(item.getProductType());
        dto.setCustomerId(item.getCustomerId());
        dto.setTransactionType(item.getTransactionType());
        dto.setAmount(item.getAmount());
        dto.setStatus(item.getStatus());
        return dto;
    }
}
