package com.designpatterns.payment;

public class PaymentService {

    private final PaymentMethod paymentMethod;

    public PaymentService(String method) {
        PaymentMethodFactory factory = new PaymentMethodFactory();
        this.paymentMethod = factory.create(method);
    }

    public void pay(double amount) {

        if (paymentMethod != null) {
            paymentMethod.pay(amount);
        }
    }
}