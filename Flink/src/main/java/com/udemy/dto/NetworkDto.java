package com.udemy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetworkDto {
    // user_id,network_name,user_IP,user_country,website, click
    // id_4037,Claro Americas,590673587832453,BR,www.futurebazar.com,49
    private String userId;
    private String name;
    private String userIp;
    private String userCountry;
    private String website;
    private Integer clickCount;
    private Integer number;

    public static NetworkDto split(String value) {
        String[] words = value.split(",");
        return new NetworkDto(words[0], words[1], words[2], words[3], words[4], Integer.parseInt(words[5].trim()), 1);
        // return new NetworkDto(words[0], words[1], words[2], words[3], words[4], 5, 1);

    }
}
