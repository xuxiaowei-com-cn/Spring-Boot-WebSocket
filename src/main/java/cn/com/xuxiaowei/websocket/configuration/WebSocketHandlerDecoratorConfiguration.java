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

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WebSocket 消息和生命周期事件的处理程序。
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
public class WebSocketHandlerDecoratorConfiguration extends WebSocketHandlerDecorator {

    /**
     * 总在线用户
     */
    private static Map<String, WebSocketSession> allUsers;

    /**
     * 群聊在线人数
     * <p>
     * key：群号
     * values：全部群聊成员
     */
    private static Map<String, List<String>> chatRoomUsers;

    static {
        allUsers = new HashMap<>();
        chatRoomUsers = new HashMap<>();
    }

    WebSocketHandlerDecoratorConfiguration(WebSocketHandler delegate) {
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
        Map<String, Object> attributes = session.getAttributes();
        String name = attributes.get(HandshakeInterceptorConfiguration.WEB_SOCKET_USER_NAME).toString();
        System.err.println("上线: " + name);

        allUsers.put(name, session);
    }

    /**
     * 在新的WebSocket消息到达时调用。
     *
     * @throws Exception 此方法可以处理或传播异常; 有关详细信息，请参阅类级Javadoc。
     */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);

        Map<String, Object> attributes = session.getAttributes();
        String name = attributes.get(HandshakeInterceptorConfiguration.WEB_SOCKET_USER_NAME).toString();
        System.err.println("接收到用户: " + name + " 的消息");
        System.err.println("消息内容：" + message.getPayload().toString());
    }

    /**
     * 处理底层WebSocket消息传输中的错误。
     *
     * @throws Exception 此方法可以处理或传播异常; 有关详细信息，请参阅类级Javadoc。
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);

        Map<String, Object> attributes = session.getAttributes();
        String name = attributes.get(HandshakeInterceptorConfiguration.WEB_SOCKET_USER_NAME).toString();
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
        Map<String, Object> attributes = session.getAttributes();
        String name = attributes.get(HandshakeInterceptorConfiguration.WEB_SOCKET_USER_NAME).toString();
        System.err.println("离线: " + name);

        allUsers.remove(name);

        for (Map.Entry<String, List<String>> entry : chatRoomUsers.entrySet()) {


        }
    }

}
