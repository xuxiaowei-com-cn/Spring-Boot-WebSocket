package cn.com.xuxiaowei.websocket.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 用于 WebSocket 握手请求的拦截器。 可用于检查握手请求和响应，以及将属性传递给目标{@link WebSocketHandler}。
 *
 * @author xuxiaowei
 */
@Slf4j
public class HandshakeInterceptorConfig implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

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

            String requestUri = httpServletRequest.getRequestURI();

            attributes.put("requestUri", requestUri);

            log.info("用户访问路径:" + requestUri);
            log.debug("");
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

        log.info("握手后");
        log.debug("");

    }

}
