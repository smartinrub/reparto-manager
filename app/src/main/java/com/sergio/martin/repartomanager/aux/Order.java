package com.sergio.martin.repartomanager.aux;

/**
 * Created by Sergio Martin Rubio on 7/24/16.
 */
public class Order {

    private String name;
    private String postCode;
    private double distance;
    private double price;
    private int phoneNumber;
    private String email;

    public Order(String n, String cp, double d, double p, int tel, String mail) {
        name = n;
        postCode = cp;
        distance = d;
        price = p;
        phoneNumber = tel;
        email = mail;
    }

    public String getName() {
        return name;
    }

    public String getPostCode() {
        return postCode;
    }

    public double getDistance() {
        return distance;
    }

    public double getPrice() {
        return price;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "name=" + name +
                ", Codigo Postal=" + postCode +
                ", Distancia=" + distance +
                ", Precio=" + price +
                "}";
    }
}
