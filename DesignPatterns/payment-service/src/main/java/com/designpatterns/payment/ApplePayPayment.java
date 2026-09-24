package com.designpatterns.payment;

public class ApplePayPayment implements PaymentMethod {
    @Override
    public void pay(double amount) {
        System.out.println("Processing Apple Pay payment of $" + amount);
    }
    
}
