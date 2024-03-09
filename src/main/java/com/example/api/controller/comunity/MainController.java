package com.example.api.controller.comunity;

import com.example.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/")
    public ApiResponse<Object> index(){
        return ApiResponse.ok(null);
    }

}
