package com.transactions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransSalesPerMonth {
    private int year;
    private int month;
    private double totalSales;
}
