package com.example.project_economic.controller.admin;

import com.example.project_economic.dto.request.ColorRequest;
import com.example.project_economic.service.ColorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
@RequestMapping(value = "/admin/color")
public class ColorAdminController {
    ColorService colorService;

    @GetMapping(value = "")
    public String showAll(Model model){
        //Call service Get all color available in database
        //Pushing to view using model
        model.addAttribute("colorResponseList", colorService.getAll());
        //Declare ColorRequest Dto for create-request-form
        //Pushing to view using model
        model.addAttribute("colorRequest", new ColorRequest());
        return "/admin/color.html";
    }

    @PostMapping(value = "/save")
    public String save(@ModelAttribute ColorRequest colorRequest){
        //Handle already exist entity

        //Call service Add/Update colorEntity to database
        colorService.save(colorRequest);
        return "redirect:/admin/color";
    }

    @DeleteMapping(value = "/delete/{colorId}")
    public String delete(@PathVariable Long colorId){
        //Call service delete selected colorEntity by id
        colorService.deleteById(colorId);
        return "redirect:/admin/color";
    }
}
