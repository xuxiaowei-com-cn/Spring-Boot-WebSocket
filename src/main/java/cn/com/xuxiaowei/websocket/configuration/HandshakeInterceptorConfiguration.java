package cn.com.xuxiaowei.websocket.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

/**
 * 用于 WebSocket 握手请求的拦截器。
 * 可用于检查握手请求和响应，以及将属性传递给目标{@link WebSocketHandler}。
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
public class HandshakeInterceptorConfiguration implements HandshakeInterceptor {

    /**
     * 用户名
     */
    static final String WEB_SOCKET_USER_NAME = "WEB_SOCKET_USER_NAME";

    /**
     * WebSocket URL
     */
    static final String WEB_SOCKET_URL = "WEB_SOCKET_URL";

    /**
     * 聊天室标识
     */
    static final String CHAT_ROOM = "chatRoom";

    /**
     * @param attributes 用于在{@link WebSocketHandler}中的{@link WebSocketSession#getAttributes()}中获取
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        attributes.put(WEB_SOCKET_USER_NAME, name);

        URI uri = request.getURI();
        attributes.put(WEB_SOCKET_URL, uri);

        String path = uri.getPath();
        String[] pathSplit = path.split("/");
        if (pathSplit.length > 1) {
            String pathSplit1 = pathSplit[1];
            if (CHAT_ROOM.equals(pathSplit1)) {
                String pathSplit2 = pathSplit[2];
                attributes.put(CHAT_ROOM, pathSplit2);
            }
        }

        log.debug("");
        log.debug("用户：" + name + " 建立连接");
        log.debug("");

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception exception) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();

        log.debug("");
        log.info("用户：" + name + " 握手后");
        log.debug("");

    }

}
