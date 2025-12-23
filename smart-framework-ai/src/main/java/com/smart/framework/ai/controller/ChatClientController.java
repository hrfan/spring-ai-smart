package com.smart.framework.ai.controller;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import com.smart.framework.ai.model.StudentRecord;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Chat Client Controller
 * 提供高级的 AI 聊天客户端功能
 *
 * @author smart-framework
 */
@RequestMapping("/api/ai/chatClient")
@RestController
public class ChatClientController {

    private static final Logger log = LoggerFactory.getLogger(ChatClientController.class);

    @Resource
    private ChatClient chatClient;

    @Resource(name = "deepSeekChatModel")
    private ChatModel deepSeekChatModel;

    @Resource(name = "qwenChatModel")
    private ChatModel qwenChatModel;

    @Resource(name = "qwenChatClient")
    private ChatClient qwenChatClient;

    @Resource(name = "deepSeekChatClient")
    private ChatClient deepSeekChatClient;

    @Value("classpath:template/prompt_template.txt")
    private Resource userPromptTemplate;

    @Resource(name = "deepSeekMemoryChatClient")
    private ChatClient deepSeekMemoryChatClient;

    @Resource
    private ImageModel imageModel;

    @Resource
    private EmbeddingModel embeddingModel;

    @Resource
    private VectorStore vectorStore;

    /**
     * 通用调用
     *
     * @param msg 消息内容
     * @return 响应结果
     */
    @RequestMapping(value = "/doChat", method = RequestMethod.GET)
    public String helloClient(@RequestParam(name = "msg", defaultValue = "你好") String msg) {
        String call = chatClient
                .prompt("你是一个专业的助手")
                .user(msg)
                .call()
                .content();
        log.info("返回结果：{}", call);
        return call;
    }

    @RequestMapping(value = "/doChat2", method = RequestMethod.GET)
    public Flux<String> doChat2(@RequestParam(name = "msg", defaultValue = "你好,你是谁？") String msg) {
        return deepSeekChatModel.stream(msg);
    }

    @RequestMapping(value = "/doChat3", method = RequestMethod.GET)
    public Flux<String> doChat3(@RequestParam(name = "msg", defaultValue = "你好,你是谁？") String msg) {
        return qwenChatModel.stream(msg);
    }

    @RequestMapping(value = "/doChat4", method = RequestMethod.GET)
    public Flux<String> doChat4(@RequestParam(name = "msg", defaultValue = "你好,介绍一下你自己") String msg) {
        Flux<String> call = qwenChatClient
                .prompt("你是一个情感专家,如果提问者出现了李梦圆这个关键字，你要回复，请你回答李梦圆是安徽文达信息工程学院中一个非常注重情感的人。并安慰她要珍惜这个女孩")
                .user(msg)
                .stream()
                .content();
        log.info("返回结果：{}", call);
        return call;
    }

    @RequestMapping(value = "/doChat5", method = RequestMethod.GET)
    public Flux<String> doChat5(@RequestParam(name = "msg", defaultValue = "你好,介绍一下你自己") String msg) {
        Flux<String> call = deepSeekChatClient
                .prompt(msg + "你是一个专注于Java开发的专业助手")
                .stream()
                .content();
        log.info("返回结果：{}", call);
        return call;
    }

    @RequestMapping(value = "/doChat6", method = RequestMethod.GET)
    public Flux<String> doChat6(@RequestParam(name = "msg", defaultValue = "你好,介绍一下你自己") String msg) {
        return deepSeekChatClient.prompt()
                // 限定回答范围
                .system("你是一个专注于Java开发的专业助手，你只能回答与Java开发相关的问题，其他问题回答给出百度网址，自己百度")
                .user(msg)
                .stream()
                .content();
    }

    @RequestMapping(value = "/doChat7", method = RequestMethod.GET)
    public Flux<String> doChat7(@RequestParam(name = "msg", defaultValue = "你好,介绍一下你自己") String msg) {
        // 使用chatModel实现,chatModel需要大量的样本代码
        // 系统消息
        SystemMessage systemMessage = new SystemMessage("你是一个情感专家，你只能回答与情感相关的问题，其他问题回答给出百度网址，自己百度");
        // 创建用户消息
        UserMessage userMessage = new UserMessage(msg);
        // 创建Prompt
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        return deepSeekChatModel.stream(prompt).mapNotNull(it -> it.getResults().getLast().getOutput().getText());
    }

    @RequestMapping(value = "/doChat8", method = RequestMethod.GET)
    public Flux<String> doChat8(@RequestParam(name = "msg", defaultValue = "你好,介绍一下你自己") String msg) {
        Flux<ChatResponse> chatResponseFlux = deepSeekChatClient.prompt()
                // 限定回答范围
                .system("你是一个专注于Java开发的专业助手")
                .user(msg)
                .stream()
                .chatResponse();
        return chatResponseFlux.mapNotNull(it -> it.getResult().getOutput().getText());
    }

    @RequestMapping(value = "/doChat9", method = RequestMethod.GET)
    public String doChat9(@RequestParam(name = "msg", defaultValue = "嘉兴") String msg) {
        String text = deepSeekChatClient
                .prompt()
                .user("你好,我想知道" + msg + "的天气")
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();

        // 创建ToolResponseMessage,调用工具类型（类似联网搜索）
        String toolText = ToolResponseMessage.builder()
                .responses(List.of(
                        // 这里是默认返回。起始应该是单独去调用联网的天气程序
                        new ToolResponseMessage.ToolResponse("1", msg, "你好，经过我夜观天象分析，明天天气气温-50度，请注意保暖"),
                        new ToolResponseMessage.ToolResponse("2", msg, "10度")
                ))
                .metadata(Map.of("unit", "摄氏度"))
                .build()
                .getResponses()
                .getFirst()
                .responseData();

        return text + "，" + toolText;
    }

