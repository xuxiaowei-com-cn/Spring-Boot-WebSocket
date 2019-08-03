package cn.com.xuxiaowei.websocket.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 广播式 Controller
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Controller
public class BroadcastController {

    /**
     * 广播式 页面
     */
    @RequestMapping("/broadcast")
    public String broadcast(HttpServletRequest request, HttpServletResponse response, Model model) {

        model.addAttribute("from", request.getSession().getId());

        return "broadcast";
    }

    /**
     * 接收用户发送的 WebSocket 并响应
     * <p>
     * 广播式
     * <p>
     * {@link MessageMapping}：当浏览器向服务端发送消息时，
     * 通过{@link MessageMapping}映射到 /hello 这个地址，类似于{@link RequestMapping}。
     * <p>
     * {@link SendTo}：当服务端有消息时，会对订阅了{@link SendTo}中的浏览器发送消息。
     *
     * @param message 用户发送的 用户接收 WebSocket
     * @return 用户接收 WebSocket 数据
     */
    @MessageMapping("/welcome")
    @SendTo("/topic/broadcast")
    public WelcomeMessage broadcast(WelcomeMessage message) {

        // 模拟延迟
        // <code>Thread.sleep(1000);</code>

        String msg = message.getMsg();

        msg = ("".equals(msg) || msg == null) ? "这个人很懒，什么都没写" : msg;

        // 将特殊字符转换为HTML字符引用。
        return new WelcomeMessage("来自（8080端口）用户：" + message.getFrom(), HtmlUtils.htmlEscape(msg));
    }

    /**
     * 广播消息
     *
     * @author xuxiaowei
     * @since 0.0.1
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class WelcomeMessage {

        /**
         * 消息发送者
         */
        private String from;

        /**
         * 消息内容
         */
        private String msg;

    }

}
