package com.example.projectBackEnd.service.impl;

import com.example.projectBackEnd.entity.Gift;
import com.example.projectBackEnd.entity.Order;
import com.example.projectBackEnd.entity.User;
import com.example.projectBackEnd.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger LOGGER = Logger.getLogger(EmailServiceImpl.class.getName());

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public void sendRegistrationEmail(User user) {
        try {
            Context context = new Context();
            context.setVariable("userName", user.getUserName());

            String process = templateEngine.process("emails/registration-email", context);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setSubject("Welcome to Gift Shop!");
            helper.setText(process, true);
            helper.setTo(user.getEmail());
            mailSender.send(mimeMessage);

            LOGGER.info("Registration email sent to: " + user.getEmail());
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Failed to send registration email", e);
        }
    }

    @Override
    public void sendOrderConfirmationEmail(Gift gift, User user) {
        // Legacy method for Gift entity
    }

    @Override
    public void sendOrderReadyEmail(Gift gift, User user) {
        // Legacy method for Gift entity
    }

    @Override
    public void sendOrderDeliveredEmail(Gift gift, User user) {
        // Legacy method for Gift entity
    }

    @Override
    public void sendOrderConfirmationEmail(Order order, User user) {
        try {
            Context context = new Context();
            context.setVariable("userName", user.getUserName());
            context.setVariable("orderId", order.getId());
            context.setVariable("totalPrice", order.getOrderTotal());
            context.setVariable("shippingAddress", order.getReceiverAddress());

            String process = templateEngine.process("emails/order-confirmation", context);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setSubject("Order Confirmation - #" + order.getId());
            helper.setText(process, true);
            helper.setTo(user.getEmail());
            mailSender.send(mimeMessage);

            LOGGER.info("Order confirmation email sent to: " + user.getEmail());
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Failed to send order confirmation email", e);
        }
    }

    @Override
    public void sendOrderStatusUpdateEmail(Order order, User user) {
        try {
            Context context = new Context();
            context.setVariable("userName", user.getUserName());
            context.setVariable("orderId", order.getId());
            context.setVariable("shippingAddress", order.getReceiverAddress());

            String templateName;
            String subject;

            switch (order.getOrderStatus()) {
                case PROCESSING:
                    templateName = "emails/order-processing";
                    subject = "Your Order is Being Processed - #" + order.getId();
                    break;
                case SHIPPED:
                    templateName = "emails/order-shipped";
                    subject = "Your Order Has Shipped - #" + order.getId();
                    break;
                case DELEVERD:
                    templateName = "emails/order-delivered";
                    subject = "Your Order Has Been Delivered - #" + order.getId();
                    break;
                default:
                    templateName = "emails/order-confirmation";
                    subject = "Order Status Update - #" + order.getId();
            }

            String process = templateEngine.process(templateName, context);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setSubject(subject);
            helper.setText(process, true);
            helper.setTo(user.getEmail());
            mailSender.send(mimeMessage);

            LOGGER.info("Order status update email sent to: " + user.getEmail() + " for status: " + order.getOrderStatus());
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Failed to send order status update email", e);
        }
    }

    // Helper method to send a generic email with a template
    private void sendTemplateEmail(String to, String subject, String templateName, Context context) {
        try {
            String htmlContent = templateEngine.process(templateName, context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            LOGGER.info("Email sent successfully to: " + to + " with subject: " + subject);
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Failed to send email to: " + to, e);
        }
    }
}

