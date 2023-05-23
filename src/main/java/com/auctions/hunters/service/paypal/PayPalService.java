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
import java.util.Objects;

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

    /**
     * Creates a {@link Payment} object by setting the Intent,  {@link Payer}, the {@link List} of {@link Transaction} objects,
     * and the {@link RedirectUrls}.
     *
     * @param paymentRequest the request for the payment to be processed
     * @return a {@link Payment} object that represent the processed object obtained after the request was made
     */
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

    /**
     * Creates the total amount for a {@link Payment} entity wrapped in a {@link Amount} object
     *
     * @param total the price that the user needs to pay
     * @return the price that the user needs to pay wrapped in an {@link Amount} object
     */
    private Amount createAmount(Double total) {
        Objects.requireNonNull(total, "total cannot be null");

        Amount amount = new Amount();
        amount.setCurrency(payPalProperties.getCurrency());

        total = BigDecimal.valueOf(total).setScale(2, HALF_UP).doubleValue();
        amount.setTotal(String.format("%.2f", total));

        return amount;
    }

    /**
     * Creates a {@link Transaction} that contains the total price that the user needs to pay.
     *
     * @param amount the price that the user needs to pay wrapped in a {@link Amount} object
     * @return the new created {@link Transaction} object
     */
    private Transaction createTransaction(@NotNull Amount amount) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);

        return transaction;
    }

    /**
     * Creates a {@link Payer} object that will pay the {@link Transaction} total price.
     *
     * @param method the paying method
     * @return the new created {@link Payer} object
     */
    private Payer createPayer(@NotBlank String method) {
        Payer payer = new Payer();
        payer.setPaymentMethod(method);

        return payer;
    }

    /**
     * Creates the redirecting URLS.
     * <p>
     * Components:
     * <ul>
     *     <li>
     *         successUrl - the URL if the payments was successful and the transaction was finished
     *     </li>
     *      <li>
     *         cancelUrl - the URL if the payments was unsuccessful
     *     </li>
     * </ul>
     *
     * @return the URLs set for the {@link RedirectUrls} object
     */
    private RedirectUrls createRedirectUrls(@NotBlank String cancelUrl, @NotBlank String successUrl) {
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);

        return redirectUrls;
    }

    /**
     * Process the created {@link Payment}.
     *
     * @param paymentId the  {@link Payment} ID
     * @param payerId   the id of the user that will pay the price of the  {@link Payment}
     * @return the processed  {@link Payment}
     * @throws PayPalPaymentException is thrown if the {@link Payment} processing fails
     */
    public Payment executePayment(@NotBlank String paymentId, @NotBlank String payerId) throws PayPalPaymentException {
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