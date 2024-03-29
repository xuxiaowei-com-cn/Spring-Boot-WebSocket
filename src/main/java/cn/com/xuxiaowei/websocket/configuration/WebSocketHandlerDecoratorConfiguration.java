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
package cn.com.xuxiaowei.websocket.configuration;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 消息和生命周期事件的处理程序。
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
public class WebSocketHandlerDecoratorConfiguration extends WebSocketHandlerDecorator {

    /**
     * 总在线用户
     */
    private static final Map<String, WebSocketSession> ALL_WEB_SOCKET_SESSION = new ConcurrentHashMap<>();

    private SimpMessagingTemplate messagingTemplate;

    public void setApplicationContext(ApplicationContext applicationContext) {
        messagingTemplate = applicationContext.getBean(SimpMessagingTemplate.class);

        log.info("使用 ApplicationContext 获取 SimpMessagingTemplate：{}", messagingTemplate);
    }

    public WebSocketHandlerDecoratorConfiguration(WebSocketHandler delegate) {
        super(delegate);
    }

    /**
     * 在WebSocket协商成功并且WebSocket连接打开并准备好使用后调用。
     *
     * @param session 发送WebSocket消息：{@link TextMessage}或{@link BinaryMessage}。
     *                <p><strong>注意：</strong>底层标准WebSocket会话（JSR-356）不允许并发发送。
     *                因此必须同步发送。
     *                为了确保这一点，一个选项是使用{@link ConcurrentWebSocketSessionDecorator}包装{@link WebSocketSession}。
     * @throws Exception 此方法可以处理或传播异常; 有关详细信息，请参阅类级Javadoc。
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        // 客户端与服务器端建立连接后，此处记录谁上线了
        Principal principal = session.getPrincipal();
        assert principal != null;
        String name = principal.getName();
        System.err.println("上线: " + name);

        ALL_WEB_SOCKET_SESSION.put(name, session);

        // 用户上线通知
        // 放在添加用户后面
        onlineMsg(name, true);

        // 在线用户
        // 放在添加用户后面
        onlineNum(name);
    }

    /**
     * 用户上线/下线通知
     * <p>
     * 放在添加用户后面
     *
     * @param name   上线用户名
     * @param online true 上线，false 下线
     */
    private void onlineMsg(String name, boolean online) {
        Map<String, Object> map = new HashMap<>(4);

        map.put("type", "onlineMsg");
        map.put("online", online);
        map.put("username", name);
        map.put("number", ALL_WEB_SOCKET_SESSION.size());
        String payload = JSONObject.toJSONString(map);

        // 发送上线下通知（方式一）
//        messagingTemplate.convertAndSend("/topic/broadcast", payload);

        for (Map.Entry<String, WebSocketSession> entry : ALL_WEB_SOCKET_SESSION.entrySet()) {
            String key = entry.getKey();
            if (!name.equals(key)) {
                WebSocketSession value = entry.getValue();

                // 发送上线下通知（方式二）
                messagingTemplate.convertAndSend("/topic/broadcast", payload,
                        Collections.singletonMap(SimpMessageHeaderAccessor.SESSION_ID_HEADER, value.getId()));

                // 发送消息：无法解析（方式三）
//                value.sendMessage(new TextMessage(payload, true));
            }
        }
    }

    /**
     * 在线人数
     * <p>
     * 放在添加用户后面
     *
     * @param name 当前用户
     */
    private void onlineNum(String name) throws IOException {
        Set<String> users = ALL_WEB_SOCKET_SESSION.keySet();
        Map<String, Object> map = new HashMap<>(4);
        map.put("type", "onlineNum");
        map.put("number", ALL_WEB_SOCKET_SESSION.size());
        map.put("users", users);
        String payload = JSONObject.toJSONString(map);
        WebSocketSession webSocketSession = ALL_WEB_SOCKET_SESSION.get(name);
        webSocketSession.sendMessage(new TextMessage(payload, true));
    }

    /**
     * 在新的WebSocket消息到达时调用。
     *
     * @throws Exception 此方法可以处理或传播异常; 有关详细信息，请参阅类级Javadoc。
     */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);

        Principal principal = session.getPrincipal();
        assert principal != null;
        String name = principal.getName();
        System.err.println("接收到用户: " + name + " 的消息");
        System.err.println("消息内容：\n" + message.getPayload().toString());
    }

    /**
     * 处理底层WebSocket消息传输中的错误。
     *
     * @throws Exception 此方法可以处理或传播异常; 有关详细信息，请参阅类级Javadoc。
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);

        Principal principal = session.getPrincipal();
        assert principal != null;
        String name = principal.getName();
        System.err.println("接收到用户: " + name + " 的异常");
        System.err.println("异常信息：" + exception.getMessage());
    }

    /**
     * WebSocket连接被任何一方关闭后，或者在发生传输错误后调用。
     * 虽然会话在技术上可能仍然是开放的，但取决于底层实现，此时不鼓励发送消息，并且很可能不会成功。
     *
     * @throws Exception 此方法可以处理或传播异常; 有关详细信息，请参阅类级Javadoc。
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        super.afterConnectionClosed(session, closeStatus);

        // 客户端与服务器端断开连接后，此处记录谁下线了
        Principal principal = session.getPrincipal();
        assert principal != null;
        String name = principal.getName();
        System.err.println("离线: " + name);

        ALL_WEB_SOCKET_SESSION.remove(name);

        // 用户下线通知
        // 放在移除用户后面
        onlineMsg(name, false);
    }

}
