package com.icecold.controllers;

import java.lang.Integer;
import java.lang.Long;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.icecold.types.Flavor;
import com.icecold.types.FlavorRowMapper;
import com.icecold.types.Order;
import com.icecold.types.OrderRowMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final AtomicLong counter = new AtomicLong();

    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * Redirects the user to the main ordering page.
     */
    @RequestMapping(value={"/", "/order", "/cart", "/checkout"}, method=RequestMethod.GET)
    public ModelAndView order() {
        ModelAndView model = new ModelAndView("order");
        return model;
    }

    /**
     * Creates a customer ID for the current user.
     */
    @RequestMapping(value="/user", method=RequestMethod.GET)
    public Long user() {
        final long cid = counter.getAndIncrement();
        log.info("Generating ID for customer: " + cid);
        return cid;
    }

    /**
     * Gets the list of available flavors.
     */
    @RequestMapping(value="/flavors", method=RequestMethod.GET)
    public List<Flavor> flavors(Model model) {
        log.info("Fetching flavors");

        return jdbcTemplate.query("SELECT * FROM flavors", new Object[] {}, new FlavorRowMapper());
    }

    /**
     * Updates the users cart.
     * Flavor quantities and customer ID should be passed in as a query string.
     * Example payload: customerId=10&chocolate=1&vanilla=2
     */
    @RequestMapping(value="/cart", method=RequestMethod.POST)
    public ModelAndView postCart(@RequestBody String payload) {
        long customerId = -1;

        String[] pairs = payload.split("&");
        Map<String, Integer> params = new HashMap<String, Integer>();
        for (int i = 0; i < pairs.length; i++) {
            String[] pair = pairs[i].split("=");
            if (pair.length == 2) {
                if (pair[0].equals("customerId")) {
                    customerId = Long.valueOf(pair[1]);
                } else {
                    int value = Integer.parseInt(pair[1]);
                    if (value > 0) {
                        params.put(pair[0], value);
                    }
                }
            }
        }

        log.info("Updating cart for customer: " + customerId);
        for (Map.Entry<String, Integer> entry : params.entrySet()) {
            final String flavor = entry.getKey();
            final int quantity = entry.getValue();

            // If there is already an incomplete for this customer and flavor, update the entry.
            int count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM orders WHERE customer = " + customerId + " AND flavor = '" + flavor +
                    "' AND complete = false", Integer.class);

            if (count > 0) {
                int oldQuantity = jdbcTemplate.queryForObject(
                        "SELECT quantity FROM orders WHERE customer = " + customerId + " AND flavor = '" +
                        flavor + "' AND complete = false", Integer.class);

                int total = quantity + oldQuantity;

                jdbcTemplate.update("UPDATE orders SET quantity = " + total + " WHERE customer = " + customerId +
                        " AND flavor = '" + flavor + "' AND complete = false");
            } else {
                jdbcTemplate.update("INSERT INTO orders(customer, flavor, quantity, complete) VALUES (? , ?, ?, ?)",
                        new Object[] { customerId, flavor, quantity, false });
            }
        }

        ModelAndView model = new ModelAndView("cart");
        return model;
    }

    /**
     * Gets the total cost for the user's cart.
     */
    @RequestMapping(value="/cart/{id}/total", method=RequestMethod.GET)
    public int cartTotal(@PathVariable Long id) {
        log.info("Fetching cart total for customer: " + id);

        final Integer result = jdbcTemplate.queryForObject(
                "SELECT SUM(quantity) FROM orders WHERE customer = ? AND complete = false",
                new Object[] { id }, Integer.class);

        return result == null ? 0 : result;
    }

    /**
     * Gets the user's cart.
     */
    @RequestMapping(value="/cart/{id}", method=RequestMethod.GET)
    public List<Order> getCart(@PathVariable Long id) {
        log.info("Fetching cart for customer: " + id);

        final List<Order> result = jdbcTemplate.query(
                "SELECT * FROM orders WHERE customer = ? AND complete = false",
                new Object[] { id }, new OrderRowMapper());

        return result;
    }

    /**
     * Completes the user's order.
     */
    @RequestMapping(value="/checkout", method=RequestMethod.POST)
    public ModelAndView checkout(@RequestParam(value="customerId", required=true) Long customerId) {
        log.info("Checking out for user: " + customerId);

        ModelAndView model = new ModelAndView("confirmation");

        jdbcTemplate.update("UPDATE orders SET complete = true WHERE customer = " + customerId +
                " AND complete = false");

        return model;
    }
}
