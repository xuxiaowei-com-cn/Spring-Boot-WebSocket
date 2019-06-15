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
    var socket = new SockJS('http://127.0.0.1:8080/gs-guide-websocket'); // "SockJS" 构造函数

    stompClient = Stomp.over(socket); // 过度

    stompClient.debug = false; // 禁用控制台打印你连接时的内容

    stompClient.connect({}, function (frame) { // 连接

        setConnected(true); // 设置已连接

        console.log('Stomp SockJS 已连接');

        // 不需要写域名（域名同上）
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
    });
}

/**
 * 显示问候语
 */
function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
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
    $("#greetings").html("");
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
 * 发送名字
 */
function sendName() {

    if (stompClient == null) {
        return;
    }

    stompClient.debug = false; // 禁用控制台打印你发送的内容

    // 不需要写域名（域名同上）
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));

    console.log("Stomp SockJS 已发送 - " + new Date().toLocaleTimeString())
}

$(function () {

    // 发送
    // 连接时
    $("form").on('submit', function (e) {
        e.preventDefault();
    });

    // 连接
    $("#connect").click(function () {
        connect();
    });

    // 断开连接
    $("#disconnect").click(function () {
        disconnect();
    });

    // 发送
    $("#send").click(function () {
        sendName();
    });
});
