package com.example.demo.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 *@author peilizhi 
 *@date 2026/4/1 17:37
 **/
@RestController
public class ToolController {
    //
    //@Resource(name = )
    //private ChatClient chatClient;


    @GetMapping("/tools")
    public String tools() {
        return "hello";
    }
}
