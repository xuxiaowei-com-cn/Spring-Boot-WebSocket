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

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.util.Map;

/**
 * WebSocket 消息和生命周期事件的处理程序。
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
public class WebSocketHandlerDecoratorConfiguration extends WebSocketHandlerDecorator {

    WebSocketHandlerDecoratorConfiguration(WebSocketHandler delegate) {
        super(delegate);
    }

    /**
     * 在WebSocket协商成功并且WebSocket连接打开并准备好使用后调用。
     *
     * @throws Exception 此方法可以处理或传播异常; 有关详细信息，请参阅类级Javadoc。
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        // 客户端与服务器端建立连接后，此处记录谁上线了
        Map<String, Object> attributes = session.getAttributes();
        String username = attributes.get(HandshakeInterceptorConfiguration.WEB_SOCKET_USER_SESSION_ID).toString();
        System.err.println("上线: " + username);

        super.afterConnectionEstablished(session);
    }

    /**
     * 在新的WebSocket消息到达时调用。
     *
     * @throws Exception 此方法可以处理或传播异常; 有关详细信息，请参阅类级Javadoc。
     */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

        Map<String, Object> attributes = session.getAttributes();
        String username = attributes.get(HandshakeInterceptorConfiguration.WEB_SOCKET_USER_SESSION_ID).toString();
        System.err.println("接收到用户: " + username + " 的消息");
        System.err.println("消息内容：" + message.getPayload().toString());

        super.handleMessage(session, message);
    }

    /**
     * 处理底层WebSocket消息传输中的错误。
     *
     * @throws Exception 此方法可以处理或传播异常; 有关详细信息，请参阅类级Javadoc。
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

        Map<String, Object> attributes = session.getAttributes();
        String username = attributes.get(HandshakeInterceptorConfiguration.WEB_SOCKET_USER_SESSION_ID).toString();
        System.err.println("接收到用户: " + username + " 的异常");
        System.err.println("异常信息：" + exception.getMessage());

        super.handleTransportError(session, exception);
    }

    /**
     * WebSocket连接被任何一方关闭后，或者在发生传输错误后调用。
     * 虽然会话在技术上可能仍然是开放的，但取决于底层实现，此时不鼓励发送消息，并且很可能不会成功。
     *
     * @throws Exception 此方法可以处理或传播异常; 有关详细信息，请参阅类级Javadoc。
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

        // 客户端与服务器端断开连接后，此处记录谁下线了
        Map<String, Object> attributes = session.getAttributes();
        String username = attributes.get(HandshakeInterceptorConfiguration.WEB_SOCKET_USER_SESSION_ID).toString();
        System.err.println("离线: " + username);

        super.afterConnectionClosed(session, closeStatus);
    }

}