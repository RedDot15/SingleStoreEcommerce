package com.example.project_economic.controller.admin;

import com.example.project_economic.dto.request.CommentDTO;
import com.example.project_economic.dto.request.CountProuductDto;
import com.example.project_economic.dto.request.Price;
import com.example.project_economic.dto.request.ProductRequest;
import com.example.project_economic.dto.response.PageProductResponse;
import com.example.project_economic.dto.response.ProductResponse;
import com.example.project_economic.service.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
@RequestMapping(path = "/admin/product")
public class ProductAdminController {
    CategoryService categoryService;
    CartItemService cartItemService;
    CommentService commentService;
    ProductService productService;
    ColorService colorService;
    SizeService sizeService;

    @GetMapping(value = "")
    public String showAll(Model model) {
        //Call service Get all product available in database
        //Pushing to view using model
        model.addAttribute("productResponseList", productService.getAll());
        //Declare ProductRequest Dto for create-request-form
        //Pushing to view using model
        model.addAttribute("productRequest", new ProductRequest());
        //Declare categoryResponseList Dto for create-request-form
        //Pushing to view using model
        model.addAttribute("categoryResponseList", categoryService.getAll());
        //Declare colorResponseList Dto for create-request-form
        //Pushing to view using model
        model.addAttribute("colorResponseList", colorService.getAll());
        //Declare sizeResponseList Dto for create-request-form
        //Pushing to view using model
        model.addAttribute("sizeResponseList", sizeService.getAll());
        return "/admin/product.html";
    }

    @PostMapping(value = "/save")
    public String save(@ModelAttribute ProductRequest productRequest) throws IOException {
        //Handle already exist entity

        //Call service Adding productEntity to database
        productService.add(productRequest);
        //Call service Updating productEntity to database
        return "redirect:/admin/product";
    }

    @DeleteMapping(value = "/delete/{productId}")
    public String delete(@PathVariable Long productId) {
        //Call service delete selected productEntity by id
        productService.deleteById(productId);
        return "redirect:/admin/product";
    }

    List<Price> prices = List.of(
            new Price(0, 100000),
            new Price(100000, 200000),
            new Price(200000, 400000),
            new Price(400000, 1000000)
    );

    public List<CountProuductDto> countProuductDtos() {
        List<CountProuductDto> countProuductDtos = new ArrayList<>();
        List<Object[]> objects = this.productService.countProductByCategoryId();
        for (Object[] obj : objects) {
            CountProuductDto countProuductDto = new CountProuductDto();
            countProuductDto.setCount((Long) obj[1]);
            countProuductDto.setCategoryId((Long) obj[0]);
            countProuductDtos.add(countProuductDto);
        }
        return countProuductDtos;
    }

    @GetMapping("/active/")
    public String activeProduct(@RequestParam("id") Long id, Model model) {
        this.productService.activeById(id);
        model.addAttribute("countProductByCategory", this.countProuductDtos());
        model.addAttribute("categories", this.categoryService.getAll());
        model.addAttribute("product", new ProductRequest());
        ProductResponse product = this.productService.getById(id);
//        model.addAttribute("allproducts", this.productService.findAllProductByUserId(product.getUserEntity().getId()));
        return "/home/createproduct";
    }


//    @GetMapping("/productdetail/{userId}/{productId}")
//    public String showProductDetail(@PathVariable Long userId, @PathVariable Long productId, Model model) {
//        int pageSize = 5;
//        int pageNumber = 1;
//        model.addAttribute("product", productService.getById(productId));
//        model.addAttribute("productId", productId);
//        model.addAttribute("products", this.productService.getAllIsActive());
//        model.addAttribute("categories", this.categoryService.findAllByActived());
//        if (this.cartItemService.findCartByProductId(productId, userId)) {
//            model.addAttribute("findProductInCart", "already");
//        }
//        List<CommentDTO> commentDTOS = this.commentService.findByPostId(productId);
//        model.addAttribute("comments", commentDTOS);
//        return "home/product-detail";
//    }

