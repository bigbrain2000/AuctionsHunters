package com.auctions.hunters.controller;

import com.auctions.hunters.exceptions.PayPalPaymentException;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.PaymentRequest;
import com.auctions.hunters.service.auction.AuctionService;
import com.auctions.hunters.service.paypal.PayPalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@Controller
@RequestMapping(produces = APPLICATION_JSON_VALUE)
public class PaymentController {

    private final PayPalService payPalService;
    private final AuctionService auctionService;

    private static final String PAYMENT_ERROR_TEMPLATE = "/paymentError";

    public PaymentController(PayPalService payPalService,
                             AuctionService auctionService) {
        this.payPalService = payPalService;
        this.auctionService = auctionService;
    }

    @GetMapping("/pay")
    public String getPaymentLobby(Model model) {
        List<Car> carsToBuyList = auctionService.getCarsFromFinishedAuctionsForBuyerId();

        if(carsToBuyList.isEmpty()) {
            return "/no_auction_to_pay";
        }

        List<Float> finishedAuctionsCurrentPrice = auctionService.getFinishedAuctionsCurrentPrice();
        Float totalPrice = auctionService.getTotalPriceToPay();

        model.addAttribute("carsToBuyList", carsToBuyList);
        model.addAttribute("carsPriceList", finishedAuctionsCurrentPrice);
        model.addAttribute("totalPrice", totalPrice);

        return "/payment";
    }

    @PostMapping("/pay")
    public String submitPayment() {
        try {
            Double totalPrice = Double.valueOf(auctionService.getTotalPriceToPay());
            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setTotalAmount(totalPrice);

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
    public String getSuccessPayment(@RequestParam("paymentId") String paymentId,
                                    @RequestParam("PayerID") String payerId) {

        try {
            Payment payment = payPalService.executePayment(paymentId, payerId);

            if (payment.getState().equals("approved")) {
                auctionService.updateFinishedAuctionsStatusAsSold();
                return "/paymentSuccess";
            }
        } catch (PayPalPaymentException e) {
            return PAYMENT_ERROR_TEMPLATE;
        }

        return "redirect:/";
    }
}
