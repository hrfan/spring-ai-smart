package com.smart.framework.ai.controller;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Chat Hello Controller
 * 提供基础的 AI 聊天接口
 *
 * @author smart-framework
 */
@RequestMapping("/api/ai/chat")
@RestController
public class ChatHelloController {

    private static final Logger log = LoggerFactory.getLogger(ChatHelloController.class);

    /**
     * 注入 ChatModel 实例
     * 对话模型，调用阿里云百炼平台
     */
    @Resource(name = "deepSeekChatModel")
    private ChatModel chatModel;

    /**
     * 通用调用
     *
     * @param msg 消息内容
     * @return 响应结果
     */
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello(@RequestParam(name = "msg", defaultValue = "你好") String msg) {
        String call = chatModel.call(msg);
        log.info("返回结果：{}", call);
        return call;
    }

    /**
     * 普通调用（流式）
     *
     * @param msg 消息内容
     * @return 响应流
     */
    @RequestMapping(value = "/helloStream", method = RequestMethod.GET)
    public Flux<String> helloStream(@RequestParam(name = "msg", defaultValue = "你好") String msg) {
        Flux<String> stream = chatModel.stream(msg);
        log.info("返回结果：{}", stream);
        return stream;
    }
}

