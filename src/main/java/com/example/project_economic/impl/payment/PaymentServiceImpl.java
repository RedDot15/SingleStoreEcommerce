package com.example.project_economic.impl.payment;

import com.example.project_economic.dto.request.OrderRequest;
import com.example.project_economic.dto.request.payment.PaymentRequest;
import com.example.project_economic.dto.response.OrderResponse;
import com.example.project_economic.dto.response.payment.PaymentCaptureResponse;
import com.example.project_economic.dto.response.payment.PaymentResponse;
import com.example.project_economic.exception.ErrorCode;
import com.example.project_economic.exception.custom.AppException;
import com.example.project_economic.service.OrderService;
import com.example.project_economic.service.payment.PaymentService;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {
	PayPalHttpClient payPalHttpClient;
	OrderService orderService;

	@Override
	public PaymentResponse createPayment(PaymentRequest paymentRequest) {
		// Define payment
		com.paypal.orders.OrderRequest paypalOrderRequest = new com.paypal.orders.OrderRequest();
		paypalOrderRequest.checkoutPaymentIntent("CAPTURE");
		AmountWithBreakdown amountBreakdown = new AmountWithBreakdown()
				.currencyCode(paymentRequest.getCurrency())
				.value(paymentRequest.getFee().toString());
		PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest().amountWithBreakdown(amountBreakdown);
		paypalOrderRequest.purchaseUnits(List.of(purchaseUnitRequest));
		ApplicationContext applicationContext = new ApplicationContext()
				.returnUrl(paymentRequest.getReturnUrl())
				.cancelUrl(paymentRequest.getSuccessUrl());
		paypalOrderRequest.applicationContext(applicationContext);
		OrdersCreateRequest ordersCreateRequest = new OrdersCreateRequest().requestBody(paypalOrderRequest);
		// Create payment & order
		try {
			// Create payment
			HttpResponse<Order> orderHttpResponse = payPalHttpClient.execute(ordersCreateRequest);
			Order order = orderHttpResponse.result();
			// Get redirect Url
			String redirectUrl = order.links().stream()
					.filter(link -> "approve".equals(link.rel()))
					.findFirst()
					.orElseThrow(NoSuchElementException::new)
					.href();
			// Create new order
			OrderResponse orderResponse = orderService.add(OrderRequest.builder()
					.id(order.id())
					.totalAmount(paymentRequest.getFee())
					.build());
			// Return
			return new PaymentResponse(orderResponse, redirectUrl);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new AppException(ErrorCode.PAYMENT_CREATE_REQUEST_FAILED);
		}
	}

	@PreAuthorize("@securityService.isOrderOwner(#paymentId, authentication.principal.claims['uid'])")
	@Override
	public PaymentCaptureResponse completePayment(String paymentId) {
		OrdersCaptureRequest ordersCaptureRequest = new OrdersCaptureRequest(paymentId);
		try {
			// Update order: success
			OrderResponse orderResponse = orderService.update(
					OrderRequest.builder().id(paymentId).status("Success").build());
			// Excute payment
			HttpResponse<Order> httpResponse = payPalHttpClient.execute(ordersCaptureRequest);
			// Case excute complete
			if (httpResponse.result().status() != null) {
				// Return
				return new PaymentCaptureResponse(paymentId, orderResponse);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
			// Throw exception
			throw new AppException(ErrorCode.PAYMENT_EXCUTE_REQUEST_FAILED);
		}
		throw new AppException(ErrorCode.UNCATEGORIZED);
	}

	@PreAuthorize("@securityService.isOrderOwner(#paymentId, authentication.principal.claims['uid'])")
	@Override
	public PaymentCaptureResponse cancelPayment(String paymentId) {
		// Update order: cancel
		OrderResponse orderResponse = orderService.update(
				OrderRequest.builder().id(paymentId).status("Cancel").build());
		// Return
		return new PaymentCaptureResponse(paymentId, orderResponse);
	}
}
