package com.example.project_economic.impl;

import com.example.project_economic.dto.request.ProductDetailRequest;
import com.example.project_economic.dto.response.ProductDetailResponse;
import com.example.project_economic.dto.response.ResponseObject;
import com.example.project_economic.entity.CategoryEntity;
import com.example.project_economic.entity.ProductDetailEntity;
import com.example.project_economic.entity.ProductEntity;
import com.example.project_economic.mapper.ProductDetailMapper;
import com.example.project_economic.repository.*;
import com.example.project_economic.service.ProductDetailService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Service
public class ProductDetailServiceImpl implements ProductDetailService {
    ProductDetailRepository productDetailRepository;
    ProductDetailMapper productDetailMapper;
    ProductRepository productRepository;
    ColorRepository colorRepository;
    SizeRepository sizeRepository;
    CategoryRepository categoryRepository;

    @Override
    public Set<ProductDetailResponse> getAllByProductId(Long productId) {
        return new TreeSet<>(
                productDetailRepository.findAllByProductId(productId)
                .stream().map(productDetailMapper::toProductDetailResponse)
                .collect(Collectors.toSet())
        );
    }

    @Override
    public ProductDetailResponse getFirstById(Long id) {
        return productDetailMapper.toProductDetailResponse(
                productDetailRepository.findFirstById(id)
        );
    }

    @Override
    public ProductDetailResponse getFirstByProductIdAndColorIdAndSizeId(Long productId, Long colorId, Long sizeId) {
        return productDetailMapper.toProductDetailResponse(
                productDetailRepository.findFirstByProductIdAndColorIdAndSizeId(productId, colorId, sizeId)
        );
    }

    @Override
    public ProductDetailResponse create(ProductDetailRequest productDetailRequest) {
        ProductDetailEntity newProductDetailEntity =
                ProductDetailEntity.builder()
                        .productEntity(productRepository.getReferenceById(productDetailRequest.getProductId()))
                        .colorEntity(colorRepository.getReferenceById(productDetailRequest.getColorId()))
                        .sizeEntity(sizeRepository.getReferenceById(productDetailRequest.getSizeId()))
                        .stock(productDetailRequest.getStock())
                        .build();
        return productDetailMapper.toProductDetailResponse(
                productDetailRepository.save(newProductDetailEntity)
        );
    }

    @Override
    public ProductDetailResponse update(ProductDetailRequest productDetailRequest) {
        //Get old
        ProductDetailEntity oldProductDetailEntity = productDetailRepository.findFirstById(
                productDetailRequest.getId()
        );
        //Update
        oldProductDetailEntity.setStock(productDetailRequest.getStock());
        //Save
        return productDetailMapper.toProductDetailResponse(
                productDetailRepository.save(oldProductDetailEntity)
        );
    }

    @Override
    public void delete(Long id) {
        //Get entity
        ProductDetailEntity foundProductDetailEntity = productDetailRepository.findFirstById(id);
        ProductEntity foundProductEntity = foundProductDetailEntity.getProductEntity();
        CategoryEntity foundCategoryEntity = foundProductEntity.getCategoryEntity();
        //Delete this product detail
        productDetailRepository.deleteById(id);
        //if product detail inactive then product (owner of this product detail) wont be affect
        if (!foundProductDetailEntity.getIsActive())
            return;
        //Handle case product (owner of this product detail) inactive or still having at least 1 product detail
        if (!foundProductEntity.getIsActive() || foundProductEntity.getActiveProductDetailEntitySet().size() != 1)
            return;
        //Deactivate product (owner of this product detail)
        foundProductEntity.setIsActive(false);
        productRepository.save(foundProductEntity);
        //Handle case category (owner of above product) inactive or still having at least 3 product
        if (!foundCategoryEntity.getIsActive() || foundCategoryEntity.getActiveProductEntitySet().size() != 3)
            return;
        //Deactivate category (owner of above product)
        foundCategoryEntity.setIsActive(false);
        categoryRepository.save(foundCategoryEntity);
        return;
    }

