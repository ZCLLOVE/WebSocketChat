package main.java;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.*;

class MyWebSocketServer extends WebSocketServer {
    private static Integer result;
    private static String question;
    private static Integer next;
    private Set<WebSocket> connections = new HashSet<>();
    public MyWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        connections.add(conn);
        System.out.println("New connection from " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        connections.remove(conn);
        System.out.println("Connection closed: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        if (message.equals("开始")){
            result = null;
            question = getFiveElementArray();
            broadcast("题目"+question);
            return;
        }
        System.out.println("Received message from " + conn.getRemoteSocketAddress() + ": " + message);
        String nextRes = message.split(";")[0];
        String check = message.split(";")[1];
        String name = message.split(";")[2];
        if (next.equals(Integer.parseInt(check))){
            conn.setAttachment(name+"正确");
        }else {
            conn.setAttachment(name+"错误");
        }
        next = Integer.parseInt(nextRes);
        // 广播消息给所有连接
        //broadcast(conn.getAttachment().toString());
        question = getFiveElementArray();
        broadcast("题目"+question+"用户"+conn.getAttachment().toString());

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
    public synchronized   String getFiveElementArray() {
        int[] orderedList = {0,1,2,3,4,5,6,7,8,9};
        // 创建一个列表来存放选择过的元素
        List<Integer> selectedElements = new ArrayList<>();
        // 创建随机数生成器
        // 循环直到选择了5个不重复的元素
        while (selectedElements.size() < 5) {
            // 生成一个随机索引
            int randomIndex = (int) (Math.random() * orderedList.length);
            // 检查该索引对应的元素是否已经被选择过
            if (!selectedElements.contains(orderedList[randomIndex])) {
                // 如果没有被选择过，则添加到列表中
                selectedElements.add(orderedList[randomIndex]);
            }
        }
        if (result!=null){
            if (!selectedElements.contains(result)){
                selectedElements.add((int) (Math.random() * 4)+1,result);
            }
            if (selectedElements.get(0)==result){
                int r = (int)(Math.random() * 4)+1;
                int temp =selectedElements.get(r);
                selectedElements.remove(0);
                selectedElements.add(0,temp);
                selectedElements.remove(r);
                selectedElements.add(r,result);
            }
            result = selectedElements.get(0);
        }else {
            result = selectedElements.get( (int) (Math.random() * 4)+1);
            next = result;
        }
        return result+""+selectedElements.get(1)+selectedElements.get(2)+selectedElements.get(3)+selectedElements.get(4);
    }
    public static void main(String[] args) {
        int port = 80; // 使用与前端WebSocket相同的端口
        MyWebSocketServer server = new MyWebSocketServer(port);
        server.start();
    }
}
