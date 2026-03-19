package com.example.demo;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;

import org.junit.jupiter.api.Test;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 *@author peilizhi 
 *@date 2026/3/19 23:23
 **/
@SpringBootTest
public class ALITest {


    @Test
    public void testQWen(@Autowired DashScopeChatModel chatModel){
        String string = chatModel.call("你是谁");
        System.out.println("string = " + string);
    }





    @Test
    public void testQWen1(@Autowired DashScopeImageModel imageModel){


        DashScopeImageOptions options  = DashScopeImageOptions.builder()
                .withModel("qwen-image-plus")
                .withPromptExtend(true)
                .build();
        String prompt = "请描述江南烟雨天，一位少女撑伞走在路上";

        ImagePrompt imagePrompt = new ImagePrompt(prompt,options);
        ImageResponse call = imageModel.call(imagePrompt);
        String url = call.getResult().getOutput().getUrl();
        System.out.println("url = " + url);
    }



}
