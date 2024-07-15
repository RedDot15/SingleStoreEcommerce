package com.example.project_economic.controller.admin;

import com.example.project_economic.dto.request.SizeRequest;
import com.example.project_economic.service.SizeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
@RequestMapping(value = "/admin/size")
public class SizeAdminController {
    SizeService sizeService;

    @GetMapping(value = "")
    public String showAll(Model model){
        //Call service Get all size available in database
        //Pushing to view using model
        model.addAttribute("sizeResponseList",sizeService.getAll());
        //Declare SizeRequest Dto for create-request-form
        //Pushing to view using model
        model.addAttribute("sizeRequest",new SizeRequest());
        return "/admin/size.html";
    }

    @PostMapping(value = "/save")
    public String save(@ModelAttribute SizeRequest sizeRequest){
        //Handle already available exception

        //Call service to Add/Update sizeEntity to database
        sizeService.save(sizeRequest);
        return "redirect:/admin/size";
    }

    @DeleteMapping(value = "/delete/{sizeId}")
    public String delete(@PathVariable Long sizeId){
        //Call service to Delete selected sizeEntity by id
        sizeService.deleteById(sizeId);
        return "redirect:/admin/size";
    }
}
