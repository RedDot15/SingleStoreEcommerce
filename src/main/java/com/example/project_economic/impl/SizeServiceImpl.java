package com.example.project_economic.impl;

import com.example.project_economic.dto.request.SizeRequest;
import com.example.project_economic.dto.response.SizeResponse;
import com.example.project_economic.entity.ProductDetailEntity;
import com.example.project_economic.entity.SizeEntity;
import com.example.project_economic.exception.ErrorCode;
import com.example.project_economic.exception.custom.AppException;
import com.example.project_economic.mapper.SizeMapper;
import com.example.project_economic.repository.ProductDetailRepository;
import com.example.project_economic.repository.SizeRepository;
import com.example.project_economic.service.ProductDetailService;
import com.example.project_economic.service.ProductService;
import com.example.project_economic.service.SizeService;
import jakarta.transaction.Transactional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Service
public class SizeServiceImpl implements SizeService {
	SizeRepository sizeRepository;
	ProductDetailRepository productDetailRepository;
	SizeMapper sizeMapper;
	ProductService productService;
	ProductDetailService productDetailService;

	@PreAuthorize("hasRole('ADMIN') or hasAuthority('GET_ALL_SIZE')")
	@Override
	public Set<SizeResponse> getAll() {
		return new TreeSet<>(sizeRepository.findAll().stream()
				.map(sizeMapper::toSizeResponse)
				.collect(Collectors.toSet()));
	}

	@Override
	public Set<SizeResponse> getActiveByProductId(Long productId) {
		// Product inactive exception
		productService.validateProductIsActive(productId);
		// Get product detail
		Set<ProductDetailEntity> productDetailEntitySet = productDetailRepository.findActiveByProductId(productId);
		// Get size
		Set<SizeEntity> sizeEntitySet = new TreeSet<>();
		for (ProductDetailEntity productDetailEntity : productDetailEntitySet) {
			sizeEntitySet.add(productDetailEntity.getSizeEntity());
		}
		// Return result
		return new TreeSet<>(
				sizeEntitySet.stream().map(sizeMapper::toSizeResponse).collect(Collectors.toSet()));
	}

	@PreAuthorize("hasRole('ADMIN') or hasAuthority('ADD_SIZE')")
	@Override
	public SizeResponse add(SizeRequest sizeRequest) {
		// Name duplicate exception
		if (sizeRepository.existsByName(sizeRequest.getName())) throw new AppException(ErrorCode.SIZE_DUPLICATE);
		// Add & Return
		return sizeMapper.toSizeResponse(sizeRepository.save(sizeMapper.toSizeEntity(sizeRequest)));
	}

	@PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_SIZE')")
	@Override
	public SizeResponse update(SizeRequest sizeRequest) {
		// Name duplicate exception
		if (sizeRepository.existsByNameExceptId(sizeRequest.getName(), sizeRequest.getId()))
			throw new AppException(ErrorCode.SIZE_DUPLICATE);
		// Get Entity & Not found/Deleted exception
		SizeEntity foundSizeEntity = sizeRepository
				.findById(sizeRequest.getId())
				.orElseThrow(() -> new AppException(ErrorCode.SIZE_NOT_FOUND));
		// Update
		sizeMapper.updateSizeEntityFromRequest(foundSizeEntity, sizeRequest);
		// Save
		return sizeMapper.toSizeResponse(sizeRepository.save(foundSizeEntity));
	}

	@PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_SIZE')")
	@Override
	public Long delete(Long id) {
		// Get entity
		SizeEntity foundSizeEntity =
				sizeRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.SIZE_NOT_FOUND));
		// Also delete every product detail relate to this size
		for (ProductDetailEntity productDetailEntity : foundSizeEntity.getProductDetailEntitySet()) {
			productDetailService.delete(productDetailEntity.getId());
		}
		// Delete color
		sizeRepository.delete(foundSizeEntity);
		// Return ID
		return id;
	}
}
