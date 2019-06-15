package cn.com.xuxiaowei.websocket.controller;

import cn.com.xuxiaowei.websocket.util.Greeting;
import cn.com.xuxiaowei.websocket.util.HelloMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

/**
 * 欢迎 Controller
 *
 * @author xuxiaowei
 */
@Controller
public class GreetingController {

    /**
     * 接收用户发送的 WebSocket 并响应
     *
     * @param message 用户发送的 用户接收 WebSocket
     * @return 用户接收 WebSocket 数据
     */
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) {

        // 模拟延迟
        // <code>Thread.sleep(1000);</code>

        return new Greeting("你好（8080端口）, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

}
