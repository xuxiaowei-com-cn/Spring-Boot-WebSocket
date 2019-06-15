/**
 * Stomp 连接
 */
var stompClient = null; // stomp客户端

/**
 * 连接
 */
function connect() {
    var socket = new SockJS('/gs-guide-websocket'); // "SockJS" 构造函数

    stompClient = Stomp.over(socket); // 过度

    stompClient.debug = false; // 禁用控制台打印你连接时的内容

    stompClient.connect({}, function (frame) { // 连接

        setConnected(true); // 设置已连接

        console.log('Stomp SockJS 已连接');

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
