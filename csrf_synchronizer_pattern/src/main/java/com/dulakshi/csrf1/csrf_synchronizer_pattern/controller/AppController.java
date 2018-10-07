package com.dulakshi.csrf1.csrf_synchronizer_pattern.controller;

import java.util.HashMap;
import javax.servlet.http.HttpSession;

import com.dulakshi.csrf1.csrf_synchronizer_pattern.model.LoginForm;
import com.dulakshi.csrf1.csrf_synchronizer_pattern.model.SecondForm;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AppController{

    private static HashMap<String, String> sessionCSRFMap = new HashMap<>();
    
    @GetMapping("")
    public String index(HttpSession session){
        if(!isAuthenticated(session)){
            return "redirect:/signin";
        }else{
            return "redirect:/home";
        }
    }


    @GetMapping("/home")
    public String home(HttpSession session, Model model){
        if(!isAuthenticated(session)){
            return "redirect:/signin";
        }else{
            model.addAttribute("secondForm", new SecondForm());
            return "index";
        }
    }

    @GetMapping("/signin")
    public String signin(HttpSession session, Model model){
        if(isAuthenticated(session)){
            return "redirect:/home";
        }else{
            model.addAttribute("loginForm", new LoginForm());
            return "signin";
        }
    }


    @PostMapping("/login")
    public String login(HttpSession session, @ModelAttribute LoginForm loginForm){
        if(loginForm.getEmail().equals("email@example.com") && loginForm.getPassword().equals("password")){ 
            createCSRFToken(session);       
            session.setAttribute("username", "email@example.com");
            return "redirect:/home";
        }else{
            return "redirect:/signin";
        }
    }


    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.removeAttribute("username");
        session.invalidate();
        return "redirect:/signin";
    }

    @PostMapping("/csrf")
    @ResponseBody
    public String getcsrf(HttpSession session){
        return readCSRF(session);
    }

    @PostMapping("/verify")
    @ResponseBody
    public String verify(HttpSession session, @ModelAttribute SecondForm secondForm){
        if(secondForm.getToken().equals(readCSRF(session))){
            return "Valid Token";
        }else{
            return "Invalid Token";
        }
    }



    private boolean isAuthenticated(HttpSession session){
        if(session.getAttribute("username") == null){
            return false;
        }else{
            return true;
        }
    }

    private void createCSRFToken(HttpSession session){
        String message = session.getId() + String.valueOf(session.getAttribute("username"));
        String csrf = DigestUtils.md5Hex(message).toUpperCase();
        
        sessionCSRFMap.put(session.getId(), csrf);
     }

    private String readCSRF(HttpSession session){
        return sessionCSRFMap.get(session.getId());
    }

}