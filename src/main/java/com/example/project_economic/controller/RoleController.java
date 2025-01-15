package com.example.project_economic.controller;

import com.example.project_economic.dto.request.RoleRequest;
import com.example.project_economic.dto.response.wrap.ResponseObject;
import com.example.project_economic.service.RoleService;
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
@RequestMapping(path = "/role")
public class RoleController {
    RoleService roleService;

    @GetMapping("/list/all")
    public ResponseEntity<ResponseObject> showAll(){
        // Fetch & Return all roles
        return buildResponse(
                HttpStatus.OK,
                "All roles fetch successfully.",
                roleService.getAll()
        );
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseObject> add(@Validated({Create.class, Default.class}) @RequestBody RoleRequest roleRequest){
        // Create & Return role
        return buildResponse(
                HttpStatus.OK,
                "Created new role successfully.",
                roleService.add(roleRequest)
        );
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseObject> update(@Validated({Update.class, Default.class}) @RequestBody RoleRequest roleRequest){
        // Update & Return role
        return buildResponse(
                HttpStatus.OK,
                "Updated role successfully",
                roleService.update(roleRequest)
        );
    }

    @DeleteMapping("/{roleId}/delete")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long roleId){
        // Delete & Return id
        return buildResponse(
                HttpStatus.OK,
                "Deleted role successfully.",
                roleService.delete(roleId)
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
