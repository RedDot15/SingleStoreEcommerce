package com.example.project_economic.controller;

import com.example.project_economic.dto.request.CategoryRequest;
import com.example.project_economic.dto.response.wrap.ResponseObject;
import com.example.project_economic.service.CategoryService;
import com.example.project_economic.validation.group.Create;
import com.example.project_economic.validation.group.Update;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.example.project_economic.helper.ResponseBuilder.buildResponse;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping(path = "/category")
public class CategoryController {
    CategoryService categoryService;

    @GetMapping("/list/all")
    public ResponseEntity<ResponseObject> showAll(){
        // Fetch & Return categories
        return buildResponse(
                HttpStatus.OK,
                "All categories fetched successfully.",
                categoryService.getAll()
        );
    }

    @GetMapping("/list/active")
    public ResponseEntity<ResponseObject> showActive(){
        // Fetch & Return categories
        return buildResponse(HttpStatus.OK,
                "Active categories fetched successfully.",
                categoryService.getActive()
        );
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseObject> add(@Validated(Create.class) @RequestBody CategoryRequest categoryRequest){
        // Add category & Return
        return buildResponse(
                HttpStatus.CREATED,
                "Created new category successfully.",
                categoryService.add(categoryRequest)
        );
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseObject> update(@Validated(Update.class) @RequestBody CategoryRequest categoryRequest){
        //Update category & Return
        return buildResponse(
                HttpStatus.OK,
                "Updated category successfully.",
                categoryService.update(categoryRequest)
        );
    }

    @DeleteMapping("/{categoryId}/delete")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long categoryId){
        // Delete & Return
        return buildResponse(
                HttpStatus.OK,
                "Category deleted successfully.",
                categoryService.delete(categoryId)
        );
    }

    @PutMapping("/{categoryId}/activate")
    public ResponseEntity<ResponseObject> activate(@PathVariable Long categoryId){
        // Build and return success response
        return buildResponse(
                HttpStatus.OK,
                "Category activated successfully.",
                categoryService.activate(categoryId)
        );
    }

    @PutMapping("/{categoryId}/deactivate")
    public ResponseEntity<ResponseObject> deactivate(@PathVariable Long categoryId){
        // Deactivate
        return buildResponse(
                HttpStatus.OK,
                "Category deactivated successfully.",
                categoryService.deactivate(categoryId)
        );
    }
}
