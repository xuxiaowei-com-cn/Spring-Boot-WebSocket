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
 * 局部广播式（群聊）WebSocket Controller
 *
 * @author xuxiaowei
 */
@Controller
public class ChatRoomController {

    /**
     * 局部广播式（群聊）WebSocket 页面
     */
    @RequestMapping("/chatRoom")
    public String chatRoom(HttpServletRequest request, HttpServletResponse response, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();

        model.addAttribute("name", name);

        return "chatRoom";
    }

    @MessageMapping("/chatRoom/{chatRoomId}")
    @SendTo("/topic/chatRoom/{chatRoomId}")
    public WelcomeMessage broadcast(Principal principal, WelcomeMessage message) {

        String from = principal.getName();
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
