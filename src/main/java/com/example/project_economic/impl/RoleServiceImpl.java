package com.example.project_economic.impl;

import com.example.project_economic.dto.request.RoleRequest;
import com.example.project_economic.dto.response.RoleResponse;
import com.example.project_economic.entity.PermissionEntity;
import com.example.project_economic.entity.RoleEntity;
import com.example.project_economic.exception.ErrorCode;
import com.example.project_economic.exception.custom.AppException;
import com.example.project_economic.mapper.RoleMapper;
import com.example.project_economic.repository.PermissionRepository;
import com.example.project_economic.repository.RoleRepository;
import com.example.project_economic.service.RoleService;
import jakarta.transaction.Transactional;
import java.util.HashSet;
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
public class RoleServiceImpl implements RoleService {
	RoleRepository roleRepository;
	PermissionRepository permissionRepository;
	RoleMapper roleMapper;

	@PreAuthorize("hasRole('ADMIN') or hasAuthority('GET_ALL_ROLE')")
	public List<RoleResponse> getAll() {
		// Return result
		return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).collect(Collectors.toList());
	}

	@PreAuthorize("hasRole('ADMIN') or hasAuthority('ADD_ROLE')")
	public RoleResponse add(RoleRequest roleRequest) {
		// Name duplicate exception
		if (roleRepository.existsByName(roleRequest.getName())) throw new AppException(ErrorCode.ROLE_DUPLICATE);
		// Mapping
		RoleEntity roleEntity = roleMapper.toRoleEntity(roleRequest);
		List<PermissionEntity> permissionEntityList =
				permissionRepository.findAllById(roleRequest.getPermissionIdSet());
		roleEntity.setPermissionEntitySet(new HashSet<>(permissionEntityList));
		// Add & Return
		return roleMapper.toRoleResponse(roleRepository.save(roleEntity));
	}

	@PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_ROLE')")
	public RoleResponse update(RoleRequest roleRequest) {
		// Name duplicate exception
		if (roleRepository.existsByNameExceptId(roleRequest.getName(), roleRequest.getId()))
			throw new AppException(ErrorCode.ROLE_DUPLICATE);
		// Get old
		RoleEntity foundRoleEntity = roleRepository
				.findById(roleRequest.getId())
				.orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
		// Update
		roleMapper.updateRoleEntityFromRequest(foundRoleEntity, roleRequest);
		List<PermissionEntity> permissionEntityList =
				permissionRepository.findAllById(roleRequest.getPermissionIdSet());
		foundRoleEntity.setPermissionEntitySet(new HashSet<>(permissionEntityList));
		// Save
		return roleMapper.toRoleResponse(roleRepository.save(foundRoleEntity));
	}

	@PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_ROLE')")
	public Long delete(Long id) {
		// Get entity
		RoleEntity foundRoleEntity =
				roleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
		// Delete this product
		roleRepository.delete(foundRoleEntity);
		// Return id
		return id;
	}
}
