package com.example.projectBackEnd.service.impl;

import com.example.projectBackEnd.constant.CommonMsg;
import com.example.projectBackEnd.constant.CommonStatus;
import com.example.projectBackEnd.dto.ItemsDto;
import com.example.projectBackEnd.dto.UserDto;
import com.example.projectBackEnd.entity.Items;
import com.example.projectBackEnd.entity.Role;
import com.example.projectBackEnd.entity.User;
import com.example.projectBackEnd.repo.RoleRepo;
import com.example.projectBackEnd.repo.UserRepo;
import com.example.projectBackEnd.service.UserService;
import com.example.projectBackEnd.util.CommonResponse;
import com.example.projectBackEnd.util.CommonValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.hibernate.tool.schema.SchemaToolingLogging.LOGGER;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    private final RoleRepo roleRepo;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder, RoleRepo roleRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.roleRepo = roleRepo;
    }



    @Override
    public CommonResponse saveUser(UserDto userDto) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            List<String> validationList = userValidation(userDto);
            if (!validationList.isEmpty()) {
                commonResponse.setErrorMessages(validationList);
                return commonResponse;
            }

            User user = castUserDtoToEntity(userDto);
            user = userRepo.save(user);
            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList(user));
        } catch (Exception e) {
            LOGGER.error("/**************** Exception in ProductService -> saveProduct()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while saving the product."));
        }
        return commonResponse;
    }

    @Override
    public CommonResponse getAll() {

            CommonResponse commonResponse = new CommonResponse();
            try {
                List<Object> userDtoList = userRepo.findAll().stream()
                        .filter(user -> user.getCommonStatus() == CommonStatus.ACTIVE)  // Assuming you have CommonStatus for Users
                        .map(this::castUserEntityToDto)  // Cast User entities to DTOs (you'll implement this method)
                        .collect(Collectors.toList());

                commonResponse.setStatus(true);
                commonResponse.setPayload(userDtoList);  // Directly set the list of DTOs in the payload
            } catch (Exception e) {
                LOGGER.error("/**************** Exception in UserService -> getAllUsers()", e);
                commonResponse.setStatus(false);
                commonResponse.setErrorMessages(Collections.singletonList("An error occurred while fetching users."));
            }
            return commonResponse;


    }

    private User castUserDtoToEntity(UserDto userDto){

        Role role = roleRepo.findById("Customer").get();
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(role);

        User user=new User();
        user.setRole(userRoles);
        user.setUserName(userDto.getUserName());
        user.setAddress(userDto.getAddress());
        user.setEmail(userDto.getEmail());
        user.setTel(userDto.getTel());
        user.setImage(userDto.getImage());
        user.setCommonStatus(CommonStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return user;
    }
    private UserDto castUserEntityToDto(User user){
        UserDto userDto=new UserDto();
        userDto.setUserName(user.getUserName());
        userDto.setId(String.valueOf(user.getId()));
        userDto.setAddress(user.getAddress());
        userDto.setTel(user.getTel());
        userDto.setImage(user.getImage());
        return userDto;
    }

    public boolean isUserNameExists(String userName) {
        Optional<User> user = userRepo.findByUserName(userName);
        return user.isPresent();
    }
    public boolean isEmailExists(String email) {
        Optional<User> user = userRepo.findByEmail(email);
        return user.isPresent();
    }



    public String getEncodedPassword(String password) {
        return passwordEncoder.encode(password);
    }
    private List<String> userValidation(UserDto userDto) {
        List<String> validationList = new ArrayList<>();
        if (CommonValidation.stringNullValidation(userDto.getUserName())) {
            validationList.add(CommonMsg.EMPTY_USERNAME);
        }
        if(isUserNameExists(userDto.getUserName())){
            validationList.add(CommonMsg.USERNAME_IS_ALREADY_EXITED);
        }
        if (CommonValidation.stringNullValidation(userDto.getAddress())) {
            validationList.add(CommonMsg.EMPTY_ADDRESS);
        }
        if (CommonValidation.stringNullValidation(userDto.getPassword())) {
            validationList.add(CommonMsg.EMPTY_PASSWORD);
        }
        if (CommonValidation.stringNullValidation(userDto.getEmail())) {
            validationList.add(CommonMsg.EMPTY_EMAIL);
        }
        if(isEmailExists(userDto.getEmail())){
            validationList.add(CommonMsg.EMAIL_IS_EXITED);
        }
        if (CommonValidation.stringNullValidation(userDto.getTel())) {
            validationList.add(CommonMsg.EMPTY_CONTACT_NUMBER);
        }
        return validationList;
    }
}