    @Override
    public ProductDetailResponse calculateUp(ProductDetailRequest productDetailRequest) {
        //Get old
        ProductDetailEntity oldProductDetailEntity = productDetailRepository.findFirstByProductIdAndColorIdAndSizeId(
                productDetailRequest.getProductId(),
                productDetailRequest.getColorId(),
                productDetailRequest.getSizeId()
        );
        //Calculate up new stock
        Integer newStock = oldProductDetailEntity.getStock() + productDetailRequest.getStock();
        oldProductDetailEntity.setStock(newStock);
        //Save
        return productDetailMapper.toProductDetailResponse(
                productDetailRepository.save(oldProductDetailEntity)
        );
    }

    @Override
    public ProductDetailResponse activate(Long id) {
        //Get Entity
        ProductDetailEntity foundProductDetailEntity = productDetailRepository.findFirstById(id);
        //Activate
        foundProductDetailEntity.setIsActive(true);
        //Save & Return
        return productDetailMapper.toProductDetailResponse(
                productDetailRepository.save(foundProductDetailEntity)
        );
    }

    @Override
    public String getDeactivateCheckMessage(Long id) {
        //Init message
        String msg = "";
        //Get Entity
        ProductDetailEntity foundProductDetailEntity = productDetailRepository.findFirstById(id);
        ProductEntity foundProductEntity = foundProductDetailEntity.getProductEntity();
        CategoryEntity foundCategoryEntity =  foundProductEntity.getCategoryEntity();
        //Handle case product detail already inactive
        if (!foundProductDetailEntity.getIsActive()){
            return "";
        }
        /* Return empty if product already inactive or product still have
        at least one active product detail after deactivate this product */
        if (!foundProductEntity.getIsActive() || foundProductEntity.getActiveProductDetailEntitySet().size() != 1)
            return "";
        //Notify user their action will also deactivate a product
        msg += "This action will also deactivate product: " +
                foundProductEntity.getName() + " because it will no longer have at least 1 active color & size<br/>";
        // Return without notify about category may be deactivated
        if (!foundCategoryEntity.getIsActive() || foundCategoryEntity.getActiveProductEntitySet().size() != 3)
            return msg;
        //Notify user their action will also deactivate a category
        msg += "This action will also deactivate category: " +
                foundCategoryEntity.getName() + " because it will no longer have at least 3 active product";
        //Return message
        return msg;
    }

    @Override
    public ProductDetailResponse deactivate(Long id) {
        //Get Entity
        ProductDetailEntity foundProductDetailEntity = productDetailRepository.findFirstById(id);
        ProductEntity foundProductEntity = foundProductDetailEntity.getProductEntity();
        CategoryEntity foundCategoryEntity =  foundProductEntity.getCategoryEntity();
        //Deactivate this product detail
        foundProductDetailEntity.setIsActive(false);
        productDetailRepository.save(foundProductDetailEntity);
        ProductDetailResponse updatedProductDetailResponse = productDetailMapper.toProductDetailResponse(foundProductDetailEntity);
        /* Return if product (owner of this product detail) already inactive or product
        still have at least one active product detail after deactivate this product */
        if (!foundProductEntity.getIsActive() || foundProductEntity.getActiveProductDetailEntitySet().size() != 1)
            return updatedProductDetailResponse;
        //Deactivate product (owner of this product detail)
        foundProductEntity.setIsActive(false);
        productRepository.save(foundProductEntity);
        // Return if category (owner of the above product) wont be deactivate
        if (!foundCategoryEntity.getIsActive() || foundCategoryEntity.getActiveProductEntitySet().size() != 3)
            return updatedProductDetailResponse;
        //Deactivate category (owner of the above product)
        foundCategoryEntity.setIsActive(false);
        categoryRepository.save(foundCategoryEntity);
        //Return updated
        return updatedProductDetailResponse;
    }
}
