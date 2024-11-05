package com.example.project_economic.controller.admin;

import com.example.project_economic.dto.request.Price;
import com.example.project_economic.dto.request.UserRequest;
import com.example.project_economic.dto.response.*;
import com.example.project_economic.entity.UserEntity;
import com.example.project_economic.service.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
@RequestMapping("/admin/user")
public class UserAdminController {
    UserService userService;
    ProductService productService;
    HistoryCardService historyCardService;

    @GetMapping("/list/all")
    public String showAll(Model model){
        //table
        model.addAttribute("userResponseSet",userService.getAll());
        return "/admin/user.html";
    }

    @ResponseBody
    @PostMapping("/save")
    public ResponseEntity<ResponseObject> save(@RequestBody UserRequest userRequest) throws Exception {
        //INSERT
        if (userRequest.getId() == null){
            //Handle already exist entity
            Boolean exists = userService.existsByUsernameOrEmail(
                    userRequest.getUsername(),
                    userRequest.getEmail()
            );
            if (exists){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        new ResponseObject("failed", "Username or email already taken", "")
                );
            }
            //Create user
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Insert user successfully", userService.create(userRequest))
            );
        }
        //UPDATE
        else{
            //Handle already exist entity
            Boolean exists = userService.existsByUsernameOrEmailExceptId(
                    userRequest.getUsername(),
                    userRequest.getEmail(),
                    userRequest.getId()
            );
            if (exists){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        new ResponseObject("failed", "Username or email already taken", "")
                );
            }
            //Handle not found to update
            Boolean exists2 = userService.existsById(userRequest.getId());
            if (!exists2){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed","Resource not found to update","")
                );
            }
            //Update & Return
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Update user successfully", userService.update(userRequest))
            );
        }
    }

    @ResponseBody
    @DeleteMapping(value = "/{userId}/delete")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long userId){
        Boolean exists = userService.existsById(userId);
        if (!exists){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Cannot find user to delete", "")
            );

        }
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Delete user successfully", "")
        );
    }

    @ResponseBody
    @PutMapping("/{userId}/deactivate")
    public ResponseEntity<ResponseObject> deactivate(@PathVariable Long userId){
        //Handle case resource need to update not found
        UserResponse foundUserResponse = userService.getFirstById(userId);
        if (foundUserResponse == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject(
                            "failed",
                            "Resource need to deactivate not found",
                            ""
                    )
            );
        }
        //Deactivate
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(
                        "ok",
                        "Activate complete!",
                        userService.deactivate(userId)
                )
        );
    }

    @ResponseBody
    @PutMapping("/{userId}/activate")
    public ResponseEntity<ResponseObject> activate(@PathVariable Long userId){
        //Handle case resource need to update not found
        UserResponse foundUserResponse = userService.getFirstById(userId);
        if (foundUserResponse == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject(
                            "failed",
                            "Resource need to deactivate not found",
                            ""
                    )
            );
        }
        //Deactivate
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(
                        "ok",
                        "Activate complete!",
                        userService.activate(userId)
                )
        );
    }

    @GetMapping("/login")
    public String showFormLogin(Model model){
        model.addAttribute("userEntity" ,new UserEntity());
        return "login/index";
    }

    @GetMapping("/register")
    public String showFormRegister(Model model){
        model.addAttribute("userEntity" ,new UserEntity());
        return "register/index";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("userEntity") UserEntity userEntity, @RequestParam("reset-password") String pass, Model model){
        try{
            if(pass.equals(userEntity.getPassword()))
                userService.createUser(userEntity);
            else{
                model.addAttribute("wrongpass", "Mật khẩu không khớp!");
                model.addAttribute("userEntity", userEntity);
                return "register/index";
            }
            return "login/index";
        }catch (Exception exception){
            model.addAttribute("error",exception.getMessage());
            return "register/index";
        }
    }

