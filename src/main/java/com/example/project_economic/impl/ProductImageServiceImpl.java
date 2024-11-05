package com.example.project_economic.impl;

import com.example.project_economic.dto.request.ProductImageRequest;
import com.example.project_economic.dto.response.ProductDetailResponse;
import com.example.project_economic.dto.response.ProductImageResponse;
import com.example.project_economic.entity.CategoryEntity;
import com.example.project_economic.entity.ProductDetailEntity;
import com.example.project_economic.entity.ProductEntity;
import com.example.project_economic.entity.ProductImageEntity;
import com.example.project_economic.mapper.ProductImageMapper;
import com.example.project_economic.repository.CategoryRepository;
import com.example.project_economic.repository.ColorRepository;
import com.example.project_economic.repository.ProductImageRepository;
import com.example.project_economic.repository.ProductRepository;
import com.example.project_economic.service.ProductImageService;
import com.example.project_economic.service.StorageService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ProductImageServiceImpl implements ProductImageService {
    ProductImageRepository productImageRepository;
    ProductImageMapper productImageMapper;
    StorageService storageService;
    ProductRepository productRepository;
    ColorRepository colorRepository;
    CategoryRepository categoryRepository;

    @Override
    public Set<ProductImageResponse> getAllByProductId(Long productId) {
        return new TreeSet<>(
                productImageRepository.findAllByProductId(productId)
                .stream().map(productImageMapper::toProductImageResponse)
                .collect(Collectors.toSet())
        );
    }

    @Override
    public Boolean existsByProductIdAndColorId(Long productId, Long colorId) {
        return productImageRepository.existsByProductIdAndColorId(productId,colorId);
    }

    @Override
    public ProductImageResponse getFirstById(Long id) {
        return productImageMapper.toProductImageResponse(
                productImageRepository.findFirstById(id)
        );
    }

    @Override
    public ProductImageResponse create(ProductImageRequest productImageRequest) {
        String imageName = storageService.uploadFile(productImageRequest.getFileImage());
        ProductImageEntity newProductImageEntity =
                ProductImageEntity.builder()
                        .name(imageName)
                        .productEntity(productRepository.getReferenceById(productImageRequest.getProductId()))
                        .colorEntity(colorRepository.getReferenceById(productImageRequest.getColorId()))
                        .build();
        return productImageMapper.toProductImageResponse(
                productImageRepository.save(newProductImageEntity)
        );
    }

    @Override
    public ProductImageResponse update(ProductImageRequest productImageRequest) {
        //Get old
        ProductImageEntity foundProductImageEntity = productImageRepository.findFirstById(productImageRequest.getId());
        //Delete old image
        storageService.deleteFile(foundProductImageEntity.getName());
        //Store new image
        String imageName = storageService.uploadFile(productImageRequest.getFileImage());
        //Update image name
        foundProductImageEntity.setName(imageName);
        //Save
        return productImageMapper.toProductImageResponse(
                productImageRepository.save(foundProductImageEntity)
        );
    }

    @Override
    public void delete(Long id) {
        //Get entity
        ProductImageEntity foundProductImageEntity = productImageRepository.findFirstById(id);
        ProductEntity foundProductEntity = foundProductImageEntity.getProductEntity();
        CategoryEntity foundCategoryEntity = foundProductEntity.getCategoryEntity();
        //Delete this product image
        productImageRepository.deleteById(id);
        //if product image inactive then product (owner of this product detail) wont be affect
        if (!foundProductImageEntity.getIsActive())
            return;
        //Handle case product (owner of this product image) inactive or still having at least 1 product image
        if (!foundProductEntity.getIsActive() || foundProductEntity.getActiveProductDetailEntitySet().size() != 1)
            return;
        //Deactivate product (owner of this product image)
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
    public ProductImageResponse activate(Long id) {
        //Get old
        ProductImageEntity oldProductImageEntity = productImageRepository.findFirstById(id);
        //Activate
        oldProductImageEntity.setIsActive(true);
        //Save & Return
        return productImageMapper.toProductImageResponse(
                productImageRepository.save(oldProductImageEntity)
        );
    }

    @Override
    public String getDeactivateCheckMessage(Long id) {
        //Init message
        String msg = "";
        //Get Entity
        ProductImageEntity foundProductImageEntity = productImageRepository.findFirstById(id);
        ProductEntity foundProductEntity = foundProductImageEntity.getProductEntity();
        CategoryEntity foundCategoryEntity = foundProductEntity.getCategoryEntity();
        //Handle case product image already inactive
        if (!foundProductImageEntity.getIsActive()){
            return "";
        }
        /* Return empty if product already inactive or product still have
        at least one active product image after deactivate this image */
        if (!foundProductEntity.getIsActive() || foundProductEntity.getActiveProductImageEntitySet().size() != 1)
            return "";
        //Notify user their action will also deactivate a product
        msg += "This action will also deactivate product: " +
                foundProductEntity.getName() + " because it doesn't have at least 1 active image<br/>";
        // Return without notify about category may be deactivated
        if (!foundCategoryEntity.getIsActive() || foundCategoryEntity.getActiveProductEntitySet().size() != 3)
            return msg;
        //Notify user their action will also deactivate a category
        msg += "This action will also deactivate category: " +
                foundCategoryEntity.getName() + " because it doesn't have at least 3 active product";
        return msg;
    }

    @Override
    public ProductImageResponse deactivate(Long id) {
        //Get Entity
        ProductImageEntity foundProductImageEntity = productImageRepository.findFirstById(id);
        ProductEntity foundProductEntity = foundProductImageEntity.getProductEntity();
        CategoryEntity foundCategoryEntity =  foundProductEntity.getCategoryEntity();
        //Deactivate this product image
        foundProductImageEntity.setIsActive(false);
        productImageRepository.save(foundProductImageEntity);
        ProductImageResponse updatedProductImageResponse = productImageMapper.toProductImageResponse(foundProductImageEntity);
        /* Return if product (owner of this product detail) already inactive or product
        still have at least 3 active product image after deactivate this image */
        if (!foundProductEntity.getIsActive() || foundProductEntity.getActiveProductImageEntitySet().size() != 1)
            return updatedProductImageResponse;
        //Deactivate product (owner of this product detail)
        foundProductEntity.setIsActive(false);
        productRepository.save(foundProductEntity);
        // Return if category (owner of the above product) wont be deactivate
        if (!foundCategoryEntity.getIsActive() || foundCategoryEntity.getActiveProductEntitySet().size() != 3)
            return updatedProductImageResponse;
        //Deactivate category (owner of the above product)
        foundCategoryEntity.setIsActive(false);
        categoryRepository.save(foundCategoryEntity);
        //Return updated
        return updatedProductImageResponse;
    }
}
