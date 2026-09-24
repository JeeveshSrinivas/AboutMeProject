package com.designpatterns.payment;

public class CreditCardPayment implements PaymentMethod{
    @Override 
    public void pay (double amount) {
        System.out.println("Processing credit card payment of $" + amount);
    }
}
