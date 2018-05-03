package project.controller.servlets;


import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.UUID;

@Component
public class SendMailSSL {

    public static class randomStringGenerator {
        public static String generateString() {
            String uuid = UUID.randomUUID().toString();
            uuid = uuid.replace("-", "");
            return uuid;
        }
    }

    private static Session session;

    static {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("picssshareweb", "Picssshare1!");
                    }
                });

    }

    public static String sendMail(String username, String email) {
        String code = SendMailSSL.randomStringGenerator.generateString();
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
        return code;
    }
}

