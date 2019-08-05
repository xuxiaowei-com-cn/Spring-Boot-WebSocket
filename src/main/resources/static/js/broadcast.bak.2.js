/**
 * Stomp 连接
 */
var stompClient = null; // stomp客户端

/**
 * 连接
 */
function connect() {

    // 不谢域名：默认当前域名
    // 写域名：支持跨域（仅在此处写域名即可，其他位置不需要写域名）
    // 连接 SockJS 的 endpoint 名称为 "/toAll"。
    var socket = new SockJS('http://127.0.0.1:8080/toAll'); // "SockJS" 构造函数

    // 使用 STOMP 子协议的 WebSocket 客户端
    stompClient = Stomp.over(socket);

    stompClient.debug = false; // 禁用控制台打印你连接时的内容

    // 连接 WebSocket 服务端
    stompClient.connect({}, function (frame) {

        setConnected(true); // 设置已连接
        console.log('Stomp SockJS 已连接');

        // 通过 stompClient.subscribe 订阅 '/topic/broadcast' 目标（destination）发送的消息，
        // 这个是在控制器的 @SendTo 中定义的。
        // 不需要写域名（域名同上）
        // 订阅来自 @SendTo() 的消息
        stompClient.subscribe('/topic/broadcast', function (message) {
            console.log("订阅消息：", message);
            var body = JSON.parse(message.body);
            showMessage(body.from, body.msg);
        });

        // 服务端 WebSocketHandler 发送的消息
        socket.onmessage = function (e) {
            console.log("onmessage消息：", e);
            try {
                var data = JSON.parse(e.data);
                var type = data.type;
                var number = data.number;
                if (type === "onlineMsg") {
                    var username = data.username;
                    var online = data.online;
                    showOnline(online, username, number);
                } else if (type === "onlineNum") {
                    var users = data.users;
                    console.log("在线用户：" + users)
                }
            } catch (e) {

            }
        };

    }, function (err) { // 连接异常
        console.log("连接异常：", err);
    });
}

/**
 * 显示问候语
 */
function showMessage(from, msg) {

    var message = "<div class=\"form-group\">" +
        "<label for=\"from\">" + from + "</label>" +
        "<input type=\"text\" class=\"form-control\" value=\"" + msg + "\" readonly>" +
        "</div>";

    $("#message").append(message);
}

/**
 * 显示上线、下线通知
 *
 * @param online 上线、下线
 * @param username 用户名
 * @param number 在线用户数
 */
function showOnline(online, username, number) {

    var strong = online === true ? "用户上线通知" : "用户下线通知";
    var alert = online === true ? "alert-success" : "alert-secondary";
    var id = new Date().getTime();

    var message = "<div class=\"alert " + alert + " alert-dismissible fade show\" role=\"alert\" id=\"" + id + "\">" +
        "<strong>" + strong + "</strong>" +
        "用户 " + username + " 上线" +
        "<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-label=\"Close\">" +
        "<span aria-hidden=\"true\" id=\"onlineHidden-" + id + "\">&times;</span>" +
        "</button>" +
        "</div>";

    $("#online").append(message);

    $("#number").val(number);

    setTimeout(function () {
        $("#onlineHidden-" + id).click();
    }, 3000);
}

/**
 * 设置连接状态
 */
function setConnected(connected) {
    if (connected) {
        $("#isConnect").show();
        $("#connect").hide();
        $("#disconnect").show();
        $("#online-number").show();

    } else {
        $("#isConnect").hide();
        $("#connect").show();
        $("#disconnect").hide();
        $("#online-number").hide();
    }
    // $("#message").html(""); // （不刷新页面、连接、断开时）清理历史记录
}

/**
 * 断开连接
 */
function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }

    // 设置已断开连接
    setConnected(false);
    console.log("Stomp SockJS 已断开连接");
}

/**
 * 广播消息
 */
function sendMessage() {

    if (stompClient == null) {
        return;
    }

    // stompClient.debug = false; // 禁用控制台打印你发送的内容

    // 通过 stompClient.send 向 "/app/welcome" 目标（destination）发送消息，
    // 这是在控制器的 @MessageMapping 中定义的。
    // 不需要写域名（域名同上）
    // 填写 @MessageMapping()
    // 如果在 WebSocketMessageBrokerConfigurer#configureMessageBroker(MessageBrokerRegistry) 实现类中
    // 配置了 registry.setApplicationDestinationPrefixes()，需要加上在加上已配置的前缀
    stompClient.send("/app/welcome", {}, JSON.stringify({
        'msg': $("#msg").val()
    }));

    console.log("Stomp SockJS 已发送 - " + new Date().toLocaleTimeString())
}

$(function () {

    // 连接
    $("#connect").on("click", function () {
        connect();
    });

    // 断开连接
    $("#disconnect").on("click", function () {
        disconnect();
    });

    // 发送
    $("#send").on("click", function () {
        sendMessage();
    });

});
