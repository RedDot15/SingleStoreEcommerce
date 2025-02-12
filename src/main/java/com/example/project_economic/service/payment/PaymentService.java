package com.example.project_economic.service.payment;

import com.example.project_economic.dto.request.payment.PaymentRequest;
import com.example.project_economic.dto.response.payment.PaymentCaptureResponse;
import com.example.project_economic.dto.response.payment.PaymentResponse;

public interface PaymentService {
	// Create
	PaymentResponse createPayment(PaymentRequest paymentRequest);

	// Complete
	PaymentCaptureResponse completePayment(String paymentId);

	// Cancel
	PaymentCaptureResponse cancelPayment(String paymentId);
}
