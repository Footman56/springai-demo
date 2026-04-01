package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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


    @Test
    public void testDeepSeekChat(@Autowired DeepSeekChatModel chatModel){

        ChatClient chatClient = ChatClient
                .builder(chatModel)
                .build();
        String content = chatClient
                .prompt("你是谁")
                .call().content();
        System.out.println("content = " + content);

    }

    @Test
    public void testBoolOut(@Autowired DeepSeekChatModel chatModel) {
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        Boolean isComplain = chatClient
                .prompt()
                .system("""
            请判断用户信息是否表达了投诉意图?
            只能用 true 或 false 回答，不要输出多余内容
            """)
                .user("你们家的快递迟迟不到,我要退货！")
                .call()
                .entity(Boolean.class);

        // 分支逻辑
        if (Boolean.TRUE.equals(isComplain)) {
            System.out.println("用户是投诉，转接人工客服！");
        } else {
            System.out.println("用户不是投诉，自动流转客服机器人。");
            // todo 继续调用 客服ChatClient进行对话
        }
    }


    @Test
    public void testEntityOut(@Autowired ChatClient deepseekChatClient) {
        Address address = deepseekChatClient.prompt()
                .system("""
                        请从下面这条文本中提取收货信息
                        """)
                .user("收货人：张三，电话13588888888，地址：浙江省杭州市西湖区文一西路100号8幢202室")
                .call()
                // 本质应该是将结果转换成json格式，之后反序列化为Address对象
                .entity(Address.class);
        System.out.println(address);
    }

    public record Address(
            String name,        // 收件人姓名
            String phone,       // 联系电话
            String province,    // 省
            String city,        // 市
            String district,    // 区/县
            String detail       // 详细地址
    ) {}


    public record ActorsFilm(
            // 演员
            String actor,

            // 电影名称
            String film
    ) {}

    @Test
    public void testLowEntityOut(
            @Autowired ChatClient deepseekChatClient) {
        BeanOutputConverter<List<ActorsFilm>> beanOutputConverter =
                new BeanOutputConverter<>(new ParameterizedTypeReference<>() {});

        String format = beanOutputConverter.getFormat();

        String actor = "周星驰";

        String template = """
                提供5部{actor}参演的电影,返回JSON数组格式.
                {format}
                """;

        PromptTemplate promptTemplate = PromptTemplate.builder()
                .template(template)
                .variables(Map.of("actor", actor, "format", format))
                .build();
        ChatResponse response = deepseekChatClient.prompt(
                promptTemplate.create()
        ).call().chatResponse();

        List<ActorsFilm> actorsFilms = beanOutputConverter.convert(response.getResult()
                .getOutput()
                .getText());
        System.out.println(actorsFilms);
    }
}
