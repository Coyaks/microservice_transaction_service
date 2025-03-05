/*
 Navicat Premium Data Transfer

 Source Server         : MONGO_LOCAL
 Source Server Type    : MongoDB
 Source Server Version : 80004 (8.0.4)
 Source Host           : localhost:27017
 Source Schema         : microservice_transaction_service

 Target Server Type    : MongoDB
 Target Server Version : 80004 (8.0.4)
 File Encoding         : 65001

 Date: 05/03/2025 08:08:44
*/


// ----------------------------
// Collection structure for transactions
// ----------------------------
db.getCollection("transactions").drop();
db.createCollection("transactions");

// ----------------------------
// Documents of transactions
// ----------------------------
db.getCollection("transactions").insert([ {
    _id: ObjectId("67c83ac4fa54543ae2ccea75"),
    customerId: "67bc5049652bf702f7552f25",
    productType: "BANK_ACCOUNT",
    productTypeId: "67c822a6c5323d08f13bab0d",
    transactionType: "DEPOSIT",
    amount: "100",
    createdAt: ISODate("2025-03-05T11:51:32.118Z"),
    _class: "com.skoy.bootcamp_microservices.model.Transaction"
} ]);
db.getCollection("transactions").insert([ {
    _id: ObjectId("67c83b93fa54543ae2ccea76"),
    customerId: "67bc5049652bf702f7552f25",
    productType: "BANK_ACCOUNT",
    productTypeId: "67c822a6c5323d08f13bab0d",
    transactionType: "DEPOSIT",
    amount: "200",
    createdAt: ISODate("2025-03-05T11:54:59.807Z"),
    _class: "com.skoy.bootcamp_microservices.model.Transaction"
} ]);
db.getCollection("transactions").insert([ {
    _id: ObjectId("67c844729a85106cad07e309"),
    customerId: "67bc5049652bf702f7552f25",
    productType: "BANK_ACCOUNT",
    productTypeId: "67c822a6c5323d08f13bab0d",
    transactionType: "DEPOSIT",
    amount: "100",
    createdAt: ISODate("2025-03-05T12:32:50.044Z"),
    commissionAmount: "0",
    _class: "com.skoy.bootcamp_microservices.model.Transaction"
} ]);
db.getCollection("transactions").insert([ {
    _id: ObjectId("67c848bba057fe1e6ee0bc30"),
    customerId: "67bc5049652bf702f7552f25",
    productType: "BANK_ACCOUNT",
    productTypeId: "67c822a6c5323d08f13bab0d",
    transactionType: "DEPOSIT",
    amount: "100",
    createdAt: ISODate("2025-03-05T12:51:06.923Z"),
    commissionAmount: "0",
    _class: "com.skoy.bootcamp_microservices.model.Transaction"
} ]);
db.getCollection("transactions").insert([ {
    _id: ObjectId("67c848e7fb135d4ce643371b"),
    customerId: "67bc5049652bf702f7552f25",
    productType: "BANK_ACCOUNT",
    productTypeId: "67c822a6c5323d08f13bab0d",
    transactionType: "DEPOSIT",
    amount: "100",
    createdAt: ISODate("2025-03-05T12:51:51.535Z"),
    commissionAmount: "5.0",
    _class: "com.skoy.bootcamp_microservices.model.Transaction"
} ]);
