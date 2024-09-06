package com.rahulapp.controller;

import com.rahulapp.entity.UserDtls;
import com.rahulapp.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController

public class AdminController {
    @Autowired
    private UserService userService;

    @GetMapping("/admin/users")
    public String getCourses(Model model) {
        model.addAttribute("users",userService.allUser());
//        return new ResponseEntity<>(userService.allUser(), HttpStatus.OK);
          return "main";
    }

    @PostMapping("/admin/add")
    public String add(@RequestBody UserDtls user) {return userService.signUp(user);}

    @Transactional
    @DeleteMapping ("admin/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        return userService.deleteUser(id);
    }

    @Transactional
    @PutMapping("admin/update/{id}")
    public String update(@PathVariable("id") Long id, @RequestBody UserDtls user) {return userService.userUpdate(id, user);
    }
}
