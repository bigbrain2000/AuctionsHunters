package com.auctions.hunters.controller;

import com.auctions.hunters.exceptions.PayPalPaymentException;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.PaymentRequest;
import com.auctions.hunters.service.auction.AuctionService;
import com.auctions.hunters.service.paypal.PayPalService;
import com.paypal.api.payments.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.ui.Model;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class PaymentControllerTest {

    @Mock
    private PayPalService payPalService;
    @Mock
    private AuctionService auctionService;

    private PaymentController uut;

    @BeforeEach
    void setUp() {
        openMocks(this);
        uut = spy(new PaymentController(payPalService, auctionService));
    }

    @Mock
    private Model modelAtr;
    @Mock
    private Car car;
    @Mock
    private PaymentRequest paymentRequest;
    @Mock
    private Payment payment;

    @Test
    void getPaymentLobby_emptyFinishedAuctionList_returnsEmpty() {
        when(auctionService.getCarsFromFinishedAuctionsForBuyerId()).thenReturn(Collections.emptyList());

        String result = uut.getPaymentLobby(modelAtr);

        assertEquals("/no_auction_to_pay", result);
        verify(auctionService, times(1)).getCarsFromFinishedAuctionsForBuyerId();
    }

    @Test
    void getPaymentLobby_populatedFinishedAuctionList_returnsPopulatedList() {
        when(auctionService.getCarsFromFinishedAuctionsForBuyerId()).thenReturn(Collections.singletonList(car));
        when(auctionService.getFinishedAuctionsCurrentPrice()).thenReturn(Collections.singletonList(2000f));
        when(auctionService.getTotalPriceToPay()).thenReturn(2000f);

        String result = uut.getPaymentLobby(modelAtr);

        assertEquals("/payment", result);
        verify(auctionService, times(1)).getCarsFromFinishedAuctionsForBuyerId();
    }

    @Test
    void submitPayment_paymentIsSuccessful_returnsSuccess() throws PayPalPaymentException {
        float totalPrice = 22.14f;
        when(auctionService.getTotalPriceToPay()).thenReturn(totalPrice);
        when(payPalService.createPayment(any(PaymentRequest.class))).thenReturn(payment);

        String result = uut.submitPayment();

        assertEquals("redirect:/", result);
        verify(auctionService, times(1)).getTotalPriceToPay();
        verify(payPalService, times(1)).createPayment(any(PaymentRequest.class));
    }

    @Test
    void submitPayment_paymentFails_throwsException() throws PayPalPaymentException {
        float totalPrice = 22.14f;
        when(auctionService.getTotalPriceToPay()).thenReturn(totalPrice);
        when(payPalService.createPayment(any(PaymentRequest.class))).thenThrow(PayPalPaymentException.class);

        String result = uut.submitPayment();

        assertEquals("/paymentError", result);
        verify(auctionService, times(1)).getTotalPriceToPay();
        verify(payPalService, times(1)).createPayment(any(PaymentRequest.class));
    }

    @Test
    void cancelPayment() {
        assertEquals("/paymentError", uut.cancelPayment());
    }

    @Test
    void getSuccessPayment_paymentIsSuccessful_returnsSuccess() throws PayPalPaymentException {
        Payment payment = new Payment();
        payment.setState("approved");
        when(payPalService.executePayment(anyString(), anyString())).thenReturn(payment);
        doNothing().when(auctionService).updateFinishedAuctionsStatusAsSold();

        String result = uut.getSuccessPayment("paymentId", "payerId");

        assertEquals("/paymentSuccess", result);
        verify(payPalService, times(1)).executePayment(anyString(), anyString());
        verify(auctionService, times(1)).updateFinishedAuctionsStatusAsSold();
    }

    @Test
    void getSuccessPayment_paymentFails_throwsException() throws PayPalPaymentException {
        when(payPalService.executePayment(anyString(), anyString())).thenThrow(PayPalPaymentException.class);

        String result = uut.getSuccessPayment("paymentId", "payerId");

        assertEquals("/paymentError", result);
        verify(payPalService, times(1)).executePayment(anyString(), anyString());
    }
}