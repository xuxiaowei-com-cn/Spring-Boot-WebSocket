/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.com.xuxiaowei.websocket.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
 * 点对点式（好友聊天）Controller
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Controller
public class FriendController {

    /**
     * 向浏览器发送消息
     */
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public FriendController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @RequestMapping("/friend")
    public String friend(HttpServletRequest request, HttpServletResponse response, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();

        model.addAttribute("name", name);

        return "friend";
    }

    /**
     * @param principal 在 Spring MVC 中，principal 包含当前用户信息
     */
    @MessageMapping("/toFriend")
    @SendTo("/queue/friend")
    public void friend(Principal principal, FriendMessage message) {

        // 设置发送者，该值默认为空，
        // 发送者不信任前台传值
        message.setFrom(principal.getName());

        // 接收者
        String to = message.getTo();

        // 消息处理
        message.setMsg(HtmlUtils.htmlEscape(message.getMsg()));

        // 第一个参数：接收消息的用户
        // 第二个参数：浏览器订阅的地址
        // 第三个参数：消息本身
        messagingTemplate.convertAndSendToUser(to, "/queue/friend", message);
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
    private static class FriendMessage {

        /**
         * 消息发送者
         */
        private String from;

        /**
         * 消息接收者
         */
        private String to;

        /**
         * 消息内容
         */
        private String msg;

    }

}