    @GetMapping("/all/{pageNumber}")
    public String getAllPagePagination(
            @PathVariable Integer pageNumber,
            Model model,
            Principal principal
    ) throws URISyntaxException {
        int pageSize = 9;
        model.addAttribute("products", this.productService.getAllIsActiveByPage(pageSize, pageNumber));
        model.addAttribute("categories", this.categoryService.findAllByActived());
        model.addAttribute("prices", prices);

        PageProductResponse pageProductResponse = productService.findAllPagination(pageNumber, pageSize);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("show_pagination", "all");
        model.addAttribute("lastPage", pageProductResponse.getLastPage());
        model.addAttribute("previousPage", pageNumber > 1 ? pageNumber - 1 : pageNumber);
        model.addAttribute("totalPage", new int[pageProductResponse.getTotalPage()]);


//        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();

//        //External API post
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

        model.addAttribute("recommendedProducts", this.productService.getAllIsActiveByPage(pageSize, pageNumber));
        return "home/product-list";
    }

    @PostMapping("/all/like/{productId}")
    @ResponseBody
    public String likeProduct(@PathVariable("productId") Long id, Model model) {
        this.productService.likeById(id);
        return "/home/product-list";
    }

    @GetMapping("/search/")
    public String getProductByKeyWord(
            @RequestParam("key") String key,
            @RequestParam("pageNumber") Integer pageNumber,
            Model model
    ) {
        int pageSize = 9;
        PageProductResponse pageProductResponse = this.productService.findAllProductByKeyword(key, pageSize, (pageNumber - 1) * pageSize);
        model.addAttribute("show_pagination", "keyword");
        model.addAttribute("key", key);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("lastPage", pageProductResponse.getLastPage());
        model.addAttribute("previousPage", pageNumber > 1 ? pageNumber - 1 : pageNumber);
        model.addAttribute("totalPage", new int[pageProductResponse.getTotalPage()]);
        model.addAttribute("categories", this.categoryService.findAllByActived());
        model.addAttribute("products", pageProductResponse.getProductResponses());
        model.addAttribute("prices", prices);
        return "home/product-list";
    }

    @GetMapping("/all/")
    public String findAllProductByCategory(
            @RequestParam("category_id") Long categoryId,
            @RequestParam("pageNumber") int pageNumber,
            Model model
    ) {
        int pageSize = 9;
        PageProductResponse pageProductResponse = this.productService.findAllProductByCategory(categoryId, pageSize, (pageNumber - 1) * pageSize);
        model.addAttribute("show_pagination", "category");
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("lastPage", pageProductResponse.getLastPage());
        model.addAttribute("previousPage", pageNumber > 1 ? pageNumber - 1 : pageNumber);
        model.addAttribute("totalPage", new int[pageProductResponse.getTotalPage()]);
        model.addAttribute("categories", this.categoryService.findAllByActived());
        model.addAttribute("products", pageProductResponse.getProductResponses());
        model.addAttribute("prices", prices);

        return "home/product-list";
    }

    @GetMapping("/prices/")
    public String findAllProduceByPrice(
            @RequestParam("firstPrice") int firstPrice,
            @RequestParam("secondPrice") int secondPrice,
            @RequestParam("pageNumber") int pageNumber,
            Model model
    ) {
        int pageSize = 9;
        PageProductResponse pageProductResponse = this.productService.findAllProductByCostPrice(firstPrice, secondPrice, pageSize, (pageNumber - 1) * pageSize);
        model.addAttribute("firstPrice", firstPrice);
        model.addAttribute("secondPrice", secondPrice);
        model.addAttribute("show_pagination", "prices");
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("lastPage", pageProductResponse.getLastPage());
        model.addAttribute("previousPage", pageNumber > 1 ? pageNumber - 1 : pageNumber);
        model.addAttribute("totalPage", new int[pageProductResponse.getTotalPage()]);
        model.addAttribute("categories", this.categoryService.findAllByActived());
        model.addAttribute("products", pageProductResponse.getProductResponses());
        model.addAttribute("prices", prices);
        return "home/product-list";
    }
}
