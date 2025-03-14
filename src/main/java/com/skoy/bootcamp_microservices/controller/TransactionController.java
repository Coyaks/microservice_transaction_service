package com.skoy.bootcamp_microservices.controller;

import com.skoy.bootcamp_microservices.dto.TransactionDTO;
import com.skoy.bootcamp_microservices.enums.ProductTypeEnum;
import com.skoy.bootcamp_microservices.enums.TransactionTypeEnum;
import com.skoy.bootcamp_microservices.service.ITransactionService;
import com.skoy.bootcamp_microservices.utils.ApiResponse;
import com.skoy.bootcamp_microservices.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private ITransactionService service;

    @GetMapping
    public Flux<TransactionDTO> findAll() {
        logger.info("Fetching all transactions");

        return service.findAll()
                .doOnNext(item -> logger.info("transactions found: {}", item))
                .doOnComplete(() -> logger.info("All transactions fetched successfully."));
    }

    @GetMapping("/{id}")
    public Mono<TransactionDTO> findById(@PathVariable String id) {
        logger.info("Fetching transactions with ID: {}", id);
        return service.findById(id)
                .doOnNext(item -> logger.info("transactions found: {}", item))
                .doOnError(e -> logger.error("Error fetching transactions with ID: {}", id, e));
    }


    @PostMapping
    public Mono<ApiResponse<TransactionDTO>> create(@RequestBody TransactionDTO transactionDto) {
        logger.info("Creating new transactions: {}", transactionDto);
        return service.create(transactionDto)
                .map(createdItem -> {
                    if (createdItem != null) {
                        logger.info("transactions created successfully: {}", createdItem);
                        return new ApiResponse<>("ok", createdItem, Constants.STATUS_OK);
                    } else {
                        logger.error("Error creating transactions");
                        return new ApiResponse<>("error", null, Constants.STATUS_E500);
                    }
                });
    }

    @PutMapping("/{id}")
    public Mono<ApiResponse<TransactionDTO>> update(@PathVariable String id, @RequestBody TransactionDTO transactionDto) {
        logger.info("Updating transactions with ID: {}", id);
        return service.findById(id)
                .flatMap(existingItem -> service.update(id, transactionDto)
                        .map(updatedItem -> {
                            logger.info("transactions updated successfully: {}", updatedItem);
                            return new ApiResponse<>("Actualizado correctamente", updatedItem, Constants.STATUS_OK);
                        }))
                .switchIfEmpty(Mono.just(new ApiResponse<>("ID no encontrado", null, Constants.STATUS_E404)))
                .doOnError(e -> logger.error("Error updating transactions with ID: {}", id, e));
    }

    @DeleteMapping("/{id}")
    public Mono<ApiResponse<Void>> delete(@PathVariable String id) {
        logger.info("Deleting transactions with ID: {}", id);
        return service.findById(id)
                .flatMap(existingItem -> service.delete(id)
                        .then(Mono.just(new ApiResponse<Void>("Eliminado correctamente", null, Constants.STATUS_OK))))
                .switchIfEmpty(Mono.just(new ApiResponse<Void>("ID no encontrado", null, Constants.STATUS_E404)))
                .onErrorResume(e -> {
                    logger.error("Error deleting transactions with ID: {}", id, e);
                    return Mono.just(new ApiResponse<Void>("Error al eliminar", null, Constants.STATUS_E500));
                });
    }

    @DeleteMapping("/all")
    public Mono<ApiResponse<Void>> deleteAll() {
        logger.info("Deleting all transactions");
        return service.deleteAll()
                .then(Mono.just(new ApiResponse<Void>("Eliminado correctamente", null, Constants.STATUS_OK)))
                .onErrorResume(e -> {
                    logger.error("Error deleting all transactions", e);
                    return Mono.just(new ApiResponse<Void>("Error al eliminar", null, Constants.STATUS_E500));
                });
    }

    @GetMapping("/types")
    public TransactionTypeEnum[] findAllTransactionTypes() {
        return TransactionTypeEnum.values();
    }

    @GetMapping("/product_types")
    public ProductTypeEnum[] findAllProductTypes() {
        return ProductTypeEnum.values();
    }

    @GetMapping("/customer/{customerId}")
    public Flux<TransactionDTO> findByCustomerId(@PathVariable String customerId) {
        logger.info("Fetching transactions for customer ID: {}", customerId);
        return service.findByCustomerId(customerId)
                .doOnNext(item -> logger.info("Transaction found: {}", item))
                .doOnComplete(() -> logger.info("All transactions for customer ID {} fetched successfully.", customerId));
    }


}
