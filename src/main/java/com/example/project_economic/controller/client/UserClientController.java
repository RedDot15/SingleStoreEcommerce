package com.example.project_economic.controller.client;

import com.example.project_economic.dto.request.UserRequest;
import com.example.project_economic.dto.request.register.UserRegisterRequest;
import com.example.project_economic.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
@RequestMapping("/client/user")
public class UserClientController {
    UserService userService;

    @PostMapping("/register-handle")
    public String registerHandle(@ModelAttribute UserRegisterRequest userRegisterRequest) throws Exception {
        //Handle already available exception

        //Call service to Add/Update userEntity to database
        userService.save(userRegisterRequest);
        return "redirect:/";
    }

}
