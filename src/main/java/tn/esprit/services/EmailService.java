package tn.esprit.services;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import tn.esprit.controllers.ResetPasswordController;

import java.util.Properties;

public class EmailService {
    // Mailtrap credentials
    private static final String EMAIL_USERNAME = "0f9cdf3bffc2c1"; // Your Mailtrap username
    private static final String EMAIL_PASSWORD = "db3557c4396ab5"; // Your Mailtrap password
    private static final String SMTP_HOST = "smtp.mailtrap.io";
    private static final int SMTP_PORT = 2525;

    public static void sendPasswordResetEmail(String recipientEmail, String resetToken) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("noreply@fnart.com")); // You can set any sender email
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Reset Your Password");

            String resetLink = "fnart://reset-password/" + resetToken;
            String emailContent = "Hello,\n\n"
                    + "You have requested to reset your password. Please click the link below to reset your password:\n\n"
                    + resetLink + "\n\n"
                    + "If you did not request this, please ignore this email.\n\n"
                    + "Best regards,\n"
                    + "Fnart Team";

            message.setText(emailContent);

            Transport.send(message);
            System.out.println("Reset password email sent successfully to " + recipientEmail);

            // Show success alert
            Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Email Sent");
            alert.setHeaderText(null);
                alert.setContentText("Password reset email has been sent to " + recipientEmail);
            alert.showAndWait();
            });
        } catch (MessagingException e) {
            System.err.println("Error sending reset password email: " + e.getMessage());
            e.printStackTrace();
            
            // Show error alert
            Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to send password reset email: " + e.getMessage());
            alert.showAndWait();
            });
        }
    }

    public void sendPasswordChangedConfirmation(String toEmail) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("noreply@fnart.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Password Changed Successfully - FNART");
            
            String emailContent = """
                <html>
                <body style='font-family: Arial, sans-serif; padding: 20px;'>
                    <h2 style='color: #4169E1;'>Password Changed Successfully</h2>
                    <p>Hello,</p>
                    <p>Your password has been successfully changed.</p>
                    <p>If you did not make this change, please contact our support team immediately.</p>
                    <br>
                    <p>Best regards,</p>
                    <p>The FNART Team</p>
                </body>
                </html>
            """;

            message.setContent(emailContent, "text/html; charset=utf-8");
            Transport.send(message);
            System.out.println("Password change confirmation email sent successfully to " + toEmail);

        } catch (MessagingException e) {
            System.err.println("Error sending confirmation email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void sendHtmlEmail(String recipientEmail, String subject, String htmlContent) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("noreply@fnart.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(message);
            System.out.println("HTML email sent successfully to " + recipientEmail);
        } catch (MessagingException e) {
            System.err.println("Error sending HTML email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}