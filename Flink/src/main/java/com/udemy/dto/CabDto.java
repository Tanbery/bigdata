package com.udemy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CabDto {
    // id_4214,PB7526,Sedan,Wanda,yes,Sector 19,Sector 10,5
    private String cabId;
    private String plateNum;
    private String type;
    private String driver;
    private Boolean isReached;
    private String locPickup;
    private String locDestination;
    private Integer passengerNumber;
    private Integer number;

    public static CabDto split(String value) {
        String[] words = value.split(",");
        CabDto data=new CabDto();
        data.setCabId(words[0]);
        data.setPlateNum(words[1]);
        data.setType(words[2]);
        data.setDriver(words[3]);
        if (words[4].equalsIgnoreCase("yes")){   
            data.setIsReached(true);
            data.setLocPickup(words[5]);
            data.setLocDestination(words[6]);
            data.setPassengerNumber(Integer.parseInt(words[7]));
            data.setNumber(1);
        }
        else{
            
            data.setIsReached(false);
            data.setLocPickup("");
            data.setLocDestination("");
            data.setPassengerNumber(0);
            data.setNumber(0);
        }
        return data;
    }
}
