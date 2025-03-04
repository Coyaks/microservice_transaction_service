package com.skoy.microservice_transaction_service.service;

import com.skoy.microservice_transaction_service.dto.BankAccountDTO;
import com.skoy.microservice_transaction_service.dto.CreditDTO;
import com.skoy.microservice_transaction_service.dto.TransactionDTO;
import com.skoy.microservice_transaction_service.dto.CustomerDTO;
import com.skoy.microservice_transaction_service.enums.ProductTypeEnum;
import com.skoy.microservice_transaction_service.mapper.TransactionMapper;
import com.skoy.microservice_transaction_service.model.Transaction;
import com.skoy.microservice_transaction_service.model.UpdateBalanceRequest;
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

    @Value("${credit.service.url}")
    private String creditServiceUrl;

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
    public Mono<TransactionDTO> create(TransactionDTO transactionDto) {
        return checkCustomer(transactionDto.getCustomerId())
                .flatMap(customer -> {
                    if(transactionDto.getProductType() == ProductTypeEnum.BANK_ACCOUNT){
                        return checkBankAccount(transactionDto.getProductTypeId());
                    }else{
                        return checkCredit(transactionDto.getProductTypeId());
                    }

                })
                .flatMap(bankAccountOrCredit -> {
                    return repository.save(TransactionMapper.toEntity(transactionDto))
                            .map(TransactionMapper::toDto);
                });
    }*/

    @Override
    public Mono<TransactionDTO> create(TransactionDTO transactionDto) {
        return checkCustomer(transactionDto.getCustomerId())
                .flatMap(customer -> {
                    if (transactionDto.getProductType() == ProductTypeEnum.BANK_ACCOUNT) {
                        return checkBankAccount(transactionDto.getProductTypeId());
                    } else {
                        return checkCredit(transactionDto.getProductTypeId());
                    }
                })
                .flatMap(bankAccountOrCredit -> {
                    return repository.save(TransactionMapper.toEntity(transactionDto))
                            .map(TransactionMapper::toDto)
                            .flatMap(savedTransaction -> {
                                if(transactionDto.getProductType() == ProductTypeEnum.BANK_ACCOUNT){
                                    return updateBankAccountBalance(transactionDto)
                                            .thenReturn(savedTransaction);
                                }else if(transactionDto.getProductType() == ProductTypeEnum.CREDIT){
                                    return updateCreditBalance(transactionDto)
                                            .thenReturn(savedTransaction);

                                }else{
                                    return Mono.error(new RuntimeException("Tipo de producto no soportado"));
                                }
                            });
                });
    }

    private Mono<Void> updateBankAccountBalance(TransactionDTO transactionDto) {
        return webClientBuilder.build()
                .post()
                .uri(accountServiceUrl+"/bank_accounts/update_balance")
                .bodyValue(new UpdateBalanceRequest(
                        transactionDto.getProductTypeId(),
                        transactionDto.getTransactionType(),
                        transactionDto.getAmount()))
                .retrieve()
                .bodyToMono(Void.class);
    }

    private Mono<Void> updateCreditBalance(TransactionDTO transactionDto) {
        return webClientBuilder.build()
                .post()
                .uri(creditServiceUrl+"/credits/update_balance")
                .bodyValue(new UpdateBalanceRequest(
                        transactionDto.getProductTypeId(),
                        transactionDto.getTransactionType(),
                        transactionDto.getAmount()))
                .retrieve()
                .bodyToMono(Void.class);
    }


    private Mono<CustomerDTO> checkCustomer(String customerId) {
        return webClientBuilder.build()
                .get()
                .uri(customerServiceUrl + "/customers/" + customerId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<CustomerDTO>>() {
                })
                .flatMap(rspCustomer -> {
                    CustomerDTO customer = rspCustomer.getData();
                    if (customer == null) {
                        return Mono.error(new RuntimeException("Cliente no encontrado"));
                    }
                    return Mono.just(customer);
                });
    }

    private Mono<BankAccountDTO> checkBankAccount(String productTypeId) {
        return webClientBuilder.build()
                .get()
                .uri(accountServiceUrl + "/bank_accounts/" + productTypeId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<BankAccountDTO>>() {
                })
                .flatMap(rspBankAccount -> {
                    BankAccountDTO bankAccount = rspBankAccount.getData();
                    if (bankAccount == null) return Mono.error(new RuntimeException("Cuenta bancaria no encontrada"));
                    return Mono.just(bankAccount);
                });
    }

    private Mono<CreditDTO> checkCredit(String productTypeId) {
        return webClientBuilder.build()
                .get()
                .uri(creditServiceUrl + "/credits/" + productTypeId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<CreditDTO>>() {
                })
                .flatMap(rspCredit -> {
                    CreditDTO credit = rspCredit.getData();
                    if (credit == null) return Mono.error(new RuntimeException("Credito no encontrado"));
                    return Mono.just(credit);
                });
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

    @Override
    public Flux<TransactionDTO> findByCustomerId(String customerId) {
        return repository.findByCustomerId(customerId)
                .map(TransactionMapper::toDto);
    }

}