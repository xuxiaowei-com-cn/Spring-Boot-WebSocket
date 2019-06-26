package cn.com.xuxiaowei.websocket.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 用户接收 WebSocket
 *
 * @author xuxiaowei
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HelloMessage {

    private String name;

}
