package com.example.project_economic.impl;

import com.example.project_economic.dto.request.ProductImageRequest;
import com.example.project_economic.dto.response.ProductImageResponse;
import com.example.project_economic.entity.ProductEntity;
import com.example.project_economic.entity.ProductImageEntity;
import com.example.project_economic.exception.ErrorCode;
import com.example.project_economic.exception.custom.AppException;
import com.example.project_economic.mapper.ProductImageMapper;
import com.example.project_economic.repository.ColorRepository;
import com.example.project_economic.repository.ProductImageRepository;
import com.example.project_economic.repository.ProductRepository;
import com.example.project_economic.service.ProductImageService;
import com.example.project_economic.service.ProductService;
import com.example.project_economic.service.aws_storage.StorageService;
import jakarta.transaction.Transactional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ProductImageServiceImpl implements ProductImageService {
	ProductImageRepository productImageRepository;
	ProductRepository productRepository;
	ColorRepository colorRepository;
	ProductImageMapper productImageMapper;
	StorageService storageService;
	ProductService productService;

	@PreAuthorize("hasRole('ADMIN') or hasAuthority('GET_ALL_PRODUCT_IMAGE')")
	@Override
	public Set<ProductImageResponse> getAllByProductId(Long productId) {
		// Product not found/deleted exception
		productRepository.findById(productId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
		// Find all product-detail by product ID
		Set<ProductImageEntity> productImageEntitySet = productImageRepository.findAllByProductId(productId);
		// Return result
		return new TreeSet<>(productImageEntitySet.stream()
				.map(productImageMapper::toProductImageResponse)
				.collect(Collectors.toSet()));
	}

	@Override
	public ProductImageResponse getActiveByProductIdAndColorId(Long productId, Long colorId) {
		// Validate product is active
		productService.validateProductIsActive(productId);
		// Fetch
		ProductImageEntity productImageEntity = productImageRepository
				.findActiveByProductIdAndColorId(productId, colorId)
				.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_IMAGE_NOT_FOUND));
		// Map & Return
		return productImageMapper.toProductImageResponse(productImageEntity);
	}

	@PreAuthorize("hasRole('ADMIN') or hasAuthority('ADD_PRODUCT_IMAGE')")
	@Override
	public ProductImageResponse add(ProductImageRequest productImageRequest) {
		// Product image duplicate exception
		if (productImageRepository.existsByProductIdAndColorId(
				productImageRequest.getProductId(), productImageRequest.getColorId())) {
			throw new AppException(ErrorCode.PRODUCT_IMAGE_DUPLICATE);
		}
		// Create new product-image
		String imageName = storageService.uploadFile(productImageRequest.getFileImage());
		ProductImageEntity newProductImageEntity = ProductImageEntity.builder()
				.name(imageName)
				.productEntity(productRepository.getReferenceById(productImageRequest.getProductId()))
				.colorEntity(colorRepository.getReferenceById(productImageRequest.getColorId()))
				.build();
		// Save & Return
		return productImageMapper.toProductImageResponse(productImageRepository.save(newProductImageEntity));
	}

	@PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_PRODUCT_IMAGE')")
	@Override
	public ProductImageResponse update(ProductImageRequest productImageRequest) {
		// Get old
		ProductImageEntity foundProductImageEntity = productImageRepository
				.findById(productImageRequest.getId())
				.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_IMAGE_NOT_FOUND));
		// Delete old image
		storageService.deleteFile(foundProductImageEntity.getName());
		// Store new image
		String imageName = storageService.uploadFile(productImageRequest.getFileImage());
		// Update image name
		foundProductImageEntity.setName(imageName);
		// Save
		return productImageMapper.toProductImageResponse(productImageRepository.save(foundProductImageEntity));
	}

	@PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_PRODUCT_IMAGE')")
	@Override
	public Long delete(Long id) {
		// Get entity
		ProductImageEntity foundProductImageEntity = productImageRepository
				.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_IMAGE_NOT_FOUND));
		ProductEntity foundProductEntity = foundProductImageEntity.getProductEntity();
		// Delete this product image
		productImageRepository.deleteById(id);
		// if product image inactive then product (owner of this product detail) wont be affect
		if (!foundProductImageEntity.getIsActive()) return id;
		// Handle case product (owner of this product image) inactive or still having at least 1 product
		// image
		if (!foundProductEntity.getIsActive()
				|| foundProductEntity.getActiveProductDetailEntitySet().size() != 1) return id;
		// Deactivate product (owner of this product image)
		productService.deactivate(foundProductEntity.getId());
		// Return id
		return id;
	}

	@PreAuthorize("hasRole('ADMIN') or hasAuthority('ACTIVATE_PRODUCT_IMAGE')")
	@Override
	public ProductImageResponse activate(Long id) {
		// Get old
		ProductImageEntity oldProductImageEntity = productImageRepository
				.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_IMAGE_NOT_FOUND));
		// Activate
		oldProductImageEntity.setIsActive(true);
		// Save & Return
		return productImageMapper.toProductImageResponse(productImageRepository.save(oldProductImageEntity));
	}

	@PreAuthorize("hasRole('ADMIN') or hasAuthority('DEACTIVATE_PRODUCT_IMAGE')")
	@Override
	public String getDeactivateCheckMessage(Long id) {
		// Init message
		String msg = "";
		// Get Entity
		ProductImageEntity foundProductImageEntity = productImageRepository
				.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_IMAGE_NOT_FOUND));
		ProductEntity foundProductEntity = foundProductImageEntity.getProductEntity();
		// Handle case product image already inactive
		if (!foundProductImageEntity.getIsActive()) {
			return "";
		}
		/* Return empty if product already inactive or product still have
		at least one active product image after deactivate this image */
		if (!foundProductEntity.getIsActive()
				|| foundProductEntity.getActiveProductImageEntitySet().size() != 1) return "";
		// Notify user their action will also deactivate a product
		msg += "This action will also deactivate product: "
				+ foundProductEntity.getName()
				+ " because it doesn't have at least 1 active image<br/>";
		msg += productService.getDeactivateCheckMessage(foundProductEntity.getId());
		return msg;
	}

	@PreAuthorize("hasRole('ADMIN') or hasAuthority('DEACTIVATE_PRODUCT_IMAGE')")
	@Override
	public ProductImageResponse deactivate(Long id) {
		// Get Entity
		ProductImageEntity foundProductImageEntity = productImageRepository
				.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_IMAGE_NOT_FOUND));
		ProductEntity foundProductEntity = foundProductImageEntity.getProductEntity();
		// Deactivate this product image
		foundProductImageEntity.setIsActive(false);
		productImageRepository.save(foundProductImageEntity);
		ProductImageResponse updatedProductImageResponse =
				productImageMapper.toProductImageResponse(foundProductImageEntity);
		/* Return if product (owner of this product detail) already inactive or product
		still have at least 3 active product image after deactivate this image */
		if (!foundProductEntity.getIsActive()
				|| foundProductEntity.getActiveProductImageEntitySet().size() != 1) return updatedProductImageResponse;
		// Deactivate product (owner of this product detail)
		productService.deactivate(foundProductEntity.getId());
		// Return updated
		return updatedProductImageResponse;
	}
}
