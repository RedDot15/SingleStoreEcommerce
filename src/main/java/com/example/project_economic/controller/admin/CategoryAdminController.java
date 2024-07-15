package com.example.project_economic.controller.admin;

import com.example.project_economic.dto.request.CategoryRequest;
import com.example.project_economic.entity.CategoryEntity;
import com.example.project_economic.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
@RequestMapping(path = "/admin/category")
public class CategoryAdminController {
    private CategoryService categoryService;
    private ProductAdminController productAdminController;

    @GetMapping("")
    public String showAll(Model model){
        //Call service Get all category available in databse
        //Pushing to view using model
        model.addAttribute("categoryResponseList", categoryService.getAll());
        //Declare categoryRequest Dto for create-request-form
        //Pushing to view using model
        model.addAttribute("categoryRequest",new CategoryRequest());
        return "/admin/category.html";
    }

    @PostMapping("/save")
    public String createCategory(@ModelAttribute("category") CategoryRequest categoryRequest){
        //Handle already available exception

        //Call service to Add/Update categoryEntity to database
        categoryService.save(categoryRequest);
        return "redirect:/admin/category";
    }

    @DeleteMapping("/delete/{categoryId}")
    public String deleteCategory(@PathVariable Long categoryId){
        //Call service delete selected categoryEntity by id
        categoryService.deleteById(categoryId);
        return "redirect:/admin/category";
    }

    @GetMapping("/update/active/")
    public String activeCategory(Model model,@RequestParam("id") Long id){
//        this.categoryService.enableById(id);
        model.addAttribute("allcategories",this.categoryService.getAll());
        model.addAttribute("countProductByCategory", productAdminController.countProuductDtos());
        model.addAttribute("category",new CategoryEntity());
        return "home/addnew";
    }
}
