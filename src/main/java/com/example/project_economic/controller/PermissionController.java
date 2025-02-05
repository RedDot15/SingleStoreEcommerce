package com.example.project_economic.controller;

import static com.example.project_economic.helper.ResponseBuilder.buildResponse;

import com.example.project_economic.dto.request.PermissionRequest;
import com.example.project_economic.dto.response.wrap.ResponseObject;
import com.example.project_economic.service.PermissionService;
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
@RequestMapping(path = "/permission")
public class PermissionController {
  PermissionService permissionService;

  @GetMapping("/list/all")
  public ResponseEntity<ResponseObject> showAll() {
    // Fetch & Return all permissions
    return buildResponse(
        HttpStatus.OK, "All permissions fetch successfully.", permissionService.getAll());
  }

  @PostMapping("/add")
  public ResponseEntity<ResponseObject> add(
      @Validated({Create.class, Default.class}) @RequestBody PermissionRequest permissionRequest) {
    // Create & Return permission
    return buildResponse(
        HttpStatus.OK,
        "Created new permission successfully.",
        permissionService.add(permissionRequest));
  }

  @PutMapping("/update")
  public ResponseEntity<ResponseObject> update(
      @Validated({Update.class, Default.class}) @RequestBody PermissionRequest permissionRequest) {
    // Update & Return permission
    return buildResponse(
        HttpStatus.OK,
        "Updated permission successfully",
        permissionService.update(permissionRequest));
  }

  @DeleteMapping("/{permissionId}/delete")
  public ResponseEntity<ResponseObject> delete(@PathVariable Long permissionId) {
    // Delete & Return id
    return buildResponse(
        HttpStatus.OK, "Deleted permission successfully.", permissionService.delete(permissionId));
  }
}
