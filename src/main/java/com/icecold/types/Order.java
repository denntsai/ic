package com.icecold.types;

public class Order {
    private long customer;
    private String flavor;
    private int quantity;
    boolean complete;

    public Order(long customer, String flavor, int quantity, boolean complete) {
        this.customer = customer;
        this.flavor = flavor;
        this.quantity = quantity;
        this.complete = complete;
    }

    public long getCustomer() {
        return customer;
    }

    public void setCustomer(long customer) {
        this.customer = customer;
    }

    public String getFlavor() {
        return flavor;
    }

    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean getComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String toString() {
        return String.format("Order[customer='%d', flavor='%s', quantity='%d', complete='%b']",
                customer, flavor, quantity, complete);
    }
}
