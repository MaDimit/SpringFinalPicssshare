package project.controller.managers;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.UUID;

@Component
public class SendMailSSL {

    //method used for generating a code used in registration proccess or forgot password one
    public static class randomStringGenerator {
        public static String generateString() {
            String uuid = UUID.randomUUID().toString();
            uuid = uuid.replace("-", "");
            return uuid;
        }
    }

    private static Session session;

    @Value("${email.username}")
    private String emailUsername;

    @Value("${email.pass}")
    private String emailPass;

    @PostConstruct
    public void setup() {
        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");
        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailUsername, emailPass);
                    }
                });
    }

    public static void sendMail(String username, String email, String code) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("picssshareweb@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
            message.setSubject("Registration confirmation");

                message.setText("Dear " + username + "," +
                        "\n\nThank you for registering. We are glad that you have become part of us." +
                        "\nUse this code to confirm your account: " + code + "\n " +
                        "\n\nYou can activate your account by going to \"Edit Profile\" section and enter it in the last field.");

                Transport.send(message);

                System.out.println("Done");



        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendResetPasswordEmail(String username, String email, String newPassword) {

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("picssshareweb@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
            message.setSubject("Reset password");

                message.setText("Dear " + username + "," +
                                "\n\nThis is your new password: " + newPassword+
                        "\nUse it to enter your account. \n" +
                                "\n\nYou can change your password later.");
                Transport.send(message);
                System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}

