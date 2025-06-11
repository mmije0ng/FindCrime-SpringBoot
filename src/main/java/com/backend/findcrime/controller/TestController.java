package com.backend.findcrime.controller;

import com.backend.findcrime.apiPayload.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/test")
@RestController
public class TestController {

    @GetMapping
    public ApiResponse<Long> test(){
        Long id = 1L;

        return ApiResponse.onSuccess(id);
    }
}
