package com.example.project_economic.controller;

import com.example.project_economic.dto.request.UserRequest;
import com.example.project_economic.dto.response.wrap.ResponseObject;
import com.example.project_economic.service.*;
import com.example.project_economic.validation_group.ChangePassword;
import com.example.project_economic.validation_group.Client;
import com.example.project_economic.validation_group.Create;
import com.example.project_economic.validation_group.Update;
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
    public ResponseEntity<ResponseObject> showAll(){
        // Fetch & Return all users
        return buildResponse(
                HttpStatus.OK,
                "All users fetch successfully.",
                userService.getAll()
        );
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseObject> add(@Validated({Create.class, Default.class}) @RequestBody UserRequest userRequest) {
        // Create & Return user
        return buildResponse(
                HttpStatus.OK,
                "Created new user successfully.",
                userService.add(userRequest)
        );
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseObject> update(@Validated({Update.class, Default.class}) @RequestBody UserRequest userRequest) {
        //Update & Return
        return buildResponse(
                HttpStatus.OK,
                "Updated user successfully.",
                userService.update(userRequest)
        );
    }

    @DeleteMapping(value = "/{userId}/delete")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long userId){
        // Delete & Return id
        return buildResponse(
                HttpStatus.OK,
                "Deleted user successfully.",
                userService.delete(userId)
        );
    }

    @PutMapping("/{userId}/activate")
    public ResponseEntity<ResponseObject> activate(@PathVariable Long userId){
        // Deactivate & Return
        return buildResponse(
                HttpStatus.OK,
                "Activated user successfully.",
                userService.activate(userId)
        );
    }

    @PutMapping("/{userId}/deactivate")
    public ResponseEntity<ResponseObject> deactivate(@PathVariable Long userId){
        // Deactivate & Return
        return buildResponse(
                HttpStatus.OK,
                "Deactivate user successfully",
                userService.deactivate(userId)
        );
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseObject> registerUser(@Validated({Create.class, Client.class, Default.class}) @RequestBody UserRequest userRequest){
        // Register & Return new user
        return buildResponse(
                HttpStatus.CREATED,
                "Registered new user successfully.",
                userService.register(userRequest)
        );
    }

    @PutMapping("/update-information")
    public ResponseEntity<ResponseObject> updateInformation(@Validated({Update.class, Client.class, Default.class}) @RequestBody UserRequest userRequest){
        // Update & Return user
        return buildResponse(
                HttpStatus.OK,
                "Updated user successfully.",
                userService.updateInformation(userRequest)
        );
    }

    @PutMapping("/change-password")
    public ResponseEntity<ResponseObject> changePassword(@Validated({Update.class, Client.class, ChangePassword.class, Default.class}) @RequestBody UserRequest userRequest){
        // Update & Return user
        return buildResponse(
                HttpStatus.OK,
                "Updated user successfully.",
                userService.changePassword(userRequest)
        );
    }

    private ResponseEntity<ResponseObject> buildResponse(HttpStatus status, String message, Object data) {
        return ResponseEntity.status(status).body(
                new ResponseObject(
                        status.is2xxSuccessful() ? "ok" : "failed",
                        message,
                        data
                )
        );
    }
}
