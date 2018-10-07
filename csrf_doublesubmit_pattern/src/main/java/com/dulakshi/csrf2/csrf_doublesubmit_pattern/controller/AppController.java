package com.dulakshi.csrf2.csrf_doublesubmit_pattern.controller;

import java.security.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dulakshi.csrf2.csrf_doublesubmit_pattern.model.LoginForm;
import com.dulakshi.csrf2.csrf_doublesubmit_pattern.model.SecondForm;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AppController{
    
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
    public String login(HttpSession session, @ModelAttribute LoginForm loginForm, HttpServletResponse response){
        if(loginForm.getEmail().equals("email@example.com") && loginForm.getPassword().equals("password")){        
            session.setAttribute("username", "email@example.com");
            response.addCookie(new Cookie("csrf", createCSRFToken(session)));
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

    @PostMapping("/verify")
    @ResponseBody
    public String verify(HttpSession session, @ModelAttribute SecondForm secondForm, @CookieValue(value = "csrf", defaultValue = "") String csrfCookie){
    
        if(!csrfCookie.equals("") && secondForm.getToken().equals(csrfCookie)){
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


    private String createCSRFToken(HttpSession session){
        String message = session.getId() + String.valueOf(session.getAttribute("username"));
        String csrf = DigestUtils.md5Hex(message).toUpperCase();
        return csrf;
    }
}