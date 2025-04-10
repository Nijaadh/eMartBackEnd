package com.example.projectBackEnd.controller;

import com.example.projectBackEnd.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin
public class AdminController {
    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    @GetMapping("/user/count")
    public long getUserCount() {
        return adminService.getUserCount();
    }
    @GetMapping("/item/count")
    public long getItemCount() {
        return adminService.getItemCount();
    }
    @GetMapping("/gift/count-new-gifts")
    public long countActiveGifts() {
        return adminService.countActiveGifts();
    }
    @GetMapping("/gift/count-processing-gifts")
    public long countProcessingGifts(){
        return adminService.countProcessingGifts();
    }
    @GetMapping("/gift/count-delivered-gifts")
    public long countDeliveredGifts() {
        return adminService.countDeliveredGifts();
    }


    @GetMapping("gift/total-price-paid-gifts")
    public Double getTotalPriceForPaidGifts() {
        return adminService.getTotalPriceForPaidGifts();
    }

    @GetMapping("/last-year-monthly-total-price")
    public Double[] getLastYearMonthlyTotalPrice() {
        return adminService.getLastYearMonthlyTotalPrice();
    }
}
