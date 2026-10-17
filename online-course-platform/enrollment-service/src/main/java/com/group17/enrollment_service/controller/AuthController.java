package com.group17.enrollment_service.controller;

import com.group17.enrollment_service.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> body) {
        String username = body.getOrDefault("username", "");
        String password = body.getOrDefault("password", "");

        String role = authService.login(username, password);
        // 回傳 role + username，前端存 session 用
        return Map.of("role", role, "username", username);
    }

    // 讓錯誤回傳 401（登入失敗比較合理）
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(IllegalArgumentException.class)
    public Map<String, String> handleBadLogin(IllegalArgumentException e) {
        return Map.of("message", e.getMessage());
    }
}
