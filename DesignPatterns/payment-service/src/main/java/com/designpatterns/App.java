package com.designpatterns;


import com.designpatterns.payment.CreditCardPayment;
import com.designpatterns.payment.PayPalPayment;
import com.designpatterns.payment.PaymentService;
import com.designpatterns.payment.ApplePayPayment;


public class App 
{
    public static void main( String[] args )
    {
       PaymentService creditCardPaymentService = new PaymentService(new CreditCardPayment());
        creditCardPaymentService.processPayment(100.0);

        PaymentService payPalPaymentService = new PaymentService(new PayPalPayment());
        payPalPaymentService.processPayment(200.0);

        PaymentService applePayPaymentService = new PaymentService(new ApplePayPayment());
        applePayPaymentService.processPayment(300.0);
    }
}
