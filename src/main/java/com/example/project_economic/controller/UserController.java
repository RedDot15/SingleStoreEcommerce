package com.example.project_economic.controller;

import static com.example.project_economic.helper.ResponseBuilder.buildResponse;

import com.example.project_economic.dto.request.UserChangePasswordRequest;
import com.example.project_economic.dto.request.UserRequest;
import com.example.project_economic.dto.response.wrap.ResponseObject;
import com.example.project_economic.service.UserService;
import com.example.project_economic.validation.group.Admin;
import com.example.project_economic.validation.group.Client;
import com.example.project_economic.validation.group.Create;
import com.example.project_economic.validation.group.Update;
import jakarta.validation.groups.Default;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/user")
public class UserController {
	UserService userService;

	@GetMapping("/list/all")
	public ResponseEntity<ResponseObject> showAll() {
		// Fetch & Return all users
		return buildResponse(HttpStatus.OK, "All users fetch successfully.", userService.getAll());
	}

	@GetMapping("/my-info/get")
	public ResponseEntity<ResponseObject> showMyInfo() {
		// Fetch & Return all users
		return buildResponse(HttpStatus.OK, "My information fetch successfully.", userService.getMyInfo());
	}

	@PostMapping("/add")
	public ResponseEntity<ResponseObject> add(
			@Validated({Create.class, Admin.class, Default.class}) @RequestBody UserRequest userRequest) {
		// Create & Return user
		return buildResponse(HttpStatus.OK, "Created new user successfully.", userService.add(userRequest));
	}

	@PutMapping("/update")
	public ResponseEntity<ResponseObject> update(
			@Validated({Update.class, Admin.class, Default.class}) @RequestBody UserRequest userRequest) {
		// Update & Return
		return buildResponse(HttpStatus.OK, "Updated user successfully.", userService.update(userRequest));
	}

	@DeleteMapping(value = "/{userId}/delete")
	public ResponseEntity<ResponseObject> delete(@PathVariable Long userId) {
		// Delete & Return id
		return buildResponse(HttpStatus.OK, "Deleted user successfully.", userService.delete(userId));
	}

	@PutMapping("/{userId}/activate")
	public ResponseEntity<ResponseObject> activate(@PathVariable Long userId) {
		// Deactivate & Return
		return buildResponse(HttpStatus.OK, "Activated user successfully.", userService.activate(userId));
	}

	@PutMapping("/{userId}/deactivate")
	public ResponseEntity<ResponseObject> deactivate(@PathVariable Long userId) {
		// Deactivate & Return
		return buildResponse(HttpStatus.OK, "Deactivate user successfully", userService.deactivate(userId));
	}

	@PostMapping("/register")
	public ResponseEntity<ResponseObject> registerUser(
			@Validated({Create.class, Client.class, Default.class}) @RequestBody UserRequest userRequest) {
		// Register & Return new user
		return buildResponse(
				HttpStatus.CREATED, "Registered new user successfully.", userService.register(userRequest));
	}

	@PutMapping("/update-information")
	public ResponseEntity<ResponseObject> updateInformation(
			@Validated({Update.class, Client.class, Default.class}) @RequestBody UserRequest userRequest) {
		// Update & Return user
		return buildResponse(HttpStatus.OK, "Updated user successfully.", userService.updateInformation(userRequest));
	}

	@PutMapping("/change-password")
	public ResponseEntity<ResponseObject> changePassword(
			@Validated({Update.class, Default.class}) @RequestBody UserChangePasswordRequest request) {
		// Update & Return user
		return buildResponse(HttpStatus.OK, "Changed password successfully.", userService.changePassword(request));
	}
}