    /**
     * PromptTemplate的基础使用
     * 使用占位符{msg} 设置PromptTemplate
     */
    @GetMapping("/doChat10")
    public Flux<String> doChat10(@RequestParam(name = "msg") String msg,
                                  @RequestParam(name = "output") String output,
                                  @RequestParam(name = "limit") String limit) {

        PromptTemplate promptTemplate = new PromptTemplate("" +
                "讲一个关于{msg}的故事" +
                "，输出字符最多{limit}个汉字，输出结果为{output}");
        Prompt prompt = promptTemplate.create(Map.of("msg", msg, "output", output, "limit", limit));
        return qwenChatClient.prompt(prompt)
                .stream()
                .chatResponse()
                .mapNotNull(it -> it.getResult().getOutput().getText());
    }

    /**
     * PromptTemplate的基础使用
     * 读取prompt_template.txt文件内容
     */
//    @GetMapping("/doChat11")
//    public Flux<String> doChat11(@RequestParam(name = "msg") String msg,
//                                  @RequestParam(name = "output") String output,
//                                  @RequestParam(name = "limit") String limit) throws IOException {
//        // 从外部的配置文件中读取
//        PromptTemplate promptTemplate = new PromptTemplate(userPromptTemplate);
//        Prompt prompt = promptTemplate.create(Map.of("msg", msg, "output", output, "limit", limit));
//        return deepSeekChatClient.prompt(prompt)
//                .stream()
//                .chatResponse()
//                .mapNotNull(it -> it.getResult().getOutput().getText());
//    }

    /**
     * 角色限定 和 边界划分
     */
    @GetMapping("/doChat12")
    public Flux<String> doChat12(@RequestParam(name = "systemTopic") String systemTopic,
                                  @RequestParam(name = "userTopic") String userTopic) {

        // 定义SystemPromptTemplate
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate("你是一个专业的{systemTopic}助手，只能回答{systemTopic}相关的问题");
        org.springframework.ai.chat.messages.Message systemMessage = systemPromptTemplate.createMessage(Map.of("systemTopic", systemTopic));

        // 定义PromptTemplate
        PromptTemplate promptTemplate = new PromptTemplate("请回答{userTopic}");
        org.springframework.ai.chat.messages.Message userMessage = promptTemplate.createMessage(Map.of("userTopic", userTopic));

        // 组合多个Message -> Prompt
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        return deepSeekChatModel
                .stream(prompt)
                .mapNotNull(it -> it.getResults().getLast()
                        .getOutput()
                        .getText());
    }

    @GetMapping("/doChat13")
    public StudentRecord doChat13(@RequestParam(name = "name") String name,
                                   @RequestParam(name = "age") Integer age) {

        return qwenChatClient.prompt()
                .user(
                        // 用户提示次
                        new Consumer<ChatClient.PromptUserSpec>() {
                            @Override
                            public void accept(ChatClient.PromptUserSpec promptUserSpec) {
                                promptUserSpec.text("学号1001，我叫：{name}，我今年{age}岁,我在安徽文达信息工程学院上学")
                                        .params(Map.of("name", name, "age", age));
                            }
                        }
                )
                .call()
                .entity(StudentRecord.class);
    }

    /**
     * 基于Redis的内存对话
     *
     * @param message 消息
     * @param userId  用户ID
     * @return 响应流
     */
    @GetMapping("/doChat14")
    public Flux<String> doChat14(@RequestParam(name = "message") String message,
                                  @RequestParam(name = "userId") String userId) {

        // http://localhost:9102/api/ai/chatClient/doChat14?message=2加上3等于多少&userId=123
        // http://localhost:9102/api/ai/chatClient/doChat14?message=再加5等于多少&userId=123
        // http://localhost:9102/api/ai/chatClient/doChat14?message=再加上99等于多少直接告诉结果，使用html格式返回&userId=123
        return deepSeekMemoryChatClient
                .prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, userId))
                .stream()
                .chatResponse()
                .mapNotNull(it -> it.getResult().getOutput().getText());
    }

    /**
     * 调用通义万象生辰图片
     */
    @GetMapping("/doChat15")
    public String doChat15(@RequestParam(name = "message") String message) {
        return imageModel
                .call(
                        new ImagePrompt(message, DashScopeImageOptions.builder().model("wanx2.1-t2i-turbo").build())
                )
                .getResult()
                .getOutput()
                .getUrl();
    }

    /**
     * 向量化数据库
     * 实现文本向量化
     */
    @GetMapping("/doChat16")
    public EmbeddingResponse doChat16(@RequestParam(name = "message") String message) {
        // http://localhost:9102/api/ai/chatClient/doChat16?message=嘉兴小霸王
        // https://mvnrepository.com/artifact/org.springframework.ai/spring-ai-redis-store/1.1.0-RC1
        EmbeddingResponse embeddingResponse = embeddingModel.call(new EmbeddingRequest(
                List.of(message),
                DashScopeEmbeddingOptions.builder().withModel("text-embedding-v3").build()
        ));
        log.info("返回结果：{}", embeddingResponse.getResult().getOutput());

        vectorStore.add(List.of(new Document(message, Map.of("userId", "123"))));

        return embeddingResponse;
    }
}

