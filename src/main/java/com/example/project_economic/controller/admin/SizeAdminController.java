package com.example.project_economic.controller.admin;

import com.example.project_economic.dto.request.SizeRequest;
import com.example.project_economic.dto.response.ResponseObject;
import com.example.project_economic.service.SizeService;
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
@RequestMapping(value = "/admin/size")
public class SizeAdminController {
    SizeService sizeService;

    @GetMapping("/list/all")
    public String showAll(Model model){
        //table
        model.addAttribute("sizeResponseSet",sizeService.getAll());
        return "/admin/size.html";
    }

    @ResponseBody
    @PostMapping("/save")
    public ResponseEntity<ResponseObject> save(@RequestBody SizeRequest sizeRequest){
        //INSERT
        if (sizeRequest.getId() == null){
            //Handle already exist entity
            Boolean exists = sizeService.existsByName(sizeRequest.getName());
            if (exists){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        new ResponseObject("failed", "Size already taken", "")
                );
            }

            //Create size
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Create new size successfully", sizeService.create(sizeRequest))
            );
        }
        //UPDATE
        else{
            //Handle already exist entity
            Boolean exists = sizeService.existsByNameExceptId(
                    sizeRequest.getName(),
                    sizeRequest.getId()
            );
            if (exists){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                        new ResponseObject("failed", "Size already taken", "")
                );
            }
            //Handle not found to update
            Boolean exists2 = sizeService.existsById(sizeRequest.getId());
            if (!exists2){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed","Resource need updating not found","")
                );
            }
            //Update size
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Update size successfully", sizeService.update(sizeRequest))
            );
        }
    }

    @ResponseBody
    @DeleteMapping("/{sizeId}/delete")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long sizeId){
        //Handle case not found to delete
        Boolean exists = sizeService.existsById(sizeId);
        if (!exists){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Cannot find size to delete", "")
            );
        }
        sizeService.delete(sizeId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Delete size successfully", "")
        );
    }
}
