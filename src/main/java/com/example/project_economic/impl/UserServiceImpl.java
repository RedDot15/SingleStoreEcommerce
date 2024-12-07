package com.example.project_economic.impl;

import com.amazonaws.services.dlm.model.ResourceNotFoundException;
import com.example.project_economic.dto.request.UserRequest;
import com.example.project_economic.dto.response.UserResponse;
import com.example.project_economic.entity.UserEntity;
import com.example.project_economic.exception.DuplicateException;
import com.example.project_economic.exception.WrongPasswordException;
import com.example.project_economic.mapper.UserMapper;
import com.example.project_economic.repository.UserRepository;
import com.example.project_economic.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Service
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    @Override
    public Set<UserResponse> getAll() {
        return userRepository.findAll()
                .stream().map(userMapper::toUserResponse)
                .collect(Collectors.toSet());
    }

    @Override
    public UserResponse add(UserRequest userRequest) {
        // Check if confirm password match
        if (!userRequest.getPassword().equals(userRequest.getConfirmPassword()))
            throw new IllegalArgumentException("Confirm password do not match.");
        // Username or email duplicate exception
        if (userRepository.existsByUsernameOrEmail(userRequest.getUsername(),userRequest.getEmail()))
            throw new DuplicateException("Username or email duplicate.");
        // Role blank exception
        if (userRequest.getRole() != null && userRequest.getRole().isBlank())
            throw new IllegalArgumentException("Role is blank.");
        // Encode password
        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        // Map to entity
        UserEntity newUserEntity = userMapper.toUserEntity(userRequest);
        // Add & Return
        return userMapper.toUserResponse(
                userRepository.save(newUserEntity)
        );
    }

    @Override
    public UserResponse update(UserRequest userRequest) {
        // Check if confirm password match
        if (!userRequest.getPassword().equals(userRequest.getConfirmPassword()))
            throw new IllegalArgumentException("Confirm password do not match.");
        // Username or Email duplicate exception
        if (userRequest.getEmail() != null & userRepository.existsByEmailExceptId(userRequest.getEmail(), userRequest.getId()))
            throw new DuplicateException("Email duplicate.");
        // Role blank exception
        if (userRequest.getRole() != null && userRequest.getRole().isBlank())
            throw new IllegalArgumentException("Role is blank.");
        // Get old
        UserEntity foundUserEntity = userRepository.findById(userRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        // Update
        userMapper.updateUserEntityFromRequest(foundUserEntity,userRequest);
        if (userRequest.getPassword() != null && !userRequest.getPassword().isBlank())
            foundUserEntity.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        // Save & Return
        return userMapper.toUserResponse(
                userRepository.save(foundUserEntity)
        );
    }

    @Override
    public Long delete(Long id) {
        // Fetch & Not found/deleted exception
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        // Delete
        userRepository.delete(userEntity);
        // Return ID
        return id;
    }

    @Override
    public UserResponse deactivate(Long id) {
        // Get entity
        UserEntity oldUserEntity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        // Deactivate
        oldUserEntity.setIsActive(false);
        // Save & Return
        return userMapper.toUserResponse(
                userRepository.save(oldUserEntity)
        );
    }

    @Override
    public UserResponse activate(Long id) {
        // Get entity
        UserEntity oldUserEntity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        // Deactivate
        oldUserEntity.setIsActive(true);
        // Save & Return
        return userMapper.toUserResponse(
                userRepository.save(oldUserEntity)
        );
    }

    @Override
    public UserResponse register(UserRequest userRequest) {
        // Check if confirm password match
        if (!userRequest.getPassword().equals(userRequest.getConfirmPassword()))
            throw new IllegalArgumentException("Confirm password do not match.");
        // Username or email duplicate exception
        if (userRepository.existsByUsernameOrEmail(userRequest.getUsername(),userRequest.getEmail()))
            throw new DuplicateException("Username or email duplicate.");
        // Encode password
        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        // Mapping userRequest -> userEntity
        UserEntity userEntity = userMapper.toUserEntity(userRequest);
        userEntity.setRole("USER");
        // Save & Return
        return userMapper.toUserResponse(
                userRepository.save(userEntity)
        );
    }

    @Override
    public UserResponse updateInformation(UserRequest userRequest){
        // Username or Email duplicate exception
        if (userRequest.getEmail() != null & userRepository.existsByEmailExceptId(userRequest.getEmail(), userRequest.getId()))
            throw new DuplicateException("Email duplicate.");
        // Get old
        UserEntity foundUserEntity = userRepository.findById(userRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        // Update
        userMapper.updateUserEntityFromRequest(foundUserEntity,userRequest);
        // Save & Return
        return userMapper.toUserResponse(
                userRepository.save(foundUserEntity)
        );
    }

    @Override
    public UserResponse changePassword(UserRequest userRequest) {
        // Check if confirm password match
        if (!userRequest.getPassword().equals(userRequest.getConfirmPassword()))
            throw new IllegalArgumentException("Confirm password do not match.");
        // Get old
        UserEntity foundUserEntity = userRepository.findById(userRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        // Old password unmatch exception
        if (!passwordEncoder.matches(userRequest.getOldPassword(), foundUserEntity.getPassword()))
            throw new WrongPasswordException("Old password is wrong.");
        // Change password
        foundUserEntity.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        // Save & Return
        return userMapper.toUserResponse(
                userRepository.save(foundUserEntity)
        );
    }

}
