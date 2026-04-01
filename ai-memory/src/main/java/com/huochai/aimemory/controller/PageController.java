package com.huochai.aimemory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面 Controller
 *
 * @author peilizhi
 * @date 2026/3/22
 */
@Controller
public class PageController {

    /**
     * 对话页面
     */
    @GetMapping("/")
    public String chatPage() {
        return "chat";
    }
}
