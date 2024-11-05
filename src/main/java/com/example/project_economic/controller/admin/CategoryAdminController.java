package com.example.project_economic.controller.admin;

import com.example.project_economic.dto.request.CategoryRequest;
import com.example.project_economic.dto.response.CategoryResponse;
import com.example.project_economic.dto.response.ResponseObject;
import com.example.project_economic.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping(path = "/admin/category")
public class CategoryAdminController {
    CategoryService categoryService;

    @GetMapping("/list/all")
    public ResponseEntity<ResponseObject> showAll(){
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok","Get request successfully", categoryService.getAllForAdmin())
        );
    }

    @ResponseBody
    @PostMapping("/save")
    public ResponseEntity<ResponseObject> save(@RequestBody CategoryRequest categoryRequest){
        //INSERT
        if (categoryRequest.getId() == null){
            //Handle already exist entity
            Boolean exists = categoryService.existsByName(categoryRequest.getName());
            if (exists){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        new ResponseObject("failed", "Category already taken", "")
                );
            }
            //Create category
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(
                            "ok",
                            "Create new category successfully",
                            categoryService.create(categoryRequest)
                    )
            );
        }
        //UPDATE
        else{
            //Handle already exist entity. Ensure new name not duplicate another category
            Boolean exists = categoryService.existsByNameExceptId(
                            categoryRequest.getName(),
                            categoryRequest.getId()
            );
            if (exists){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        new ResponseObject("failed", "Category already taken", "")
                );
            }
            //Handle not found to update
            Boolean exists2 = categoryService.existsById(categoryRequest.getId());
            if (!exists2){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed","Resource need updating not found","")
                );
            }
            //Update category
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(
                            "ok",
                            "Update size successfully",
                            categoryService.update(categoryRequest)
                    )
            );
        }
    }

    @ResponseBody
    @DeleteMapping("/{categoryId}/delete")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long categoryId){
        //Handle case resource need to delete not found
        CategoryResponse foundCategoryResponse = categoryService.getFirstById(categoryId);
        if (foundCategoryResponse == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject(
                            "Failed",
                            "Resource need to delete not found",
                            ""
                    )
            );
        }
        //Delete
        categoryService.delete(categoryId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(
                        "ok",
                        "Delete complete!",
                        ""
                )
        );
    }

    @ResponseBody
    @PutMapping("/{categoryId}/activate")
    public ResponseEntity<ResponseObject> activate(@PathVariable Long categoryId){
        //Handle case resource need to update not found
        CategoryResponse foundCategoryResponse = categoryService.getFirstById(categoryId);
        if (foundCategoryResponse == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject(
                            "Failed",
                            "Resource need to activate not found",
                            ""
                    )
            );
        }
        //Handle case category does not meet the requirement to activate:
        //Having at least 3 product
        if (!categoryService.activateCheck(categoryId)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject(
                            "Failed",
                            "This category does not have at least 3 active product to activate",
                            ""
                    )
            );
        }
        //Return activate success
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(
                        "ok",
                        "Activate complete",
                        categoryService.activate(categoryId)
                )
        );
//        this.categoryService.enableById(id);
    }

    @ResponseBody
    @PutMapping("/{categoryId}/deactivate")
    public ResponseEntity<ResponseObject> deactivate(@PathVariable Long categoryId){
        //Handle case resource need to update not found
        CategoryResponse foundCategoryResponse = categoryService.getFirstById(categoryId);
        if (foundCategoryResponse == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject(
                            "Failed",
                            "Resource need to activate not found",
                            ""
                    )
            );
        }
        //Deactivate
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(
                        "OK",
                        "Deactivate complete",
                        categoryService.deactivate(categoryId)
                )
        );
    }
}
