package cn.com.xuxiaowei.websocket.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 用于响应 WebSocket
 *
 * @author xuxiaowei
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Greeting {

    private String content;

}
