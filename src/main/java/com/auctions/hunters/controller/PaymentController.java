package com.auctions.hunters.controller;

import com.auctions.hunters.exceptions.PayPalPaymentException;
import com.auctions.hunters.model.PaymentRequest;
import com.auctions.hunters.service.paypal.PayPalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@Controller
@RequestMapping(produces = APPLICATION_JSON_VALUE)
public class PaymentController {

    private final PayPalService payPalService;

    private static final String PAYMENT_ERROR_TEMPLATE = "/paymentError";

    public PaymentController(PayPalService payPalService) {
        this.payPalService = payPalService;
    }

    @GetMapping("/pay")
    public String getPaymentLobby() {
        return "/payment";
    }

    @PostMapping("/pay")
    public String submitPayment(@ModelAttribute("paymentRequest") @NotNull @Valid PaymentRequest paymentRequest) {

        try {
            Payment payment = payPalService.createPayment(paymentRequest);

            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return "redirect:" + link.getHref();
                }
            }

        } catch (PayPalPaymentException e) {
            return PAYMENT_ERROR_TEMPLATE;
        }

        return "redirect:/";
    }

    @GetMapping("/pay/cancel")
    public String cancelPayment() {
        return PAYMENT_ERROR_TEMPLATE;
    }

    @GetMapping("/pay/success")
    public String getSuccessPayment(@RequestParam("payment_id") String paymentId, @RequestParam("payer_id") String payerId) {
        try {
            Payment payment = payPalService.executePayment(paymentId, payerId);

            if (payment.getState().equals("approved")) {
                return "/paymentSuccess";
            }

        } catch (PayPalPaymentException e) {
            return PAYMENT_ERROR_TEMPLATE;
        }

        return "redirect:/";
    }
}
