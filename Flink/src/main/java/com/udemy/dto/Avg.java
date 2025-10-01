package com.udemy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Avg {
    private String month;
    private String category;
    private String productName;
    private Integer profit;
    private Integer count;

    public static Avg split(String value) {
        String[] words = value.split(",");
        return new Avg(words[1], words[2], words[3], Integer.parseInt(words[4]), 1);
    }
}
// {01-06-2018},{June},{Category5},{Bat}.{12}