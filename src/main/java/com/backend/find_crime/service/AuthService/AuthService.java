package com.backend.find_crime.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    // Access & RefreshToken 재발급
    void reissueTokens(HttpServletRequest request, HttpServletResponse response);
}
