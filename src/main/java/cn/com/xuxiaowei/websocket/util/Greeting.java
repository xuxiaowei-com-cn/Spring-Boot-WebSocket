package cn.com.xuxiaowei.websocket.util;

/**
 * 用于响应 WebSocket
 *
 * @author xuxiaowei
 */
public class Greeting {

    private String content;

    public Greeting() {
    }

    public Greeting(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

}
