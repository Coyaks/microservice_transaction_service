package com.skoy.microservice_transaction_service.service;

import com.skoy.microservice_transaction_service.dto.BankAccountDTO;
import com.skoy.microservice_transaction_service.dto.TransactionDTO;
import com.skoy.microservice_transaction_service.dto.CustomerDTO;
import com.skoy.microservice_transaction_service.mapper.TransactionMapper;
import com.skoy.microservice_transaction_service.model.Transaction;
import com.skoy.microservice_transaction_service.repository.ITransactionRepository;
import com.skoy.microservice_transaction_service.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService implements ITransactionService {

    private final ITransactionRepository repository;
    private final WebClient.Builder webClientBuilder;
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Value("${customer.service.url}")
    private String customerServiceUrl;

    @Value("${account.service.url}")
    private String accountServiceUrl;

    @Override
    public Flux<TransactionDTO> findAll() {
        return repository.findAll()
                .map(TransactionMapper::toDto);
    }

    @Override
    public Mono<TransactionDTO> findById(String id) {
        return repository.findById(id)
                .map(TransactionMapper::toDto);
    }

    /*@Override
    public Mono<TransactionDTO> create(TransactionDTO accountDTO) {

        return webClientBuilder.build()
                .get()
                .uri(customerServiceUrl + "/customers/" + accountDTO.getCustomerId())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<CustomerDTO>>() {})
                .flatMap(response -> {
                    CustomerDTO customer = response.getData();
                    if (customer != null) {
                        return repository.save(TransactionMapper.toEntity(accountDTO))
                                .map(TransactionMapper::toDto);
                    } else {
                        return Mono.error(new RuntimeException("Cliente no encontrado"));
                    }
                })
                .doOnError(error -> log.error("Error al validar cliente: {}", error.getMessage()));
    }*/


    @Override
    public Mono<TransactionDTO> create(TransactionDTO transactionDto) {
        // Verificar si el cliente existe en Customer Service
        return webClientBuilder.build()
                .get()
                .uri(customerServiceUrl + "/customers/" + transactionDto.getCustomerId()) // URL de Customer Service
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<CustomerDTO>>() {
                })
                .flatMap(rspCustomer -> {

                    CustomerDTO customer = rspCustomer.getData();
                    if (customer == null) {
                        return Mono.error(new RuntimeException("Cliente no encontrado"));
                    }

                    // Verificar si la cuenta existe en BankAccount Service
                    return webClientBuilder.build()
                            .get()
                            .uri(accountServiceUrl + "/bank_accounts/" + transactionDto.getAccountId()) // URL de BankAccount Service
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<ApiResponse<BankAccountDTO>>() {
                            })
                            .flatMap(rspBankAccount -> {
                                BankAccountDTO item = rspBankAccount.getData();
                                if (item == null) {
                                    return Mono.error(new RuntimeException("Cuenta bancaria no encontrada"));
                                }

                                 return repository.save(TransactionMapper.toEntity(transactionDto))
                                            .map(TransactionMapper::toDto);

                            });
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Cliente o cuenta no encontrados")));
    }

    @Override
    public Mono<TransactionDTO> update(String id, TransactionDTO accountDTO) {
        return repository.findById(id)
                .flatMap(existing -> {
                    existing = TransactionMapper.toEntity(accountDTO);
                    existing.setUpdatedAt(LocalDateTime.now());
                    return repository.save(existing);
                })
                .map(TransactionMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }

}