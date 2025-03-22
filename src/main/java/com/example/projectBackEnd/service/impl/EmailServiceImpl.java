package com.example.projectBackEnd.service.impl;

import com.example.projectBackEnd.entity.Gift;
import com.example.projectBackEnd.entity.User;
import com.example.projectBackEnd.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Locale;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    @Override
    public void sendRegistrationEmail(User user) {
        try {
            // Prepare the evaluation context
            final Context ctx = new Context(Locale.getDefault());
            ctx.setVariable("userName", user.getUserName());

            // Create the HTML body using Thymeleaf
            final String htmlContent = templateEngine.process("emails/registration-email", ctx);

            // Send email
            sendHtmlEmail(user.getEmail(), "Welcome to Gift Shop - Registration Successful", htmlContent);

            logger.info("Registration email sent to: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send registration email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    @Async
    @Override
    public void sendOrderConfirmationEmail(Gift gift, User user) {
        try {
            // Prepare the evaluation context
            final Context ctx = new Context(Locale.getDefault());
            ctx.setVariable("userName", user.getUserName());
            ctx.setVariable("orderId", gift.getId());
            ctx.setVariable("totalPrice", String.format("%.2f", gift.getTotalPrice()));
            ctx.setVariable("shippingAddress", gift.getRecieverAddress());

            // Create the HTML body using Thymeleaf
            final String htmlContent = templateEngine.process("emails/order-confirmation", ctx);

            // Send email
            sendHtmlEmail(user.getEmail(), "Gift Shop - Order Confirmation #" + gift.getId(), htmlContent);

            logger.info("Order confirmation email sent to: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send order confirmation email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    @Async
    @Override
    public void sendOrderReadyEmail(Gift gift, User user) {
        try {
            // Prepare the evaluation context
            final Context ctx = new Context(Locale.getDefault());
            ctx.setVariable("userName", user.getUserName());
            ctx.setVariable("orderId", gift.getId());
            ctx.setVariable("shippingAddress", gift.getRecieverAddress());

            // Create the HTML body using Thymeleaf
            final String htmlContent = templateEngine.process("emails/order-ready", ctx);

            // Send email
            sendHtmlEmail(user.getEmail(), "Gift Shop - Order #" + gift.getId() + " Ready for Delivery", htmlContent);

            logger.info("Order ready email sent to: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send order ready email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    @Async
    @Override
    public void sendOrderDeliveredEmail(Gift gift, User user) {
        try {
            // Prepare the evaluation context
            final Context ctx = new Context(Locale.getDefault());
            ctx.setVariable("userName", user.getUserName());
            ctx.setVariable("orderId", gift.getId());
            ctx.setVariable("shippingAddress", gift.getRecieverAddress());

            // Create the HTML body using Thymeleaf
            final String htmlContent = templateEngine.process("emails/order-delivered", ctx);

            // Send email
            sendHtmlEmail(user.getEmail(), "Gift Shop - Order #" + gift.getId() + " Delivered", htmlContent);

            logger.info("Order delivered email sent to: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send order delivered email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    /**
     * Helper method to send HTML emails
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true indicates HTML content

        emailSender.send(message);
    }
}
