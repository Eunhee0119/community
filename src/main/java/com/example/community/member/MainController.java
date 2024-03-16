package com.example.community.member;

import com.example.community.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/")
    public ApiResponse<Object> index(){
        return ApiResponse.ok(null);
    }

}
