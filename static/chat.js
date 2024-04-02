var socket = new WebSocket('ws://localhost:8887');
var chatMessages = document.getElementById('chat-messages');
var messageInput = document.getElementById('message-input');

socket.onopen = function(event) {
    console.log("Connected to server");
};

socket.onmessage = function(event) {
    var message = event.data;
    chatMessages.innerHTML += '<p>' + message + '</p>';
};

function sendMessage() {
    var userMessage = messageInput.value;
    if (userMessage.trim() !== '') {
        if (socket.readyState === WebSocket.OPEN) {
            socket.send(userMessage);
            messageInput.value = '';
        } else {
            console.error('WebSocket is not open. Unable to send message.');
        }
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