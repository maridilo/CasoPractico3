package com.example.casopractico3.orders;

public class Order {

    private final int id;
    private final double total;
    private final String customerName;

    public Order(int id, double total, String customerName) {
        this.id = id;
        this.total = total;
        this.customerName = customerName;
    }

    public int getId() {
        return id;
    }

    public double getTotal() {
        return total;
    }

    public String getCustomerName() {
        return customerName;
    }

    @Override
    public String toString() {
        return "Order{id=" + id +
                ", total=" + total +
                ", customerName='" + customerName + '\'' +
                '}';
    }
}
