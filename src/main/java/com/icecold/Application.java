package com.icecold;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String args[]) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... strings) throws Exception {

        log.info("Creating tables");

        jdbcTemplate.execute("DROP TABLE orders IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE orders(id SERIAL, customer INTEGER, flavor VARCHAR(255), quantity INTEGER, "
            + "complete BOOLEAN)");

        jdbcTemplate.execute("DROP TABLE flavors IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE flavors(id SERIAL, identifier VARCHAR(255), name VARCHAR(255))");

        /** Insert flavors here **/
        // TODO: Implement POST/PUT to /flavors to update available flavors
        jdbcTemplate.execute("INSERT INTO flavors(name) VALUES ('Chocolate')");
        jdbcTemplate.execute("INSERT INTO flavors(name) VALUES ('Vanilla')");
        jdbcTemplate.execute("INSERT INTO flavors(name) VALUES ('Strawberry')");
        jdbcTemplate.execute("INSERT INTO flavors(name) VALUES ('Mint')");
        jdbcTemplate.execute("INSERT INTO flavors(name) VALUES ('Oreo')");
        jdbcTemplate.execute("INSERT INTO flavors(name) VALUES ('Pumpkin')");
    }
}
