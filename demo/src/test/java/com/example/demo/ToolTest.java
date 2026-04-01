package com.example.demo;

import com.example.demo.tools.NamingCountTool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 *@author peilizhi 
 *@date 2026/4/1 19:01
 **/
@SpringBootTest
public class ToolTest {

    ChatClient chatClient;
    @BeforeEach
    public  void init(@Autowired
                          DeepSeekChatModel chatModel,
                      @Autowired
                      NamingCountTool nameCountsTools) {
        chatClient = ChatClient.builder(chatModel)
                .defaultTools(nameCountsTools)
                .build();
    }
    @Test
    public void testChatOptions() {
        String content = chatClient.prompt()
                .user("北京有多少个叫裴立志的")
                // .tools() 也可以单独绑定当前对话
                .call()
                .content();
        System.out.println(content);
    }


}
