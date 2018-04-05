package com.nio.nioserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * @description:(NIO服務端)
 * @author 52762
 * @date 2017年11月3日 下午10:10:10
 * @since JDK 1.6
 */
public class NioServerDemo {

    // 1.初始化参数配置
    private int blockSize = 8192;
    // 发送数据缓冲区大小
    private ByteBuffer sendBuffer = ByteBuffer.allocate(blockSize);
    // 接收数据缓冲区大小
    private ByteBuffer receiveBuffer = ByteBuffer.allocate(blockSize);
    private Selector selector;
    public int flag = 1;

    public NioServerDemo() {

    }

    public NioServerDemo(int port) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 設置為非阻塞
        serverSocketChannel.configureBlocking(false);
        ServerSocket socket = serverSocketChannel.socket();
        // 绑定端口
        socket.bind(new InetSocketAddress(port));
        // 打开选择器
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server start,端口号是" + port);
    }

    // 监听
    public void listen() throws IOException, ClassNotFoundException {
        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = (SelectionKey) iterator.next();
                iterator.remove();
                // 业务逻辑
                handleKey(selectionKey);
            }
        }
    }

    public void handleKey(SelectionKey selectionKey) throws IOException, ClassNotFoundException {
        if (selectionKey == null) {
            System.out.println("SelectionKey is null");
            return;
        }
        ServerSocketChannel server = null;
        SocketChannel client = null;
        String receiveTest = "";
        String sendText = "";
        int count = 0;
        // 是否可以接收客户端连接
        if (selectionKey.isAcceptable()) {
            server = (ServerSocketChannel) selectionKey.channel();
            client = server.accept();
            // 设置客户端为非阻塞
            client.configureBlocking(false);
            // 客戶端數據來了，注冊读事件
            client.register(selector, SelectionKey.OP_READ);
        } else if (selectionKey.isReadable()) {
            // 得到客户端的channel
            client = (SocketChannel) selectionKey.channel();
            // 读取客户端传过来的数据
            count = client.read(receiveBuffer);
            if (count > 0) {
                // 读取客户端字符串
                receiveTest = new String(receiveBuffer.array(), 0, count, "utf-8");
                System.out.println("服务端接收到客户端的信息为" + receiveTest);
            }
            client.register(selector, SelectionKey.OP_WRITE);
        } else if (selectionKey.isWritable()) {
            sendBuffer.clear();
            client = (SocketChannel) selectionKey.channel();
            String handleService = handleService(receiveTest);
            // 将数据写入缓冲区传输到客户端
            // 第一种传输字符串
            sendText = "msg to client" + handleService;
            sendBuffer.put(sendText.getBytes("utf-8"));
            // 反转缓冲区。首先将限制设置为当前位置，然后将位置设置为 0。如果已定义了标记，则丢弃该标记。
            // 常与compact方法一起使用。通常情况下，在准备从缓冲区中读取数据时调用flip方法
            sendBuffer.flip();
            // 发送缓冲区的数据
            client.write(sendBuffer);
            System.out.println("服务端发送完毕");

        }
    }

    // 自定义业务处理

    public String handleService(String clientStr) { // 根据客户端数据，处理业务数据
        switch (clientStr) {
            case "1":
                return "1111111111111111111";
            case "2":
                return "2222222222222222222";
            case "3":
                return "3333333333333333333";
            default:
                return "你妹的";
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int port = 10110;
        NioServerDemo server = new NioServerDemo(port);
        server.listen();
    }
}
