package com.skoy.bootcamp_microservices.mapper;

import com.skoy.bootcamp_microservices.dto.TransactionDTO;
import com.skoy.bootcamp_microservices.model.Transaction;

public class TransactionMapper {

    public static Transaction toEntity(TransactionDTO dto) {
        Transaction item = new Transaction();
        item.setId(dto.getId());
        item.setProductTypeId(dto.getProductTypeId());
        item.setProductType(dto.getProductType());
        item.setCustomerId(dto.getCustomerId());
        item.setTransactionType(dto.getTransactionType());
        item.setCardType(dto.getCardType());
        item.setCardId(dto.getCardId());
        item.setAmount(dto.getAmount());
        item.setStatus(dto.getStatus());
        item.setCreatedAt(dto.getCreatedAt());
        item.setCommissionAmount(dto.getCommissionAmount());
        return item;
    }

    public static TransactionDTO toDto(Transaction item) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(item.getId());
        dto.setProductTypeId(item.getProductTypeId());
        dto.setProductType(item.getProductType());
        dto.setCustomerId(item.getCustomerId());
        dto.setTransactionType(item.getTransactionType());
        dto.setCardType(item.getCardType());
        dto.setCardId(item.getCardId());
        dto.setAmount(item.getAmount());
        dto.setStatus(item.getStatus());
        dto.setCreatedAt(item.getCreatedAt());
        dto.setCommissionAmount(item.getCommissionAmount());
        return dto;
    }
}
