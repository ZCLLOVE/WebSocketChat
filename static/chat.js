var socket = new WebSocket('ws://localhost:80');
var chatMessages = document.getElementById('chat-messages');
var messageInput = document.getElementById('message-input');

socket.onopen = function(event) {
    console.log("Connected to server");
};

socket.onmessage = function(event) {
    var message = event.data;
    if (message.includes("题目")){
        var regex = /\d/g; // 匹配任何数字
        var matches = message.match(regex);
        var randomNumbers = matches.map(Number);
        // 遍历每个 div，并设置背景图片
        for (var i = 0; i < 5; i++) {
            // 使用原生 JavaScript 获取 DOM 元素
            var divElement = document.getElementById(i+"a");
            divElement.style.backgroundImage = 'url("img/' + randomNumbers[i] + '.jpg")';
            divElement.setAttribute("name",randomNumbers[i])
        }
    }
    if (message.includes("用户")){
        showOverlayAndCountdown(message.slice(-5))
    }
    if ((message.includes("一")&&message.includes("正确"))||(message.includes("二")&&message.includes("错误"))){
        var span = document.getElementById("code1")
        span.innerHTML=parseInt(span.innerHTML)+1
    }
    if ((message.includes("一")&&message.includes("错误"))||(message.includes("二")&&message.includes("正确"))){
        var span = document.getElementById("code2")
        span.innerHTML=parseInt(span.innerHTML)+1
    }
   // chatMessages.innerHTML += '<p>' + message + '</p>';

};

function sendMessage() {
    socket.send('开始');
}
function sendResult(id) {
    var div = document.getElementById('0a');
    var result = div.getAttribute("name");
    var clickdiv = document.getElementById(id)
    var chooes = clickdiv.getAttribute("name");
    var span = document.getElementById("user")
    var user = span.innerHTML
    if (socket.readyState === WebSocket.OPEN) {
        socket.send(result+";"+chooes+";"+user);
    } else {
        console.error('WebSocket is not open. Unable to send message.');
    }
}
socket.onerror = function(error) {
    console.error('WebSocket Error: ', error);
};

socket.onclose = function(event) {
    if (event.wasClean) {
        console.log('Connection closed cleanly, code=' + event.code + ' reason=' + event.reason);
    } else {
        console.error('Connection died');
    }
};


function getFiveElementArray() {
    // 创建一个数组，包含可能的数字0到7
    var orderedArray = [0,1, 2, 3, 4, 5, 6, 7];
    var randomArray = [];

    while (randomArray.length < 6) {
        var randomIndex = Math.floor(Math.random() * orderedArray.length);
        var element = orderedArray[randomIndex];
        if (randomArray.indexOf(element) === -1) {
            randomArray.push(element);
            orderedArray.splice(randomIndex, 1); // 从原始数组中移除已选取的元素
        }
    }
    randomArray[0] = randomArray[Math.floor(Math.random() * 4)+1]

    return randomArray;
}
// 显示遮罩层并开始倒计时的函数
function showOverlayAndCountdown(match) {
    var countdownText = document.getElementById('countdown-text');
    countdownText.textContent = match;

    var seconds = 1;
    // 显示遮罩层
    var overlay = document.getElementById('overlay');
    overlay.style.display = 'block';

    // 倒计时函数
    var countdown = setInterval(function() {
        seconds--;
        if (seconds <= 0) {
            // 倒计时结束，隐藏遮罩层
            overlay.style.display = 'none';
            clearInterval(countdown); // 清除定时器
        }
    }, 1000);

}
function getQueryParam(paramName) {
    // 获取当前页面的URL
    var url = new URL(window.location.href);

    // 使用URLSearchParams接口获取参数值
    var params = new URLSearchParams(url.search);
    var paramValue = params.get(paramName);

    return paramValue;
}