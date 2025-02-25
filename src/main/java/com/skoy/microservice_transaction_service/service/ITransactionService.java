package com.skoy.microservice_transaction_service.service;

import com.skoy.microservice_transaction_service.dto.TransactionDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ITransactionService {

    Flux<TransactionDTO> findAll();
    Mono<TransactionDTO> findById(String id);
    Mono<TransactionDTO> create(TransactionDTO accountDTO);
    Mono<TransactionDTO> update(String id, TransactionDTO accountDTO);
    Mono<Void> delete(String id);
}
