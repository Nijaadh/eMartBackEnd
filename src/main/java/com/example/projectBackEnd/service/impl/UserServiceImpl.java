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
import com.example.projectBackEnd.service.EmailService;
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
    private final EmailService emailService;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder, RoleRepo roleRepo, EmailService emailService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.roleRepo = roleRepo;
        this.emailService = emailService;
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
            emailService.sendRegistrationEmail(user);
            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList(user));
        } catch (Exception e) {
            LOGGER.error("/**************** Exception in UserService -> saveUser()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while saving the user."));
        }
        return commonResponse;
    }

    @Override
    public CommonResponse getAll() {
        CommonResponse commonResponse = new CommonResponse();
        try {
            List<Object> userDtoList = userRepo.findAll().stream()
                    .filter(user -> user.getCommonStatus() == CommonStatus.ACTIVE)
                    .map(this::castUserEntityToDto)
                    .collect(Collectors.toList());

            commonResponse.setStatus(true);
            commonResponse.setPayload(userDtoList);
        } catch (Exception e) {
            LOGGER.error("/**************** Exception in UserService -> getAllUsers()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while fetching users."));
        }
        return commonResponse;
    }

    @Override
    public CommonResponse getUserById(Long id) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            Optional<User> userOptional = userRepo.findById(id);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (user.getCommonStatus() == CommonStatus.ACTIVE) {
                    UserDto userDto = castUserEntityToDto(user);
                    commonResponse.setStatus(true);
                    commonResponse.setPayload(Collections.singletonList(userDto));
                } else {
                    commonResponse.setStatus(false);
                    commonResponse.setErrorMessages(Collections.singletonList("User is not active"));
                }
            } else {
                commonResponse.setStatus(false);
                commonResponse.setErrorMessages(Collections.singletonList("User not found with ID: " + id));
            }
        } catch (Exception e) {
            LOGGER.error("/**************** Exception in UserService -> getUserById()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while fetching the user: " + e.getMessage()));
        }
        return commonResponse;
    }

    @Override
    public CommonResponse updateUser(UserDto userDto) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            if (userDto.getId() == null || userDto.getId().isEmpty()) {
                commonResponse.setStatus(false);
                commonResponse.setErrorMessages(Collections.singletonList("User ID is required for update"));
                return commonResponse;
            }

            Long userId = Long.parseLong(userDto.getId());
            Optional<User> userOptional = userRepo.findById(userId);

            if (!userOptional.isPresent()) {
                commonResponse.setStatus(false);
                commonResponse.setErrorMessages(Collections.singletonList("User not found with ID: " + userId));
                return commonResponse;
            }

            User existingUser = userOptional.get();

            // Check if username is being changed and if it already exists
            if (!existingUser.getUserName().equals(userDto.getUserName())) {
                Optional<User> userWithSameUsername = userRepo.findByUserName(userDto.getUserName());
                if (userWithSameUsername.isPresent() && !userWithSameUsername.get().getId().equals(userId)) {
                    commonResponse.setStatus(false);
                    commonResponse.setErrorMessages(Collections.singletonList(CommonMsg.USERNAME_IS_ALREADY_EXITED));
                    return commonResponse;
                }
            }

            // Check if email is being changed and if it already exists
            if (!existingUser.getEmail().equals(userDto.getEmail())) {
                Optional<User> userWithSameEmail = userRepo.findByEmail(userDto.getEmail());
                if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(userId)) {
                    commonResponse.setStatus(false);
                    commonResponse.setErrorMessages(Collections.singletonList(CommonMsg.EMAIL_IS_EXITED));
                    return commonResponse;
                }
            }

            // Update user fields
            existingUser.setUserName(userDto.getUserName());
            existingUser.setAddress(userDto.getAddress());
            existingUser.setEmail(userDto.getEmail());
            existingUser.setTel(userDto.getTel());

            // Update image if provided
            if (userDto.getImage() != null && !userDto.getImage().isEmpty()) {
                existingUser.setImage(userDto.getImage());
            }

            // Update password if provided
            if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }

            // Save updated user
            User updatedUser = userRepo.save(existingUser);

            commonResponse.setStatus(true);
            commonResponse.setPayload(Collections.singletonList(castUserEntityToDto(updatedUser)));

        } catch (Exception e) {
            LOGGER.error("/**************** Exception in UserService -> updateUser()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while updating the user: " + e.getMessage()));
        }
        return commonResponse;
    }

    @Override
    public CommonResponse deleteUser(Long id) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            Optional<User> userOptional = userRepo.findById(id);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                // Soft delete - change status to INACTIVE
                user.setCommonStatus(CommonStatus.INACTIVE);
                userRepo.save(user);

                commonResponse.setStatus(true);
                commonResponse.setPayload(Collections.singletonList("User successfully deleted"));
            } else {
                commonResponse.setStatus(false);
                commonResponse.setErrorMessages(Collections.singletonList("User not found with ID: " + id));
            }
        } catch (Exception e) {
            LOGGER.error("/**************** Exception in UserService -> deleteUser()", e);
            commonResponse.setStatus(false);
            commonResponse.setErrorMessages(Collections.singletonList("An error occurred while deleting the user: " + e.getMessage()));
        }
        return commonResponse;
    }

    private User castUserDtoToEntity(UserDto userDto) {
        Role role = roleRepo.findById("Customer").get();
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(role);

        User user = new User();
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

    private UserDto castUserEntityToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setUserName(user.getUserName());
        userDto.setId(String.valueOf(user.getId()));
        userDto.setAddress(user.getAddress());
        userDto.setEmail(user.getEmail());
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
        if (isUserNameExists(userDto.getUserName())) {
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
        if (isEmailExists(userDto.getEmail())) {
            validationList.add(CommonMsg.EMAIL_IS_EXITED);
        }
        if (CommonValidation.stringNullValidation(userDto.getTel())) {
            validationList.add(CommonMsg.EMPTY_CONTACT_NUMBER);
        }
        return validationList;
    }
}
