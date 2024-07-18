package com.example.library_app_back_end.service;

import com.example.library_app_back_end.dto.PaymentDTO;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.http.ResponseEntity;

public interface PaymentService {

    PaymentIntent createPaymentIntent(PaymentDTO paymentRequest) throws StripeException;

    void stripePayment(String userEmail) throws Exception;

    PaymentDTO getPaymentByUserEmail(String userEmail);

}
