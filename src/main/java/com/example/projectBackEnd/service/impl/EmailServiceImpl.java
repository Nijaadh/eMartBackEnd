package com.example.projectBackEnd.service.impl;

import com.example.projectBackEnd.entity.Gift;
import com.example.projectBackEnd.entity.Order;
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

import static org.hibernate.tool.schema.SchemaToolingLogging.LOGGER;

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

    // Add these methods to your existing EmailServiceImpl class

    public void sendOrderConfirmationEmail(Order order, User user) {
        try {
            String subject = "Order Confirmation - Order #" + order.getId();

            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Dear ").append(user.getUserName()).append(",\n\n");
            messageBuilder.append("Thank you for your order. Your order has been received and is being processed.\n\n");
            messageBuilder.append("Order Details:\n");
            messageBuilder.append("Order ID: ").append(order.getId()).append("\n");
            messageBuilder.append("Order Date: ").append(order.getCreatedAt()).append("\n");
            messageBuilder.append("Total Amount: $").append(order.getOrderTotal()).append("\n\n");
            messageBuilder.append("Shipping Address:\n");
            messageBuilder.append(order.getReceiverAddress()).append("\n");
            messageBuilder.append("Zip: ").append(order.getZip()).append("\n\n");
            messageBuilder.append("We will notify you when your order has been shipped.\n\n");
            messageBuilder.append("Thank you for shopping with us!\n\n");
            messageBuilder.append("Best regards,\n");
            messageBuilder.append("Your Online Store Team");

            sendHtmlEmail(user.getEmail(), subject, messageBuilder.toString());
        } catch (Exception e) {
            LOGGER.error("Error sending order confirmation email", e);
        }
    }

    public void sendOrderStatusUpdateEmail(Order order, User user) {
        try {
            String subject = "Order Status Update - Order #" + order.getId();

            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Dear ").append(user.getUserName()).append(",\n\n");
            messageBuilder.append("We're writing to inform you that the status of your order has been updated.\n\n");
            messageBuilder.append("Order Details:\n");
            messageBuilder.append("Order ID: ").append(order.getId()).append("\n");
            messageBuilder.append("Order Date: ").append(order.getCreatedAt()).append("\n");
            messageBuilder.append("Current Status: ").append(order.getOrderStatus()).append("\n");
            messageBuilder.append("Payment Status: ").append(order.getPaymentStatus()).append("\n\n");

            // Add specific messages based on order status
            switch (order.getOrderStatus()) {
                case PROCESSING:
                    messageBuilder.append("Your order is now being processed. We'll notify you once it's shipped.\n\n");
                    break;
                case SHIPPED:
                    messageBuilder.append("Great news! Your order has been shipped and is on its way to you.\n\n");
                    break;
                case DELIVERED:
                    messageBuilder.append("Your order has been delivered. We hope you enjoy your purchase!\n\n");
                    break;
                default:
                    messageBuilder.append("Your order status has been updated. Please check your account for more details.\n\n");
            }

            messageBuilder.append("Thank you for shopping with us!\n\n");
            messageBuilder.append("Best regards,\n");
            messageBuilder.append("Your Online Store Team");

            sendHtmlEmail(user.getEmail(), subject, messageBuilder.toString());
        } catch (Exception e) {
            LOGGER.error("Error sending order status update email", e);
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
