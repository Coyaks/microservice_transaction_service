package com.skoy.microservice_transaction_service.controller;

import com.skoy.microservice_transaction_service.dto.TransactionDTO;
import com.skoy.microservice_transaction_service.service.ITransactionService;
import com.skoy.microservice_transaction_service.utils.ApiResponse;
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
        logger.info("Fetching all credits");

        return service.findAll()
                .doOnNext(item -> logger.info("credits found: {}", item))
                .doOnComplete(() -> logger.info("All credits fetched successfully."));
    }

    @GetMapping("/{id}")
    public Mono<TransactionDTO> findById(@PathVariable String id) {
        logger.info("Fetching credits with ID: {}", id);
        return service.findById(id)
                .doOnNext(item -> logger.info("credits found: {}", item))
                .doOnError(e -> logger.error("Error fetching credits with ID: {}", id, e));
    }


    @PostMapping
    public Mono<ApiResponse<TransactionDTO>> create(@RequestBody TransactionDTO transactionDto) {
        logger.info("Creating new credits: {}", transactionDto);
        return service.create(transactionDto)
                .map(createdItem -> {
                    if (createdItem != null) {
                        logger.info("credits created successfully: {}", createdItem);
                        return new ApiResponse<>("ok", createdItem, 200);
                    } else {
                        logger.error("Error creating credits");
                        return new ApiResponse<>("error", null, 500);
                    }
                });
    }

    @PutMapping("/{id}")
    public Mono<ApiResponse<TransactionDTO>> update(@PathVariable String id, @RequestBody TransactionDTO transactionDto) {
        logger.info("Updating credits with ID: {}", id);
        return service.findById(id)
                .flatMap(existingItem -> service.update(id, transactionDto)
                        .map(updatedItem -> {
                            logger.info("credits updated successfully: {}", updatedItem);
                            return new ApiResponse<>("Actualizado correctamente", updatedItem, 200);
                        }))
                .switchIfEmpty(Mono.just(new ApiResponse<>("ID no encontrado", null, 404)))
                .doOnError(e -> logger.error("Error updating credits with ID: {}", id, e));
    }

    @DeleteMapping("/{id}")
    public Mono<ApiResponse<Void>> delete(@PathVariable String id) {
        logger.info("Deleting credits with ID: {}", id);
        return service.findById(id)
                .flatMap(existingItem -> service.delete(id)
                        .then(Mono.just(new ApiResponse<Void>("Eliminado correctamente", null, 200))))
                .switchIfEmpty(Mono.just(new ApiResponse<Void>("ID no encontrado", null, 404)))
                .onErrorResume(e -> {
                    logger.error("Error deleting credits with ID: {}", id, e);
                    return Mono.just(new ApiResponse<Void>("Error al eliminar", null, 500));
                });
    }


}
