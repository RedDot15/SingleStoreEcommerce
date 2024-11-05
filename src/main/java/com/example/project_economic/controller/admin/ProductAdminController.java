package com.example.project_economic.controller.admin;

import com.example.project_economic.dto.request.*;
import com.example.project_economic.dto.response.*;
import com.example.project_economic.service.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    ProductDetailService productDetailService;
    ColorService colorService;
    SizeService sizeService;
    ProductImageService productImageService;

    @GetMapping("/list/all")
    public String showAll(Model model) {
        //Table
        model.addAttribute("productResponseSet", productService.getAllForAdmin());

        //Form
        model.addAttribute("categoryResponseSet", categoryService.getAllForAdmin());
        return "/admin/product.html";
    }

    @GetMapping("/{productId}/detail")
    public String showAllProductDetail(Model model, @PathVariable Long productId){
        //Table
        model.addAttribute("productId", productId);
        model.addAttribute("productDetailResponseSet", productDetailService.getAllByProductId(productId));
        model.addAttribute("productImageResponseSet", productImageService.getAllByProductId(productId));

        //Form
        model.addAttribute("colorResponseSet", colorService.getAll());
        model.addAttribute("sizeResponseSet", sizeService.getAll());
        model.addAttribute("colorResponseSetByProductId", colorService.getAllColorOfAProduct(productId));

        return "/admin/product-detail.html";
    }

    @ResponseBody
    @PostMapping("/save")
    public ResponseEntity<ResponseObject> save(@RequestBody ProductRequest productRequest) throws IOException {
        //INSERT
        if (productRequest.getId() == null){
            //Handle already exist entity
            Boolean exists = productService.existsByName(productRequest.getName());
            if (exists){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        new ResponseObject("failed", "Product name already taken", "")
                );
            }
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Insert product successfully", productService.create(productRequest))
            );
        }
        //UPDATE
        else{
            //Handle already exist entity
            Boolean exists = productService.existsByNameExceptId(
                    productRequest.getName(),
                    productRequest.getId()
            );
            if (exists){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        new ResponseObject("failed","Product name already taken","")
                );
            }
            //Handle product not found to update
            Boolean exists2 = productService.existsById(productRequest.getId());
            if (!exists2){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed","Resource not found to update","")
                );
            }
            //Update
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Update product successfully", productService.update(productRequest))
            );
        }
    }

    @ResponseBody
    @PostMapping("/product-detail/save")
    public ResponseEntity<ResponseObject> save(@RequestBody ProductDetailRequest productDetailRequest) throws IOException {
        //CREATE
        if (productDetailRequest.getId() == null){
            ProductDetailResponse foundProductDetailResponse = productDetailService.getFirstByProductIdAndColorIdAndSizeId(
                    productDetailRequest.getProductId(),
                    productDetailRequest.getColorId(),
                    productDetailRequest.getSizeId()
            );
            //Handle case product already have this color and size: Calculate up
            if (foundProductDetailResponse != null){
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject(
                                "ok",
                                "Product detail Id: " +
                                        foundProductDetailResponse.getId() + " added " +
                                        productDetailRequest.getStock() + " more stock successfully",
                                productDetailService.calculateUp(productDetailRequest)
                        )
                );
            }
            //Else: Create a new product detail
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseObject(
                            "ok",
                            "Create new product detail success!",
                            productDetailService.create(productDetailRequest)
                    )
            );
        }
        //UPDATE
        else{
            ProductDetailResponse foundProductDetailResponse = productDetailService.getFirstById(productDetailRequest.getId());
            //Handle case not found to update
            if (foundProductDetailResponse == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject(
                                "failed",
                                "Resource need to update not found",
                                "")
                );
            }
            //Handle case display error: color and size mismatch with id
            if (
                    !foundProductDetailResponse.getProductResponse().getId().equals(productDetailRequest.getProductId()) ||
                    !foundProductDetailResponse.getColorResponse().getId().equals(productDetailRequest.getColorId()) ||
                    !foundProductDetailResponse.getSizeResponse().getId().equals(productDetailRequest.getSizeId())
            ){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        new ResponseObject(
                                "failed",
                                "Color and size mismatch with the id. Potentially display error",
                                "")
                );
            }
            //Else: Update
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(
                            "ok",
                            "Update product detail id: " + productDetailRequest.getId() + " successfully",
                            productDetailService.update(productDetailRequest)
                    )
            );
        }
    }

    @ResponseBody
    @PostMapping("/product-image/save")
    public ResponseEntity<ResponseObject> save(@ModelAttribute ProductImageRequest productImageRequest) throws IOException {
        //CREATE
        if (productImageRequest.getId() == null){
            //Handle case product already have an image for this color
            Boolean exist =
                    productImageService.existsByProductIdAndColorId(
                            productImageRequest.getProductId(),
                            productImageRequest.getColorId()
                    );
            if (exist){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        new ResponseObject("failed","This color already have an image","")
                );
            }
            //Else: Create a new image
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(
                            "ok",
                            "Create a new image success!",
                            productImageService.create(productImageRequest)
                    )
            );
        }
        //UPDATE
        else{
            ProductImageResponse foundProductImageResponse =
                    productImageService.getFirstById(productImageRequest.getId());
            //Handle case not found to update
            if (foundProductImageResponse == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject(
                                "failed",
                                "Resource need to update not found",
                                "")
                );
            }
            //Handle case display error: color mismatch with id
            if (
                !foundProductImageResponse.getProductResponse().getId().equals(productImageRequest.getProductId()) ||
                !foundProductImageResponse.getColorResponse().getId().equals(productImageRequest.getColorId())
            ){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        new ResponseObject(
                                "failed",
                                "Color mismatch with the id. Potentially display error",
                                "")
                );
            }
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(
                            "ok",
                            "Update product image success!",
                            productImageService.update(productImageRequest)
                    )
            );
        }
    }

    @DeleteMapping("/{productId}/delete")
    public ResponseEntity<ResponseObject> productDelete(@PathVariable Long productId) {
        ProductResponse foundProductResponse = productService.getFirstById(productId);
        //Handle case not found to delete
        if (foundProductResponse == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject(
                            "Not found",
                            "Resource not found to delete",
                            ""
                    )
            );
        }
        //Delete product
        productService.delete(productId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Delete product successfully", "")
        );
    }

    @DeleteMapping("/product-detail/{productDetailId}/delete")
    public ResponseEntity<ResponseObject> productDetailDelete(@PathVariable Long productDetailId){
        ProductDetailResponse foundProductDetailResponse = productDetailService.getFirstById(productDetailId);
        //Handle case not found to delete
        if (foundProductDetailResponse == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject(
                            "Not found",
                            "Resource not found to delete",
                            ""
                    )
            );
        }
        //Delete product detail
        productDetailService.delete(productDetailId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(
                        "ok",
                        "Delete complete",
                        ""
                )
        );
    }

    @DeleteMapping("/product-image/{productImageId}/delete")
    public ResponseEntity<ResponseObject> productImageDelete(@PathVariable Long productImageId){
        ProductImageResponse foundProductImageResponse = productImageService.getFirstById(productImageId);
        //Handle case not found to delete
        if (foundProductImageResponse == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject(
                            "Not found",
                            "Resource not found to delete",
                            ""
                    )
            );
        }
        //Delete product detail
        productImageService.delete(productImageId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(
                        "ok",
                        "Delete complete",
                        ""
                )
        );
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

    //Activate/Deactivate Product
    @ResponseBody
    @PutMapping("/{productId}/activate")
    public ResponseEntity<ResponseObject> productActivate(@PathVariable Long productId) {
        ProductResponse foundProductResponse = productService.getFirstById(productId);
        //Handle case not found to deactivate
        if (foundProductResponse == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject(
                            "failed",
                            "Resource need to activate not found",
                            "")
            );
        }
        //Handle case product not meet the requirement to activate: Having at least 1 product detail & 1 image
        if (!productService.activateCheck(productId)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject(
                            "failed",
                            "This product does not meet the requirement to activate: Having at leats 1 active product detail & 1 active image",
                            ""
                    )
            );
        }
        //Return activate success
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(
                        "ok",
                        "Activate complete",
                        productService.activate(productId)
                )
        );
    }

    @ResponseBody
    @GetMapping("/{productId}/deactivate/check")
    public ResponseEntity<ResponseObject> productDeactivateCheck(@PathVariable Long productId){
        ProductResponse foundProductResponse = productService.getFirstById(productId);
        //Handle case not found to deactivate
        if (foundProductResponse == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject(
                            "failed",
                            "Resource need to deactivate not found",
                            "")
            );
        }
        //Return check message
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok",productService.getDeactivateCheckMessage(productId),"")
        );
    }

    @ResponseBody
    @PutMapping("/{productId}/deactivate")
    public ResponseEntity<ResponseObject> productDeactivate(@PathVariable Long productId) {
        ProductResponse foundProductResponse = productService.getFirstById(productId);
        //Handle case not found to deactivate
        if (foundProductResponse == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject(
                            "failed",
                            "Resource need to deactivate not found",
                            "")
            );
        }
        //Return deactivate success
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(
                        "ok",
                        "Deactivate complete",
                        productService.deactivate(productId)
                )
        );
    }

    //Activate/Deactivate product detail
    @ResponseBody
    @PutMapping("/product-detail/{productDetailId}/activate")
    public ResponseEntity<ResponseObject> productDetailActivate(@PathVariable Long productDetailId) {
        ProductDetailResponse foundProductDetailResponse = productDetailService.getFirstById(productDetailId);
        //Handle case not found to deactivate
        if (foundProductDetailResponse == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject(
                            "failed",
                            "Resource need to deactivate not found",
                            "")
            );
        }
        //Return activate success
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(
                        "ok",
                        "Activate complete",
                        productDetailService.activate(productDetailId)
                )
        );
    }

    @ResponseBody
    @GetMapping("/product-detail/{productDetailId}/deactivate/check")
    public ResponseEntity<ResponseObject> productDetailDeactivateCheck(@PathVariable Long productDetailId){
        ProductDetailResponse foundProductDetailResponse = productDetailService.getFirstById(productDetailId);
        //Handle case not found to deactivate
        if (foundProductDetailResponse == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject(
                            "failed",
                            "Resource need to deactivate not found",
                            "")
            );
        }
        //Return check message
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok",productDetailService.getDeactivateCheckMessage(productDetailId),"")
        );
    }

    @ResponseBody
    @PutMapping("/product-detail/{productDetailId}/deactivate")
    public ResponseEntity<ResponseObject> productDetailDeactivate(@PathVariable Long productDetailId) {
        ProductDetailResponse foundProductDetailResponse = productDetailService.getFirstById(productDetailId);
        //Handle case not found to deactivate
        if (foundProductDetailResponse == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject(
                            "failed",
                            "Resource need to deactivate not found",
                            "")
            );
        }
        //Return deactivate success
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(
                        "ok",
                        "Deactivate complete",
                        productDetailService.deactivate(productDetailId)
                )
        );
    }

    //Activate/Deactivate product image
    @ResponseBody
    @PutMapping("/product-image/{productImageId}/activate")
    public ResponseEntity<ResponseObject> productImageActivate(@PathVariable Long productImageId) {
        ProductImageResponse foundProductImageResponse = productImageService.getFirstById(productImageId);
        //Handle case not found to deactivate
        if (foundProductImageResponse == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject(
                            "failed",
                            "Resource need to activate not found",
                            "")
            );
        }
        //Return activate success
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(
                        "ok",
                        "Activate complete",
                        productImageService.activate(productImageId)
                )
        );
    }

    @ResponseBody
    @GetMapping("/product-image/{productImageId}/deactivate/check")
    public ResponseEntity<ResponseObject> productImageDeactivateCheck(@PathVariable Long productImageId){
        ProductImageResponse foundProductImageResponse = productImageService.getFirstById(productImageId);
        //Handle case not found to deactivate
        if (foundProductImageResponse == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject(
                            "failed",
                            "Resource need to deactivate not found",
                            "")
            );
        }
        //Return check message
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok",productImageService.getDeactivateCheckMessage(productImageId),"")
        );
    }

    @ResponseBody
    @PutMapping("/product-image/{productImageId}/deactivate")
    public ResponseEntity<ResponseObject> productImageDeactivate(@PathVariable Long productImageId) {
        ProductImageResponse foundProductImageResponse = productImageService.getFirstById(productImageId);
        //Handle case not found to deactivate
        if (foundProductImageResponse == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject(
                            "failed",
                            "Resource need to deactivate not found",
                            "")
            );
        }
        //Return deactivate success
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(
                        "ok",
                        "Deactivate complete",
                        productImageService.deactivate(productImageId)
                )
        );
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
//        model.addAttribute("categories", this.categoryService.findAllByActived());
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
//        model.addAttribute("categories", this.categoryService.findAllByActived());
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
//        model.addAttribute("categories", this.categoryService.findAllByActived());
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
//        model.addAttribute("categories", this.categoryService.findAllByActived());
        model.addAttribute("products", pageProductResponse.getProductResponses());
        model.addAttribute("prices", prices);
        return "home/product-list";
    }
}
