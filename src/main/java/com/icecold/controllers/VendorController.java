package com.icecold.controllers;

import com.icecold.types.Order;
import com.icecold.types.OrderRowMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class VendorController {

    private static final Logger log = LoggerFactory.getLogger(VendorController.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @RequestMapping(value="/total", method=RequestMethod.GET)
    public int total() {
        log.info("Querying for total amount from sales");

        final Integer total = jdbcTemplate.queryForObject("SELECT SUM(quantity) FROM orders WHERE complete = true",
                Integer.class);

        return total == null ? 0 : total;
    }

    @RequestMapping(value="/sales", method=RequestMethod.GET)
    public List<Order> sales() {
        log.info("Querying for a list of all sales");

        final List<Order> result = jdbcTemplate.query(
                "SELECT customer, flavor, quantity, complete FROM orders WHERE complete = true",
                new Object[] {}, new OrderRowMapper());

        return result;
    }
}
