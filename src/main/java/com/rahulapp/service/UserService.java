package com.rahulapp.service;

import com.rahulapp.entity.UserDtls;
import com.rahulapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;

//
//    @Autowired
//    private SessionRegistry sessionRegistry;
//    public List<String> getUsersFromSessionRegistry() {
//        return sessionRegistry.getAllPrincipals().stream()
//                .filter(u -> !sessionRegistry.getAllSessions(u, false).isEmpty())
//                .map(Object::toString)
//                .collect(Collectors.toList());
//    }
//
//    public boolean findActiveUser(){
//        for()
//        if(getUsersFromSessionRegistry().contains(userRepo.findAll().stream().map(UserDetails::getName).toList().get(0)))
//            return true;
//        return false;
//    }

    public String signUp(UserDtls u) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (userRepo.findByEmail(u.getEmail()) != null) {
            return "User Already Exist";
        } else {
            String encodedPassword = encoder.encode(u.getPassword());
            u.setPassword(encodedPassword);
            userRepo.save(u);
        }
        return "User Created";
    }

    public List<UserDtls> allUser() {
        System.out.println(userRepo.findAll());
        return userRepo.findAll();
    }

    public String deleteUser(Long id) {
        if (userRepo.findById(id).isPresent()) {
            userRepo.deleteById(id);
            return "deleted Successfully";
        }
        return "User Not Found";
    }

    public String userUpdate(Long id, UserDtls u) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Optional<UserDtls> optionalUser = userRepo.findById(id);
        if (optionalUser.isPresent()) {
            UserDtls user = optionalUser.get();
            user.setName(u.getName());
            user.setEmail(u.getEmail());
            String encodedPassword = encoder.encode(u.getPassword());
            user.setPassword(encodedPassword);
            user.setGender(u.getGender());
            userRepo.save(user);
            return "User updated successfully";
        }
        return "User not found";
    }

}