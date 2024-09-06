package com.rahulapp.service;

import com.rahulapp.entity.UserDtls;
import com.rahulapp.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service
public class JavaMailService {
    @Autowired
    private UserRepository repo;

    @Autowired
    private JavaMailSender mailSender;

    public void register(UserDtls user, String siteURL) throws UnsupportedEncodingException, MessagingException {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
        String encryptPassword = encoder.encode(user.getPassword());
        user.setPassword(encryptPassword);
        user.setPassword2(encryptPassword);
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(64);
        for (int i = 0; i < 64; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String randomCode = buffer.toString();

        //---------------------------------
        user.setVerificationCode(randomCode);
        user.setEnabled(false);
        repo.save(user);
        sendVerificationEmail(user, siteURL);
    }
    private void sendVerificationEmail(UserDtls user, String siteURL)throws MessagingException, UnsupportedEncodingException {
String toAddress = user.getEmail();
String fromAddress = "rj73008524@gmail.com";
String senderName = "Jalpari Technologies";
String subject = "Please verify your registration";
String content = "Dear [[name]],<br>"
+ "Please click the link below to verify your registration:<br>"
+ "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
+ "Thank you,<br>"
+ "Jellyfish.";

MimeMessage message = mailSender.createMimeMessage();
MimeMessageHelper helper = new MimeMessageHelper(message);

helper.setFrom(fromAddress, senderName);
helper.setTo(toAddress);
helper.setSubject(subject);

content = content.replace("[[name]]", user.getName());
String verifyURL = siteURL + "/user/verify?code=" + user.getVerificationCode();
content = content.replace("[[URL]]", verifyURL);

helper.setText(content, true);

mailSender.send(message);

    }
    public void passwordChangeEmail(UserDtls user, HttpSession session)throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "rj73008524@gmail.com";
        String senderName = "Jalpari Technologies";
        String subject = "This is the code to change password";
        Double randomCode =Math.floor(1000 + Math.random() * 9000);
        session.setAttribute("code",randomCode);
        String content = "Code: "+randomCode;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);

    }

    public boolean verify(String verificationCode){
        UserDtls user= repo.findByVerificationCode(verificationCode);
        if(user==null || user.isEnabled()){
            return false;
        }
        else{
            user.setEnabled(true);
            user.setVerificationCode(null);
            repo.save(user);
            return true;
        }
    }
}
