package com.example.project_economic.impl;

import com.example.project_economic.dto.request.UserRequest;
import com.example.project_economic.dto.request.register.UserRegisterRequest;
import com.example.project_economic.dto.response.UserResponse;
import com.example.project_economic.entity.UserEntity;
import com.example.project_economic.mapper.UserMapper;
import com.example.project_economic.repository.UserRepository;
import com.example.project_economic.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).collect(Collectors.toList());
    }

    @Override
    public void save(UserRequest userRequest) throws Exception {
        //Encode password
        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        //Mapping userRequest -> userEntity
        //Adding/Updating userEntity to database
        userRepository.save(userMapper.toUserEntity(userRequest));
        return;
    }

    @Override
    public void save(UserRegisterRequest userRegisterRequest) {
        //Encode password
        userRegisterRequest.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        //Mapping userRequest -> userEntity
        //Adding/Updating userEntity to database
        UserEntity userEntity = userMapper.toUserEntity(userRegisterRequest);
        userEntity.setRole("USER");
        userRepository.save(userEntity);
        return;
    }

    @Override
    public void deleteById(Long userId) {
        //Delete selected userEntity by id
        userRepository.deleteById(userId);
        return;
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
    public UserEntity findUserById(Long userId) {
        return this.userRepository.findById(userId).get();
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
