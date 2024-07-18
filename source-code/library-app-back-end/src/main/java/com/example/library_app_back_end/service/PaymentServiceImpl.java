package com.example.library_app_back_end.service;

import com.example.library_app_back_end.constant.ErrorConstant;
import com.example.library_app_back_end.dao.PaymentRepository;
import com.example.library_app_back_end.dto.PaymentDTO;
import com.example.library_app_back_end.entity.Payment;
import com.example.library_app_back_end.error.ValidationException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Log4j2
@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private PaymentRepository paymentRepository;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, @Value("${stripe.key.secret}") String secretKey) {
        this.paymentRepository = paymentRepository;
        Stripe.apiKey = secretKey;
    }

    public PaymentIntent createPaymentIntent(PaymentDTO paymentRequest) throws StripeException {
        if (Objects.isNull(paymentRequest.getAmount())) {
            throw new ValidationException(ErrorConstant.BLANK_FIELD, String.format(ErrorConstant.BLANK_FIELD_LABEL, "amount"));
        }
        if (Objects.isNull(paymentRequest.getCurrency())) {
            throw new ValidationException(ErrorConstant.BLANK_FIELD, String.format(ErrorConstant.BLANK_FIELD_LABEL, "currency"));
        }
        List<String> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add("card");

        Map<String, Object> params = new HashMap<>();
        params.put("amount", (int) paymentRequest.getAmount());
        params.put("currency", paymentRequest.getCurrency());
        params.put("payment_method_types", paymentMethodTypes);

        return PaymentIntent.create(params);
    }

    public void stripePayment(String userEmail) throws Exception {
        Payment payment = paymentRepository.findFirstByUserEmail(userEmail).orElseThrow(()
                -> new ValidationException(ErrorConstant.NOT_FOUND_OBJECT,
                String.format(ErrorConstant.NOT_FOUND_OBJECT_LABEL, ErrorConstant.PAYMENT)));
        payment.setAmount(0.00);
        paymentRepository.save(payment);
    }

    @Override
    public PaymentDTO getPaymentByUserEmail(String userEmail) {
        Payment payment = paymentRepository.findFirstByUserEmail(userEmail).orElseThrow(()
                -> new ValidationException(ErrorConstant.NOT_FOUND_OBJECT,
                String.format(ErrorConstant.NOT_FOUND_OBJECT_LABEL, ErrorConstant.PAYMENT)));
        PaymentDTO paymentDTO = PaymentDTO.builder()
                .amount(payment.getAmount())
                .userEmail(payment.getUserEmail())
                .build();
        return paymentDTO;
    }


}
