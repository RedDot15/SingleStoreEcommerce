package com.example.project_economic.impl;

import com.amazonaws.services.dlm.model.ResourceNotFoundException;
import com.example.project_economic.dto.request.ProductImageRequest;
import com.example.project_economic.dto.response.ProductImageResponse;
import com.example.project_economic.entity.ProductEntity;
import com.example.project_economic.entity.ProductImageEntity;
import com.example.project_economic.exception.custom.DuplicateException;
import com.example.project_economic.mapper.ProductImageMapper;
import com.example.project_economic.repository.*;
import com.example.project_economic.service.ProductImageService;
import com.example.project_economic.service.ProductService;
import com.example.project_economic.service.StorageService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
    ProductService productService;

    @Override
    public Set<ProductImageResponse> getAllByProductId(Long productId) {
        // Product not found/deleted exception
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product is not found."));
        // Find all product-detail by product ID
        Set<ProductImageEntity> productImageEntitySet = productImageRepository.findAllByProductId(productId);
        // Return result
        return new TreeSet<>(
                productImageEntitySet
                .stream().map(productImageMapper::toProductImageResponse)
                .collect(Collectors.toSet())
        );
    }

    @Override
    public ProductImageResponse getFirstActiveByProductIdAndColorId(Long productId, Long colorId) {
        // Validate product is active
        productService.validateProductIsActive(productId);
        // Fetch
        ProductImageEntity productImageEntity = productImageRepository.findFirstActiveByProductIdAndColorId(productId,colorId);
        // Not found exception
        if (productImageEntity == null) {
            throw new ResourceNotFoundException("Product image is not found.");
        }
        // Fetch & Return
        return productImageMapper.toProductImageResponse(productImageEntity);
    }

    @Override
    public ProductImageResponse add(ProductImageRequest productImageRequest) {
        // Product image duplicate exception
        if (productImageRepository.existsByProductIdAndColorId(
                productImageRequest.getProductId(),
                productImageRequest.getColorId()
        )){
            throw new DuplicateException("Product Image already exists for this color.");
        }
        // Create new product-image
        String imageName = storageService.uploadFile(productImageRequest.getFileImage());
        ProductImageEntity newProductImageEntity =
                ProductImageEntity.builder()
                        .name(imageName)
                        .productEntity(productRepository.getReferenceById(productImageRequest.getProductId()))
                        .colorEntity(colorRepository.getReferenceById(productImageRequest.getColorId()))
                        .build();
        // Save & Return
        return productImageMapper.toProductImageResponse(
                productImageRepository.save(newProductImageEntity)
        );
    }

    @Override
    public ProductImageResponse update(ProductImageRequest productImageRequest) {
        //Get old
        ProductImageEntity foundProductImageEntity = productImageRepository.findById(productImageRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product image not found to update."));
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
    public Long delete(Long id) {
        // Get entity
        ProductImageEntity foundProductImageEntity = productImageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product image not found."));
        ProductEntity foundProductEntity = foundProductImageEntity.getProductEntity();
        // Delete this product image
        productImageRepository.deleteById(id);
        // if product image inactive then product (owner of this product detail) wont be affect
        if (!foundProductImageEntity.getIsActive())
            return id;
        // Handle case product (owner of this product image) inactive or still having at least 1 product image
        if (!foundProductEntity.getIsActive() || foundProductEntity.getActiveProductDetailEntitySet().size() != 1)
            return id;
        // Deactivate product (owner of this product image)
        productService.deactivate(foundProductEntity.getId());
        // Return id
        return id;
    }

    @Override
    public ProductImageResponse activate(Long id) {
        //Get old
        ProductImageEntity oldProductImageEntity = productImageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product image not found."));
        //Activate
        oldProductImageEntity.setIsActive(true);
        //Save & Return
        return productImageMapper.toProductImageResponse(
                productImageRepository.save(oldProductImageEntity)
        );
    }

    @Override
    public String getDeactivateCheckMessage(Long id) {
        // Init message
        String msg = "";
        // Get Entity
        ProductImageEntity foundProductImageEntity = productImageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product image not found."));
        ProductEntity foundProductEntity = foundProductImageEntity.getProductEntity();
        // Handle case product image already inactive
        if (!foundProductImageEntity.getIsActive()){
            return "";
        }
        /* Return empty if product already inactive or product still have
        at least one active product image after deactivate this image */
        if (!foundProductEntity.getIsActive() || foundProductEntity.getActiveProductImageEntitySet().size() != 1)
            return "";
        // Notify user their action will also deactivate a product
        msg += "This action will also deactivate product: " +
                foundProductEntity.getName() + " because it doesn't have at least 1 active image<br/>";
        msg += productService.getDeactivateCheckMessage(foundProductEntity.getId());
        return msg;
    }

    @Override
    public ProductImageResponse deactivate(Long id) {
        // Get Entity
        ProductImageEntity foundProductImageEntity = productImageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product image not found."));
        ProductEntity foundProductEntity = foundProductImageEntity.getProductEntity();
        // Deactivate this product image
        foundProductImageEntity.setIsActive(false);
        productImageRepository.save(foundProductImageEntity);
        ProductImageResponse updatedProductImageResponse = productImageMapper.toProductImageResponse(foundProductImageEntity);
        /* Return if product (owner of this product detail) already inactive or product
        still have at least 3 active product image after deactivate this image */
        if (!foundProductEntity.getIsActive() || foundProductEntity.getActiveProductImageEntitySet().size() != 1)
            return updatedProductImageResponse;
        // Deactivate product (owner of this product detail)
        productService.deactivate(foundProductEntity.getId());
        // Return updated
        return updatedProductImageResponse;
    }
}
