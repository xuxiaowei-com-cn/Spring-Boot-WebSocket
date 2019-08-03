package cn.com.xuxiaowei.websocket.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 用于 WebSocket 握手请求的拦截器。 可用于检查握手请求和响应，以及将属性传递给目标{@link WebSocketHandler}。
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
public class HandshakeInterceptorConfiguration implements HandshakeInterceptor {

    /**
     * 用户 SessionId
     * <p>
     * 用户标识，这里充当登录用户的主键
     */
    static final String WEB_SOCKET_USER_SESSION_ID = "WEB_SOCKET_USER_SESSION_ID";

    /**
     * @param attributes 用于在{@link WebSocketHandler}中的{@link WebSocketSession#getAttributes()}中获取
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        log.debug("");
        log.debug("用户建立连接");
        log.debug("");

        if (request instanceof ServletServerHttpRequest) {

            ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpServletRequest = servletServerHttpRequest.getServletRequest();
            HttpSession session = httpServletRequest.getSession();
            String id = session.getId();

            log.info("用户 Session ID:" + id);
            log.debug("");

            // 放入用户 SessionId
            attributes.put(WEB_SOCKET_USER_SESSION_ID, id);
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception exception) {

        log.info("握手后");
        log.debug("");

    }

}
