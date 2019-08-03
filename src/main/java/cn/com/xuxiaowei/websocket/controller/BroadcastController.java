package cn.com.xuxiaowei.websocket.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

/**
 * 广播式 Controller
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Controller
public class BroadcastController {

    /**
     * 广播式 WebSocket 页面
     */
    @RequestMapping("/broadcast")
    public String broadcast(HttpServletRequest request, HttpServletResponse response, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();

        model.addAttribute("name", name);

        return "broadcast";
    }

    /**
     * 广播式 WebSocket 接收与相应
     * <p>
     * {@link MessageMapping}：当浏览器向服务端发送消息时，
     * 通过{@link MessageMapping}映射到 /hello 这个地址，类似于{@link RequestMapping}。
     * <p>
     * {@link SendTo}：当服务端有消息时，会对订阅了{@link SendTo}中的浏览器发送消息。
     *
     * @param message 广播式 WebSocket 接收消息
     * @return 广播式 WebSocket 相应消息
     */
    @MessageMapping("/welcome")
    @SendTo("/topic/broadcast")
    public WelcomeMessage broadcast(Principal principal, WelcomeMessage message) {

        // 模拟延迟
        // <code>Thread.sleep(1000);</code>

        String from = "来自（8080端口）用户：" + principal.getName();
        String msg = HtmlUtils.htmlEscape(message.getMsg());

        // 将特殊字符转换为HTML字符引用。
        return new WelcomeMessage(from, msg);
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
