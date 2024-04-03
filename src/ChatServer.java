import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

class MyWebSocketServer extends WebSocketServer {

    private Set<WebSocket> connections = new HashSet<>();

    public MyWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        connections.add(conn);
        System.out.println("New connection from " + conn.getRemoteSocketAddress());
        // 发送欢迎消息或其他初始化消息
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        connections.remove(conn);
        System.out.println("Connection closed: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Received message from " + conn.getRemoteSocketAddress() + ": " + message);
        // 广播消息给所有连接
        broadcast(message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null) {
            System.err.println("Error for " + conn.getRemoteSocketAddress());
        }
    }

    @Override
    public void onStart() {
        System.out.println("Server started");
    }

    public void broadcast(String text) {
        for (WebSocket conn : connections) {
            if (conn.isOpen()) {
                conn.send(text);
            }
        }
    }

    public static void main(String[] args) {
        int port = 8887; // 使用与前端WebSocket相同的端口
        MyWebSocketServer server = new MyWebSocketServer(port);
        server.start();
    }
}