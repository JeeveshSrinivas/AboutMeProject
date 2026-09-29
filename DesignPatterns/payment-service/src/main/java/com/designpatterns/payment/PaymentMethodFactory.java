package com.designpatterns.payment;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PaymentMethodFactory {

    private final Map<String, Supplier<PaymentMethod>> paymentMethods = new HashMap<>();

    public PaymentMethodFactory() {
        paymentMethods.put("CreditCard", CreditCardPayment::new);
        paymentMethods.put("PayPal", PayPalPayment::new);
        paymentMethods.put("ApplePay", ApplePayPayment::new);
    }

    public PaymentMethod create(String method) {

        Supplier<PaymentMethod> paymentSupplier = paymentMethods.get(method);

        if (paymentSupplier == null) {
            System.out.println(
                    "Payment method is not supported: " + method);
            return null;
        }

        return paymentSupplier.get();
    }
}