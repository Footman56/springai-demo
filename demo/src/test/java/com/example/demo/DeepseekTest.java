package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import reactor.core.publisher.Flux;

/**
 *
 *@author peilizhi 
 *@date 2026/3/17 23:32
 **/
@SpringBootTest
public class DeepseekTest {


    @Test
    public void testChat(@Autowired DeepSeekChatModel chatModel){
        String called = chatModel.call("你是谁");
        System.out.println("called = " + called);
    }

    @Test
    public void testStream(@Autowired DeepSeekChatModel chatModel){
        Flux<String> stream = chatModel.stream("你是谁");
        stream.toIterable().forEach(System.out::print);
    }

    @Test
    public void testParam(@Autowired DeepSeekChatModel chatModel){
        DeepSeekChatOptions options  = DeepSeekChatOptions.builder()
                // 调小之后会变的保守
                .temperature(0.1d)
                // 输出到哪里截止
                .stop(Arrays.asList("注"))
                .build();
        Prompt prompt = new Prompt("请你用一首诗来描述黄昏",options);
        Flux<ChatResponse> stream = chatModel.stream(prompt);
        stream.toIterable().forEach(v -> {
            System.out.print(v.getResult().getOutput().getText());
        });
    }

    @Test
    public void testDeepSeekOutput(@Autowired DeepSeekChatModel chatModel){
        DeepSeekChatOptions options  = DeepSeekChatOptions.builder()
                // 调小之后会变的保守
                .temperature(1.8d)
                .model("deepseek-reasoner")
                // 输出到哪里截止
                .stop(Arrays.asList("注"))
                .build();
        Prompt prompt = new Prompt("请你用一首诗来描述黄昏",options);
        Flux<ChatResponse> stream = chatModel.stream(prompt);

        // 深度思考过程
        stream.toIterable().forEach(v -> {
            DeepSeekAssistantMessage assistantMessage =(DeepSeekAssistantMessage) v.getResult().getOutput();
            String reasoningContent = assistantMessage.getReasoningContent();
            if (null != reasoningContent){
                System.out.print(reasoningContent);
            }
        });

        System.out.println("====================");

        stream.toIterable().forEach(v -> {
            DeepSeekAssistantMessage assistantMessage =(DeepSeekAssistantMessage) v.getResult().getOutput();
            String reasoningContent = assistantMessage.getText();
            if (null != reasoningContent){
                System.out.print(reasoningContent);
            }
        });
    }
}
