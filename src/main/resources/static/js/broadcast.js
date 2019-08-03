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
    // 连接 SockJS 的 endpoint 名称为 "/websocket"。
    var socket = new SockJS('http://127.0.0.1:8080/websocket'); // "SockJS" 构造函数

    // 使用 STOMP 子协议的 WebSocket 客户端
    stompClient = Stomp.over(socket);

    stompClient.debug = false; // 禁用控制台打印你连接时的内容

    // 连接 WebSocket 服务端
    stompClient.connect({}, function (frame) { // 连接

        $("#isConnect").show();
        $("#connect").hide();
        $("#disconnect").show();

        setConnected(true); // 设置已连接

        console.log('Stomp SockJS 已连接');

        // 通过 stompClient.subscribe 订阅 '/topic/broadcast' 目标（destination）发送的消息，
        // 这个是在控制器的 @SendTo 中定义的。
        // 不需要写域名（域名同上）
        // 订阅来自 @SendTo() 的消息
        stompClient.subscribe('/topic/broadcast', function (message) {
            var body = JSON.parse(message.body);
            showMessage(body.from, body.msg);
        });
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
 * 设置已连接
 */
function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#message").html("");
}

/**
 * 断开连接
 */
function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }

    $("#isConnect").hide();
    $("#disconnect").hide();
    $("#connect").show();

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

    // 发送
    // 连接时
    $("form").on('submit', function (e) {
        e.preventDefault();
    });

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
