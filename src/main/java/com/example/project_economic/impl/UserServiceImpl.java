package com.example.project_economic.impl;

import com.example.project_economic.dto.request.UserRequest;
import com.example.project_economic.dto.request.register.UserRegisterRequest;
import com.example.project_economic.dto.response.UserResponse;
import com.example.project_economic.entity.UserEntity;
import com.example.project_economic.mapper.UserMapper;
import com.example.project_economic.repository.UserRepository;
import com.example.project_economic.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
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
    public Boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public Boolean existsByUsernameOrEmail(String username, String email) {
        return userRepository.existsByUsernameOrEmail(username, email);
    }

    @Override
    public Boolean existsByUsernameOrEmailExceptId(String username, String email, Long id) {
        return userRepository.existsByUsernameOrEmailExceptId(username, email, id);
    }

    @Override
    public UserResponse getFirstById(Long id) {
        return userMapper.toUserResponse(
                userRepository.findFirstById(id)
        );
    }

    @Override
    public UserResponse create(UserRequest userRequest) {
        //Map to entity
        UserEntity newUserEntity = userMapper.toUserEntity(userRequest);
        //Encode password
        newUserEntity.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        //Save
        return userMapper.toUserResponse(userRepository.save(newUserEntity));
    }

    @Override
    public UserResponse update(UserRequest userRequest) {
        //Get old
        UserEntity foundUserEntity = userRepository.findFirstById(userRequest.getId());
        //Update
        userMapper.updateUserEntityFromRequest(foundUserEntity,userRequest);
        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()){
            foundUserEntity.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }
        //Save
        return userMapper.toUserResponse(userRepository.save(foundUserEntity));
    }

    @Override
    public void save(UserRegisterRequest userRegisterRequest) {
        //Encode password
        userRegisterRequest.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        //Mapping userRequest -> userEntity
        UserEntity userEntity = userMapper.toUserEntity(userRegisterRequest);
        //Set role
        userEntity.setRole("USER");

        userRepository.save(userEntity);
        return;
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserResponse deactivate(Long id) {
        //Get entity
        UserEntity oldUserEntity = userRepository.findFirstById(id);
        //Deactivate
        oldUserEntity.setIsActive(false);
        //Save & Return
        return userMapper.toUserResponse(
                userRepository.save(oldUserEntity)
        );
    }

    @Override
    public UserResponse activate(Long id) {
        //Get entity
        UserEntity oldUserEntity = userRepository.findFirstById(id);
        //Deactivate
        oldUserEntity.setIsActive(true);
        //Save & Return
        return userMapper.toUserResponse(
                userRepository.save(oldUserEntity)
        );
    }

    @Override
    public UserEntity createUser(UserEntity userEntity) throws Exception {
//        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userEntity.setRole("USER");
        Optional<UserEntity> user = userRepository.findByUsername(userEntity.getUsername());
        if(user.isPresent()){
            throw new Exception("Tài khoản "+ userEntity.getUsername()+" đã tồn tại!");
        }
        UserEntity userSaved=this.userRepository.save(userEntity);
        return userSaved;
    }

    @Override
    public UserEntity update(UserEntity userEntity, Long userId){
        UserEntity userEntityFind = new UserEntity();
        try{
            userEntityFind = this.userRepository.findById(userId).get();
            userEntityFind.setPassword(this.userRepository.findById(userId).get().getPassword());
            userEntityFind.setUsername(userEntity.getUsername() != "" ? userEntity.getUsername() : userEntityFind.getUsername());
            userEntityFind.setEmail(userEntity.getEmail() != "" ? userEntity.getEmail() : userEntityFind.getEmail());
            userEntityFind.setAddress(userEntity.getAddress() != "" ?  userEntity.getAddress() : userEntityFind.getAddress());
            userEntityFind.setPhoneNumber(userEntity.getPhoneNumber() != "" ?  userEntity.getPhoneNumber() : userEntityFind.getPhoneNumber());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return this.userRepository.save(userEntityFind);
    }
}
