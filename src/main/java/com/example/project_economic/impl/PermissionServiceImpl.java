package com.example.project_economic.impl;

import com.example.project_economic.dto.request.PermissionRequest;
import com.example.project_economic.dto.response.PermissionResponse;
import com.example.project_economic.entity.PermissionEntity;
import com.example.project_economic.exception.ErrorCode;
import com.example.project_economic.exception.custom.AppException;
import com.example.project_economic.mapper.PermissionMapper;
import com.example.project_economic.repository.PermissionRepository;
import com.example.project_economic.service.PermissionService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
@Service
public class PermissionServiceImpl implements PermissionService {
	PermissionRepository permissionRepository;
	PermissionMapper permissionMapper;

	@PreAuthorize("hasRole('ADMIN') or hasAuthority('GET_ALL_PERMISSION')")
	public List<PermissionResponse> getAll() {
		// Return result
		return permissionRepository.findAll().stream()
				.map(permissionMapper::toPermissionResponse)
				.collect(Collectors.toList());
	}

	@PreAuthorize("hasRole('ADMIN') or hasAuthority('ADD_PERMISSION')")
	public PermissionResponse add(PermissionRequest permissionRequest) {
		// Name duplicate exception
		if (permissionRepository.existsByName(permissionRequest.getName()))
			throw new AppException(ErrorCode.PERMISSION_DUPLICATE);
		// Mapping
		PermissionEntity permissionEntity = permissionMapper.toPermissionEntity(permissionRequest);
		// Add & Return
		return permissionMapper.toPermissionResponse(permissionRepository.save(permissionEntity));
	}

	@PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_PERMISSION')")
	public PermissionResponse update(PermissionRequest permissionRequest) {
		// Name duplicate exception
		if (permissionRepository.existsByNameExceptId(permissionRequest.getName(), permissionRequest.getId())) {
			throw new AppException(ErrorCode.PERMISSION_DUPLICATE);
		}
		// Get old
		PermissionEntity foundPermissionEntity = permissionRepository
				.findById(permissionRequest.getId())
				.orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));
		// Update
		permissionMapper.updatePermissionEntityFromRequest(foundPermissionEntity, permissionRequest);
		// Save
		return permissionMapper.toPermissionResponse(permissionRepository.save(foundPermissionEntity));
	}

	@PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_PERMISSION')")
	public Long delete(Long id) {
		// Get entity
		PermissionEntity foundPermissionEntity =
				permissionRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));
		// Delete this product
		permissionRepository.delete(foundPermissionEntity);
		// Return id
		return id;
	}
}
