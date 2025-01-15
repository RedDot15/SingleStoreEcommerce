package com.example.project_economic.impl;

import com.example.project_economic.dto.request.UserChangePasswordRequest;
import com.example.project_economic.dto.request.UserRequest;
import com.example.project_economic.dto.response.UserResponse;
import com.example.project_economic.entity.RoleEntity;
import com.example.project_economic.entity.UserEntity;
import com.example.project_economic.exception.ErrorCode;
import com.example.project_economic.exception.custom.AppException;
import com.example.project_economic.mapper.UserMapper;
import com.example.project_economic.repository.RoleRepository;
import com.example.project_economic.repository.UserRepository;
import com.example.project_economic.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Service
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('GET_ALL_USER')")
    @Override
    public Set<UserResponse> getAll() {
        return userRepository.findAll()
                .stream().map(userMapper::toUserResponse)
                .collect(Collectors.toSet());
    }

    @Override
    public UserResponse getMyInfo() {
        // Get context
        SecurityContext context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();
        // Fetch
        UserEntity userEntity = userRepository.findActiveByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        // Return
        return userMapper.toUserResponse(userEntity);
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADD_USER')")
    @Override
    public UserResponse add(UserRequest userRequest) {
        // Username or email duplicate exception
        if (userRepository.existsByUsername(userRequest.getUsername()))
            throw new AppException(ErrorCode.USERNAME_DUPLICATE);
        if (userRepository.existsByEmail(userRequest.getEmail()))
            throw new AppException(ErrorCode.EMAIL_DUPLICATE);
        // Map to entity
        UserEntity newUserEntity = userMapper.toUserEntity(userRequest);
        // Encode password
        newUserEntity.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        // Mapping role
        List<RoleEntity> roleEntitySet = roleRepository.findAllById(userRequest.getRoleIdSet());
        newUserEntity.setRoleEntitySet(new HashSet<>(roleEntitySet));
        // Add & Return
        return userMapper.toUserResponse(
                userRepository.save(newUserEntity)
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_USER')")
    @Override
    public UserResponse update(UserRequest userRequest) {
        // Username or Email duplicate exception
        if (userRepository.existsByEmailExceptId(userRequest.getEmail(), userRequest.getId()))
            throw new AppException(ErrorCode.EMAIL_DUPLICATE);
        // Get old
        UserEntity foundUserEntity = userRepository.findById(userRequest.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        // Update
        userMapper.updateUserEntityFromRequest(foundUserEntity,userRequest);
        // Update password
        if (userRequest.getPassword() != null)
            foundUserEntity.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        // Update role
        List<RoleEntity> roleEntitySet = roleRepository.findAllById(userRequest.getRoleIdSet());
        foundUserEntity.setRoleEntitySet(new HashSet<>(roleEntitySet));
        // Save & Return
        return userMapper.toUserResponse(
                userRepository.save(foundUserEntity)
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_USER')")
    @Override
    public Long delete(Long id) {
        // Fetch & Not found/deleted exception
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        // Delete
        userRepository.delete(userEntity);
        // Return ID
        return id;
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DEACTIVATE_USER')")
    @Override
    public UserResponse deactivate(Long id) {
        // Get entity
        UserEntity oldUserEntity = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        // Deactivate
        oldUserEntity.setIsActive(false);
        // Save & Return
        return userMapper.toUserResponse(
                userRepository.save(oldUserEntity)
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ACTIVATE_USER')")
    @Override
    public UserResponse activate(Long id) {
        // Get entity
        UserEntity oldUserEntity = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        // Deactivate
        oldUserEntity.setIsActive(true);
        // Save & Return
        return userMapper.toUserResponse(
                userRepository.save(oldUserEntity)
        );
    }

    @Override
    public UserResponse register(UserRequest userRequest) {
        // Username or email duplicate exception
        if (userRepository.existsByUsername(userRequest.getUsername()))
            throw new AppException(ErrorCode.USERNAME_DUPLICATE);
        if (userRepository.existsByEmail(userRequest.getEmail()))
            throw new AppException(ErrorCode.EMAIL_DUPLICATE);
        // Mapping userRequest -> userEntity
        UserEntity userEntity = userMapper.toUserEntity(userRequest);
        // Encode password
        userEntity.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        // Add role USER by default
        RoleEntity roleEntity = roleRepository.findByName("USER")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        Set<RoleEntity> roleEntitySet = new HashSet<>();
        roleEntitySet.add(roleEntity);
        userEntity.setRoleEntitySet(roleEntitySet);
        // Save & Return
        return userMapper.toUserResponse(
                userRepository.save(userEntity)
        );
    }

    @Override
    public UserResponse updateInformation(UserRequest userRequest){
        // Username or Email duplicate exception
        if (userRepository.existsByEmailExceptId(userRequest.getEmail(), userRequest.getId()))
            throw new AppException(ErrorCode.EMAIL_DUPLICATE);
        // Get old
        UserEntity foundUserEntity = userRepository.findById(userRequest.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        // Update
        userMapper.updateUserEntityFromRequest(foundUserEntity,userRequest);
        // Save & Return
        return userMapper.toUserResponse(
                userRepository.save(foundUserEntity)
        );
    }

    @Override
    public UserResponse changePassword(UserChangePasswordRequest request) {
        // Get old
        UserEntity foundUserEntity = userRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        // Old password unmatched exception
        if (!passwordEncoder.matches(request.getOldPassword(), foundUserEntity.getPassword()))
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        // Change password
        foundUserEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        // Save & Return
        return userMapper.toUserResponse(
                userRepository.save(foundUserEntity)
        );
    }
}
