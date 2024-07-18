package com.example.library_app_back_end.controller;

import com.example.library_app_back_end.constant.ErrorConstant;
import com.example.library_app_back_end.constant.ValidateConstant;
import com.example.library_app_back_end.dto.PaymentDTO;
import com.example.library_app_back_end.error.ValidationException;
import com.example.library_app_back_end.response.ApiResponse;
import com.example.library_app_back_end.service.PaymentService;
import com.example.library_app_back_end.utils.ExtractJWT;
import com.stripe.model.PaymentIntent;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Log4j2
@RestController
@CrossOrigin(origins = "https://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/payments/secure")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ApiResponse<?> getPayment(@RequestHeader(value = "Authorization") String token) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        if (Objects.isNull(userEmail)){
            return new ApiResponse<>(400, null, ErrorConstant.ERROR_TOKEN, ErrorConstant.MISSING_USER_EMAIL);
        }
        log.debug("REST request to get payment for user: %", userEmail);
        PaymentDTO dto;
        try {
            dto = paymentService.getPaymentByUserEmail(userEmail);
        } catch (ValidationException e) {
            return new ApiResponse<>(400, null, e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return new ApiResponse<>(400, null, ValidateConstant.ERROR, ValidateConstant.ERROR_LABEL);
        }
        return new ApiResponse<>(200, dto, null, null);
    }
    @RequestMapping(value = "/payment-intent", method = RequestMethod.POST)
    public ApiResponse<?> createPaymentIntent(@RequestHeader(value = "Authorization") String token,
            @RequestBody PaymentDTO paymentRequest) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");

        if (Objects.isNull(userEmail)){
            return new ApiResponse<>(400, null, ErrorConstant.ERROR_TOKEN
                    , ErrorConstant.MISSING_USER_EMAIL);
        }
        log.debug("REST request to create Payment Intent for user: %", userEmail);

        PaymentIntent paymentIntent;
        try {
            paymentIntent = paymentService.createPaymentIntent(paymentRequest);
        }catch (ValidationException e){
            return new ApiResponse<>(400, null, e.getErrorCode(), e.getErrorMessage());
        }catch (Exception e){
            return new ApiResponse<>(400, null, e.getMessage(), ValidateConstant.ERROR_LABEL);
        }
        String clientSecret = paymentIntent.getClientSecret();
        return new ApiResponse<>(200, clientSecret, null
                , null);
    }
//    @RequestMapping(value = "/payment-intent", method = RequestMethod.POST)
//    public ResponseEntity<String> createPaymentIntent(@RequestBody PaymentDTO paymentInfoRequest)
//                throws StripeException {
//
//            PaymentIntent paymentIntent = paymentService.createPaymentIntent(paymentInfoRequest);
//            String paymentStr = paymentIntent.toJson();
//
//            return new ResponseEntity<>(paymentStr, HttpStatus.OK);
//        }

    @RequestMapping(value = "/payment-complete", method = RequestMethod.PATCH)
    public ApiResponse<?> stripePaymentComplete(@RequestHeader(value = "Authorization") String token){
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        if (Objects.isNull(userEmail)){
            return new ApiResponse<>(400, null, ErrorConstant.ERROR_TOKEN, ErrorConstant.MISSING_USER_EMAIL);
        }
        log.debug("REST request to complete payment for user: %", userEmail);
        try {
           paymentService.stripePayment(userEmail);
        }catch (ValidationException e){
            return new ApiResponse<>(400, null, e.getErrorCode(), e.getErrorMessage());
        }catch (Exception e){
            return new ApiResponse<>(400, null, ValidateConstant.ERROR, ValidateConstant.ERROR_LABEL);
        }
        return new ApiResponse<>(200, HttpStatus.OK, null
                , null);
    }

}
