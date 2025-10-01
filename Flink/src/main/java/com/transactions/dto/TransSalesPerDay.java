package com.transactions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;

@Data
@AllArgsConstructor
public class TransSalesPerDay {
    private Date transactionDate;
    private Double totalSales ;
}
