package cn.com.xuxiaowei.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * WebSocket 配置
 *
 * @author xuxiaowei
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketMessageBrokerConfigurerConfig implements WebSocketMessageBrokerConfigurer {

    @Bean
    public HandshakeInterceptor handshakeInterceptor() {
        return new HandshakeInterceptorConfig();
    }

    /**
     * 配置消息代理选项。
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        // 启用简单的消息代理并配置一个或多个前缀以过滤以代理为目标的目标（例如，前缀为“/topic”的目标）。
        config.enableSimpleBroker("/topic");

        // 配置一个或多个前缀以过滤以应用程序注释方法为目标的目标。
        // 例如，前缀为“/app”的目的地可以通过注释方法处理，而其他目的地可以以消息代理为目标（例如“/topic”，“/queue”）。
        // 处理消息时，将从目标中删除匹配前缀以形成查找路径。 这意味着注释不应包含目标前缀。
        // 没有尾部斜杠的前缀将自动附加一个。
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * 注册 STOMP 端点，将每个端点映射到特定URL，并（可选）启用和配置 SockJS 后备选项。
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // 跨域（注意端口与协议）
        List<String> origins = new ArrayList<>(10);

        origins.add("http://127.0.0.1:8080");
        origins.add("http://localhost:8080");

        origins.add("http://127.0.0.1:8081");
        origins.add("http://localhost:8081");

        // 启用 SockJS 后备选项。
        registry.addEndpoint("/gs-guide-websocket")
                .addInterceptors(handshakeInterceptor())
                .setAllowedOrigins(origins.toArray(new String[]{}))
                .withSockJS();

    }

}