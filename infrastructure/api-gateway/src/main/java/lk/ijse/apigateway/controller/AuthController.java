package lk.ijse.apigateway.controller;


import lk.ijse.apigateway.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/login")
    public Mono<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if ("student123".equals(username) && "password123".equals(password)) {
            String token = jwtUtil.generateToken(username);
            return Mono.just(Map.of("token", token));
        } else {
            return Mono.error(new RuntimeException("Unauthorized"));
        }
    }
}
