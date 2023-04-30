package com.auctions.hunters.service.paypal;


import com.auctions.hunters.config.PayPalProperties;
import com.auctions.hunters.exceptions.PayPalPaymentException;
import com.auctions.hunters.model.PaymentRequest;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

import static java.math.RoundingMode.HALF_UP;

@Service
@Validated
public class PayPalService {

    @Autowired
    private APIContext apiContext;

    private final PayPalProperties payPalProperties;

    public PayPalService(PayPalProperties payPalProperties) {
        this.payPalProperties = payPalProperties;
    }

    public Payment createPayment(PaymentRequest paymentRequest) throws PayPalPaymentException {
        try {
            Payment payment = buildPayment(paymentRequest);
            return payment.create(apiContext);
        } catch (PayPalRESTException e) {
            throw new PayPalPaymentException("Error creating PayPal payment", e);
        }
    }

    private Payment buildPayment(@NotNull @Valid PaymentRequest paymentRequest) {
        Amount amount = createAmount(paymentRequest.getTotalAmount());
        Transaction transaction = createTransaction(amount);
        Payer payer = createPayer(payPalProperties.getMethod());
        RedirectUrls redirectUrls = createRedirectUrls(payPalProperties.getCancelUrl(), payPalProperties.getSuccessUrl());

        Payment payment = new Payment();
        payment.setIntent(payPalProperties.getIntent());
        payment.setPayer(payer);
        payment.setTransactions(List.of(transaction));
        payment.setRedirectUrls(redirectUrls);

        return payment;
    }

    private Amount createAmount(@NotNull Double total) {
        Amount amount = new Amount();
        amount.setCurrency(payPalProperties.getCurrency());

        total = BigDecimal.valueOf(total).setScale(2, HALF_UP).doubleValue();
        amount.setTotal(String.format("%.2f", total));

        return amount;
    }

    private Transaction createTransaction(@NotNull Amount amount) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);

        return transaction;
    }

    private Payer createPayer(@NotBlank String method) {
        Payer payer = new Payer();
        payer.setPaymentMethod(method);

        return payer;
    }

    private RedirectUrls createRedirectUrls(@NotBlank String cancelUrl, @NotBlank String successUrl) {
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);

        return redirectUrls;
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalPaymentException {
        try {
            Payment payment = new Payment();
            payment.setId(paymentId);

            PaymentExecution paymentExecute = new PaymentExecution();
            paymentExecute.setPayerId(payerId);

            return payment.execute(apiContext, paymentExecute);
        } catch (PayPalRESTException e) {
            throw new PayPalPaymentException("Error executing PayPal payment", e);
        }
    }
}