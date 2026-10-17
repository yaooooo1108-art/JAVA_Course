package com.group17.enrollment_service.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private static final Map<String, String> STUDENTS = Map.of(
            "s1121610", "10",
            "s1121611", "11",
            "s1121612", "12",
            "s1121613", "13",
            "s1121614", "14",
            "s1121615", "15"
    );

    public String login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("請輸入帳號與密碼");
        }

        // 老師：admin/1234
        if ("admin".equals(username) && "1234".equals(password)) {
            return "TEACHER";
        }

        // 學生：固定清單
        if (STUDENTS.containsKey(username) && STUDENTS.get(username).equals(password)) {
            return "STUDENT";
        }

        throw new IllegalArgumentException("帳號或密碼錯誤");
    }
}
