package com.rahulapp.controller;

import com.rahulapp.entity.UserDtls;
import com.rahulapp.repository.UserRepository;
import com.rahulapp.service.JavaMailService;
import com.rahulapp.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;

@Controller
public class UserController {

    final private UserService userService;

    final private UserRepository userRepository;

    @Autowired
    private UserRepository repo;
    @Autowired
    private JavaMailService service;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    @GetMapping("/")
    public String homePage() {
        return "homepage";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }


    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute UserDtls user, RedirectAttributes redirAttrs, String siteURL, HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
        UserDtls storedUserDetails = repo.findByEmail(user.getEmail());

        if (storedUserDetails != null) {
            redirAttrs.addFlashAttribute("error", "Email already exists.");
            return "redirect:/register";
        }
        if ((user.getPassword()).equals(user.getPassword2())) {
            user.setRole("ROLE_USER");
            service.register(user, getSiteURL(request));
            redirAttrs.addFlashAttribute("error", "Registration successful.Please verify.");
            return "redirect:/register";
        } else {
            redirAttrs.addFlashAttribute("error", "Passwords mismatch.");
            return "redirect:/register";
        }
    }

    @GetMapping("/user/main")
    public String kain(Model model) {
        model.addAttribute("users", userService.allUser());
        System.out.println(model.getAttribute("users"));
        return "main";
    }

    @GetMapping("/user/verify")
    public String verify(@Param("code") String code) {
        System.out.println(code);
        if (service.verify(code)) {
            return "verify_success";
        } else {
            return "verify_fail";
        }
    }

    @GetMapping("/forget-password")
    public String forgetPass(){
        return "forgetPassword";
    }

    @PostMapping("/forget-password")
    public String forgetPassword(@RequestParam("email")String email, RedirectAttributes redirectAttributes,
                                 HttpSession session) throws MessagingException, UnsupportedEncodingException {
        UserDtls user = userRepository.findByEmail(email);
        if (userRepository.findByEmail(email)==null) {
            redirectAttributes.addFlashAttribute("error", "Email not found");
            return "redirect:/forget-password";
        }
        session.setAttribute("email" , userRepository.findByEmail(email).getEmail());
        service.passwordChangeEmail(user, session);
        return "redirect:/change-password";
    }

    @GetMapping("/change-password")
    public String chagePass(){
        return "changePassword";
    }

    @PostMapping("/change-password")
    public String updatePass( @RequestParam("code") String code
            , @RequestParam("password") String password
            , RedirectAttributes redirAttrs, HttpSession session ) {
        String email = session.getAttribute("email").toString();
        UserDtls storedUserDetails = repo.findByEmail(email);

        if (storedUserDetails == null) {
            redirAttrs.addFlashAttribute("error", "Email does not exists.");
            return "redirect:/change-password";
        }

        if (!session.getAttribute("code").toString().equals(code)) {
            redirAttrs.addFlashAttribute("error", "Code does not match");
            return "redirect:/change-password";
        }


        storedUserDetails.setPassword(new BCryptPasswordEncoder().encode(password));
        userRepository.save(storedUserDetails);
        session.removeAttribute(code);
        session.removeAttribute(email);
        return "redirect:/login";
    }
}