//    @PostMapping("/home")
//    public String loginPage(@ModelAttribute("userEntity") UserEntity userEntity, Model model){
//        try{
////            Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
//                    userEntity.getUsername(),
//                    userEntity.getPassword()
//            ));
////            System.out.println(principal.getName());
////            if(authentication.isAuthenticated()){
////                SecurityContextHolder.getContext().setAuthentication(authentication);
////                String token=this.jwtService.generateToken(authentication.getName());
////                model.addAttribute("token",token);
////                model.addAttribute("username",authentication.getName());
////                model.addAttribute("roles",authentication.getAuthorities().toString());
////                model.addAttribute("products",this.findByAllProductActive());
////                model.addAttribute("numbercart",this.cartItemService.countCart(1L));
////                return "home/index";
////
////            }
//
//        }catch (Exception exception){
//            model.addAttribute("error","Sai tài khoản hoặc mật khẩu!");
//            return "login/index";
//        }
//        model.addAttribute("error","Sai tài khoản hoặc mật khẩu!");
//        return "login/index";
//    }

    @GetMapping("/fail")
    public String loginFail(Model model){
        model.addAttribute("error","Sai tài khoản hoặc mật khẩu!");
        model.addAttribute("userEntity", new UserEntity());
        return "login/index";
    }

    List<Price>prices=List.of(
            new Price(0,100000),
            new Price(100000,200000),
            new Price(200000,400000),
            new Price(400000,1000000)
    );

    @GetMapping("/homepage")
    public String getHomeIndex(Model model,Principal principal){
//        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
//        model.addAttribute("userId",((UserInfoDetails)authentication.getPrincipal()).getUserId());
        model.addAttribute("username",principal.getName());
        model.addAttribute("products", this.productService.getAllIsActiveByPage(9, 1));
//        model.addAttribute("numbercart",this.cartItemService.countCart(((UserInfoDetails)(authentication.getPrincipal())).getUserId()));
//        model.addAttribute("categories", this.categoryService.findAllByActived());
        model.addAttribute("prices", prices);
        int pageSize=9;
        PageProductResponse pageProductResponse=productService.findAllPagination(1,9);
        model.addAttribute("currentPage",1);
        model.addAttribute("show_pagination","all");
        model.addAttribute("lastPage",pageProductResponse.getLastPage());
        model.addAttribute("previousPage",1);
        model.addAttribute("totalPage",new int[pageProductResponse.getTotalPage()]);


        //External API post
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        JSONObject userID = new JSONObject();
//        userID.put("user_id", ((UserInfoDetails)authentication.getPrincipal()).getUserId());
//
//        HttpEntity<String> request =
//                new HttpEntity<String>(userID.toString(), headers);
//        String result =
//                restTemplate.postForObject("https://2f5e-35-237-149-61.ngrok-free.app/traketqua", request, String.class);
//
//        JSONObject resultAsJSON = new JSONObject(result);
//        JSONArray jsonArray = resultAsJSON.getJSONArray("item_ids");
//
//        List<ProductResponse> recommendedProducts= new ArrayList();
//        for (int i = 0; i < jsonArray.length(); i++) {
//            recommendedProducts.add(productService.findById(jsonArray.getLong(i)));
//        }

        model.addAttribute("recommendedProducts", this.productService.getAllIsActiveByPage(9, 1));
        return "home/product-list";
    }

    @PostMapping("/update/")
    public String updateUser(@ModelAttribute("users") UserEntity userEntity, @RequestParam("id") Long userId, Model model){
        try{
            this.userService.update(userEntity, userId);
//            model.addAttribute("users",new UserEntity());
        }catch (Exception exception){
            model.addAttribute("error","error");
            return "home/my-account";
        }
        model.addAttribute("history_card",this.historyCardService.findByUserId(userId));
//        model.addAttribute("user",this.userService.findUserById(userId));
        return "home/my-account";
    }

//    @PostMapping("/change-password/")
//    @ResponseBody
//    public Integer changePassword(
//                                 @RequestParam("userId") Long userId,
//                                 @RequestParam("passNow") String passNow,
//                                 @RequestParam("pass1") String pass1,
//                                 @RequestParam("pass2") String pass2,
//                                 Model model){
//        try{
//            UserEntity userEntity = this.userService.findUserById(userId);
//            if(pass1.equals(pass2)){
////                if (passwordEncoder.matches(passNow, this.userService.findUserById(userId).getPassword())){
////                    userEntity.setPassword(passwordEncoder.encode(pass1));
//                    this.userService.update(userEntity, userId);
//                    return 0;
//                }
//                else{
//                    return 1;
//                }
//            }
//            else return 2;
//        }catch (Exception exception){
////            model.addAttribute("error","error");
//        }
////        model.addAttribute("history_card",this.historyCardService.findByUserId(userId));
////        model.addAttribute("user",this.userService.findUserById(userId));
//        return 0;
//    }

}
