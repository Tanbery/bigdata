package com.transactions.dto;

import lombok.Data;

import java.io.IOException;
import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.util.MyApp;

@Data
public class Transaction {
    private String transactionId;
    private String productId;
    private String productName;
    private String productCategory;
    private Double productPrice;
    private Integer productQuantity;
    private String productBrand;
    private String currency;
    private String customerId;
    private Timestamp transactionDate;
    private String paymentMethod;
    private Double totalAmount;

    public static Transaction fromJSON(String json) throws IOException{
        return MyApp.jsonMapper.readValue(json, Transaction.class);
    }
    public static String toJSON(Transaction transaction){
        try {
            return MyApp.jsonMapper.writeValueAsString(transaction);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
