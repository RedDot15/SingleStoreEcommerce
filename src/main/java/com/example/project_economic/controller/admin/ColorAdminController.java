package com.example.project_economic.controller.admin;

import com.example.project_economic.dto.request.ColorRequest;
import com.example.project_economic.dto.response.ResponseObject;
import com.example.project_economic.service.ColorService;
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
@Controller
@RequestMapping(value = "/admin/color")
public class ColorAdminController {
    ColorService colorService;

    @GetMapping("/list/all")
    public String showAll(Model model){
        //table
        model.addAttribute("colorResponseSet", colorService.getAll());
        return "/admin/color.html";
    }

    @ResponseBody
    @PostMapping("/save")
    public ResponseEntity<ResponseObject> save(@RequestBody ColorRequest colorRequest){
        //INSERT
        if (colorRequest.getId() == null){
            //Handle already exist entity
            Boolean exists = colorService.existsByNameOrHexCode(
                    colorRequest.getName(),
                    colorRequest.getHexCode()
            );
            if (exists){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        new ResponseObject("failed", "Color name or color hexcode already taken", "")
                );
            }
            //Create color
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok",
                            "Create new color successfully",
                            colorService.create(colorRequest)
                    )
            );
        }
        //UPDATE
        else{
            //Handle already exist entity
            Boolean exists = colorService.existsByNameOrHexCodeExceptId(
                    colorRequest.getName(),
                    colorRequest.getHexCode(),
                    colorRequest.getId()
            );
            if (exists){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        new ResponseObject("failed", "Color name or color hexcode already taken", "")
                );
            }
            //Handle not found to update
            Boolean exists2 = colorService.existsById(colorRequest.getId());
            if (!exists2){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed","Resource need updating not found","")
                );
            }
            //Update color
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Update color successfully", colorService.update(colorRequest))
            );
        }
    }

    @ResponseBody
    @DeleteMapping("/{colorId}/delete")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long colorId){
        //Handle not found to delete
        Boolean exists = colorService.existsById(colorId);
        if (!exists){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Cannot find color to delete", "")
            );
        }
        //Delete
        colorService.delete(colorId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Delete color successfully", "")
        );
    }
}
