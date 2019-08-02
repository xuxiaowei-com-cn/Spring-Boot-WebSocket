package cn.com.xuxiaowei.websocket.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户接收 WebSocket
 *
 * @author xuxiaowei
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelloMessage {

    private String name;

}
