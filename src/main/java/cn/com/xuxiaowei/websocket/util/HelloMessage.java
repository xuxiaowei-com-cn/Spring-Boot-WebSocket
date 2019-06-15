package cn.com.xuxiaowei.websocket.util;

/**
 * 用户接收 WebSocket
 *
 * @author xuxiaowei
 */
public class HelloMessage {

    private String name;

    public HelloMessage() {
    }

    public HelloMessage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
