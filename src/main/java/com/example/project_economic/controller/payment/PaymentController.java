package com.example.project_economic.controller.payment;

import static com.example.project_economic.helper.ResponseBuilder.buildResponse;

import com.example.project_economic.dto.request.payment.PaymentRequest;
import com.example.project_economic.helper.ResponseObject;
import com.example.project_economic.service.payment.PaymentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping(path = "/payment")
public class PaymentController {
	PaymentService paymentService;

	@PostMapping(value = "/init")
	public ResponseEntity<ResponseObject> createPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
		return buildResponse(
				HttpStatus.OK, "Created payment successfully.", paymentService.createPayment(paymentRequest));
	}

	@PutMapping(value = "/complete")
	public ResponseEntity<ResponseObject> completePayment(@RequestParam("paymentId") String paymentId) {
		return buildResponse(
				HttpStatus.OK, "Complete payment successfully.", paymentService.completePayment(paymentId));
	}

	@PutMapping(value = "/cancel")
	public ResponseEntity<ResponseObject> cancelPayment(@RequestParam("paymentId") String paymentId) {
		return buildResponse(HttpStatus.OK, "Cancel payment successfully.", paymentService.cancelPayment(paymentId));
	}
}
