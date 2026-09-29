package com.designpatterns;

import com.designpatterns.payment.PaymentService;

public class App {

    public static void main(String[] args) {

        PaymentService creditCardPaymentService = new PaymentService("CreditCard");
        creditCardPaymentService.pay(100.00);

        PaymentService payPalPaymentService = new PaymentService("PayPal");
        payPalPaymentService.pay(200.00);

        PaymentService applePayPaymentService = new PaymentService("ApplePay");
        applePayPaymentService.pay(300.00);
    }
}