package com.udemy.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MyApp {
    public static final ObjectMapper jsonMapper = new ObjectMapper();

    public final static int portNumber=9090;
    
    public final static String kafkaTopic= "financial_transactions";

    public static final String jdbcDriver ="org.postgresql.Driver";
    public static final String jdbcUrl ="jdbc:postgresql://localhost:5432/postgres";
    public static final String jdbcUser = "postgres";
    public static final String jdbcPass = "postgres";

    public static ArrayList<String> readFile(String filename) {
        InputStream in = MyApp.class.getClassLoader().getResourceAsStream(filename);
        ArrayList<String> out = new ArrayList<>();
        Scanner s = new Scanner(in);
        s.useDelimiter("\n");
        while(s.hasNext()) {
            out.add(s.next());
        }
        s.close();
        return out;
    }

}
