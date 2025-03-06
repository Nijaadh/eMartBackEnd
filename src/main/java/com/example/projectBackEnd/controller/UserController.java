package com.example.projectBackEnd.controller;

import com.example.projectBackEnd.dto.UserDto;
import com.example.projectBackEnd.service.UserService;
import com.example.projectBackEnd.util.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public CommonResponse registerUser(@RequestBody UserDto userDto){
        return userService.saveUser(userDto);
    }

    @GetMapping("/check-username")
    public boolean checkUserName(@RequestParam String userName) {
        return userService.isUserNameExists(userName);
    }
    @GetMapping("/check-email")
    public boolean checkEmail(@RequestParam String email) {
        return userService.isEmailExists(email);
    }

    @GetMapping("/getAll")
    public CommonResponse getAll(){
        return userService.getAll();
    }

}
