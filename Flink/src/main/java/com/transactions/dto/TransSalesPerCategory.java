package com.transactions.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;



@Data
@AllArgsConstructor
public class TransSalesPerCategory {
    private Date transactionDate;
    private String category;
    private Double totalSales;
}
