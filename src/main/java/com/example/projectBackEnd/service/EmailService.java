package com.example.projectBackEnd.service;

import com.example.projectBackEnd.entity.Gift;
import com.example.projectBackEnd.entity.User;

public interface EmailService {
    void sendRegistrationEmail(User user);
    void sendOrderConfirmationEmail(Gift gift, User user);
    void sendOrderReadyEmail(Gift gift, User user);
    void sendOrderDeliveredEmail(Gift gift, User user);
}
