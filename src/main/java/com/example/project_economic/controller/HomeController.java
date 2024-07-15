package com.example.project_economic.controller;

import com.example.project_economic.dto.request.ProductRequest;
import com.example.project_economic.dto.request.UserRequest;
import com.example.project_economic.dto.request.register.UserRegisterRequest;
import com.example.project_economic.entity.UserEntity;
import com.example.project_economic.service.CategoryService;
import com.example.project_economic.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.catalina.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
@RequestMapping(value = "/")
public class HomeController {
    ProductService productService;
    CategoryService categoryService;

    @GetMapping(value = "/")
    public String showHome(Model model, Authentication authentication){
        Long userId = 0L;
        if (authentication != null){
            userId = ((UserEntity)authentication.getPrincipal()).getId();
        }
        model.addAttribute("userId", userId);
        model.addAttribute("userRegisterRequest",new UserRegisterRequest());
        int pageSize = 8;
        int pageNumber = 1;
        model.addAttribute("productResponseList", productService.getAllIsActiveByPage(pageSize,pageNumber));
        return "/home/index.html";
    }

    @GetMapping(value = "/shop/{pageNumber}")
    public String showShop(@PathVariable int pageNumber, Model model){
        model.addAttribute("userRegisterRequest",new UserRegisterRequest());
        int pageSize = 12;
        model.addAttribute("productResponseList", productService.getAllIsActiveByPage(pageSize,pageNumber));
        return "/home/shop.html";
    }

    @GetMapping(value = "/product-detail/{productId}")
    public String showProductDetail(@PathVariable Long productId, Model model){
        model.addAttribute("userRegisterRequest",new UserRegisterRequest());
        model.addAttribute("productResponse",productService.getById(productId));
        return "/home/product-detail.html";
    }

    @GetMapping(value = "/category")
    public String showCategory(Model model){
        model.addAttribute("userRegisterRequest",new UserRegisterRequest());
        model.addAttribute("categoryResponseList", categoryService.getAll());
        return "/home/category.html";
    }

    @GetMapping(value = "/faq")
    public String showFAQ(Model model){
        model.addAttribute("userRegisterRequest",new UserRegisterRequest());
        return "/home/faq.html";
    }

    @GetMapping(value = "/about")
    public String showAbout(Model model){
        model.addAttribute("userRegisterRequest",new UserRegisterRequest());
        return "/home/about.html";
    }

    @GetMapping(value = "/contact")
    public String showContact(Model model){
        model.addAttribute("userRegisterRequest",new UserRegisterRequest());
        return "/home/contact.html";
    }

    @GetMapping(value = "/my-account")
    public String showMyAccount(Model model, Principal principal){
        model.addAttribute("userRegisterRequest",new UserRegisterRequest());
        model.addAttribute("principalName",principal.getName());
        return "/home/my-account.html";
    }
}
