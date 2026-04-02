package com.example.payments;

public class SafeCashAdapter implements PaymentGateway {
    public final SafeCashClient client;

    public SafeCashAdapter(SafeCashClient client) {
        this.client = client;
    }

    @Override
    public String charge(String customerId, int amountCents) {
        return client.createPayment(amountCents, customerId).confirm();
    }
}
