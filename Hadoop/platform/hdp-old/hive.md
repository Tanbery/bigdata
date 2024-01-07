# Hive Scripts

```shell
create database test;

//Create Table
create table test.bank(
    age int,
    job string,
    marital string,
    education string,
    default string,
    balance int,
    housing string,
    loan string,
    contact string,
    day int,
    month string,
    duration int,
    campaign int,
    pdays int,
    previous int,
    poutcome string,
    y string) 
ROW FORMAT delimited fields terminated by ';' LINES terminated by '\n' STORED AS TEXTFILE;

```