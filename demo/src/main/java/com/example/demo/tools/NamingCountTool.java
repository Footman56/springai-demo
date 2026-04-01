package com.example.demo.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

/**
 *
 *@author peilizhi 
 *@date 2026/4/1 17:24
 **/
@Service
public class NamingCountTool {


    @Tool(description = "北京有多少个叫这个名字的人")
    String LocationNameCounts(
            @ToolParam(description = "名字")
            String name) {
        System.out.println(" 访问了命名工具 ");
        return "10个";
    }


}
