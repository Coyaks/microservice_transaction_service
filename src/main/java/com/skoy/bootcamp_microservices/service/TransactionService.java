package com.skoy.bootcamp_microservices.service;

import com.skoy.bootcamp_microservices.dto.BankAccountDTO;
import com.skoy.bootcamp_microservices.dto.CreditDTO;
import com.skoy.bootcamp_microservices.dto.CustomerDTO;
import com.skoy.bootcamp_microservices.dto.TransactionDTO;
import com.skoy.bootcamp_microservices.enums.ProductTypeEnum;
import com.skoy.bootcamp_microservices.mapper.TransactionMapper;
import com.skoy.bootcamp_microservices.model.UpdateBalanceRequest;
import com.skoy.bootcamp_microservices.repository.ITransactionRepository;
import com.skoy.bootcamp_microservices.utils.ApiResponse;
import com.skoy.bootcamp_microservices.utils.Util;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
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

//    @Override
//    public Mono<TransactionDTO> create(TransactionDTO transactionDto) {
//        transactionDto.setCreatedAt(LocalDateTime.now());
//        return checkCustomer(transactionDto.getCustomerId())
//                .flatMap(customer -> {
//                    if (transactionDto.getProductType() == ProductTypeEnum.BANK_ACCOUNT) {
//                        return checkBankAccount(transactionDto.getProductTypeId());
//                    } else {
//                        return checkCredit(transactionDto.getProductTypeId());
//                    }
//                })
//                .flatMap(bankAccountOrCredit -> {
//                    return repository.save(TransactionMapper.toEntity(transactionDto))
//                            .map(TransactionMapper::toDto)
//                            .flatMap(savedTransaction -> {
//                                if (transactionDto.getProductType() == ProductTypeEnum.BANK_ACCOUNT) {
//                                    return updateBankAccountBalance(transactionDto)
//                                            .thenReturn(savedTransaction);
//                                } else if (transactionDto.getProductType() == ProductTypeEnum.CREDIT) {
//                                    return updateCreditBalance(transactionDto)
//                                            .thenReturn(savedTransaction);
//
//                                } else {
//                                    return Mono.error(new RuntimeException("Tipo de producto no soportado"));
//                                }
//                            });
//                });
//    }

    @Override
    public Mono<TransactionDTO> create(TransactionDTO transactionDto) {
        transactionDto.setCreatedAt(LocalDateTime.now());
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
                                if (transactionDto.getProductType() == ProductTypeEnum.BANK_ACCOUNT) {
                                    return updateBankAccountBalance(transactionDto)
                                            .flatMap(commissionAmount -> {
                                                savedTransaction.setCommissionAmount(commissionAmount);
                                                return repository.save(TransactionMapper.toEntity(savedTransaction))
                                                        .map(TransactionMapper::toDto);
                                            });
                                } else if (transactionDto.getProductType() == ProductTypeEnum.CREDIT) {
                                    return updateCreditBalance(transactionDto)
                                            .thenReturn(savedTransaction);
                                } else {
                                    return Mono.error(new RuntimeException("Tipo de producto no soportado"));
                                }
                            });
                });
    }



//    private Mono<Void> updateBankAccountBalance(TransactionDTO transactionDto) {
//        return webClientBuilder.build()
//                .post()
//                .uri(accountServiceUrl + "/bank_accounts/update_balance")
//                .bodyValue(new UpdateBalanceRequest(
//                        transactionDto.getProductTypeId(),
//                        transactionDto.getTransactionType(),
//                        transactionDto.getAmount()))
//                .retrieve()
//                .bodyToMono(Void.class);
//    }

    private Mono<BigDecimal> updateBankAccountBalance(TransactionDTO transactionDto) {
        return webClientBuilder.build()
                .post()
                .uri(accountServiceUrl + "/bank_accounts/update_balance")
                .bodyValue(new UpdateBalanceRequest(
                        transactionDto.getProductTypeId(),
                        transactionDto.getTransactionType(),
                        transactionDto.getAmount()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<BankAccountDTO>>() {})
                .map(response -> {
                    //BigDecimal commissionAmount = response.getDataExtra();
                    BigDecimal commissionAmount = Util.convertObjectToBigDecimal(response.getDataExtra());
                    return commissionAmount;
                });
    }

    private Mono<Void> updateCreditBalance(TransactionDTO transactionDto) {
        return webClientBuilder.build()
                .post()
                .uri(creditServiceUrl + "/credits/update_balance")
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
                    if (customer == null) return Mono.error(new RuntimeException("Cliente no encontrado"));
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

    @Override
    public Mono<Void> deleteAll() {
        return repository.deleteAll();
    }

}