package com.nio.nioclient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioClientDemo {

    // 1.初始化参数配置
    private static final int blockSize = 8192;
    // 发送数据缓冲区大小
    private static ByteBuffer sendBuffer = ByteBuffer.allocate(blockSize);
    // 接收数据缓冲区大小
    private static ByteBuffer receiveBuffer = ByteBuffer.allocate(blockSize);
    // 封装一个IP和端口
    private final static InetSocketAddress serverAddress = new InetSocketAddress("localhost", 10110);

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        /* 创建一个SocketChannel */
        SocketChannel client = SocketChannel.open();
        /* 设为非阻塞模式 */
        client.configureBlocking(Boolean.FALSE);
        /* 创建Selector */
        Selector selector = Selector.open();
        /* 注册连接服务端socket动作 */
        client.register(selector, SelectionKey.OP_CONNECT);
        // 鏈接服務器
        boolean connect = client.connect(serverAddress);
        // System.out.println(connect);
        Set<SelectionKey> selectedKeys = null;
        Iterator<SelectionKey> iterator = null;
        SelectionKey selectionKey = null;
        String receiveTest;
        String sendText;
        int count = 0;
        while (true) {
            /* 监控注册在Selector上的SocketChannel，返回值代表有多少channel已经就绪，可以进行I/O操作了 */
            int ready = selector.select();// 重点
            // System.out.println(ready);
            selectedKeys = selector.selectedKeys();
            iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {
                selectionKey = iterator.next();
                if (selectionKey.isConnectable()) {
                    System.out.println("client connet");
                    client = (SocketChannel) selectionKey.channel();
                    if (client.isConnectionPending()) {
                        client.finishConnect();
                        System.out.println("finish connect");
                        sendBuffer.clear();
                        sendBuffer.put("hello Server".getBytes("utf-8"));
                        sendBuffer.flip();
                        client.write(sendBuffer);
                        client.register(selector, SelectionKey.OP_READ);
                    }
                } else if (selectionKey.isReadable()) {
                    client = (SocketChannel) selectionKey.channel();
                    receiveBuffer.clear();
                    // 读取服务端传输到客户端的数据
                    count = client.read(receiveBuffer);
                    if (count > 0) {
                        // 第一种方式传输的是字符串
                        receiveTest = new String(receiveBuffer.array(), 0, count, "utf-8");
                        System.out.println("server's msg is" + receiveTest);
                        client.register(selector, SelectionKey.OP_WRITE);
                    }

                } else if (selectionKey.isWritable()) {
                    sendBuffer.clear();
                    client = (SocketChannel) selectionKey.channel();
                    // 第一种传输字符串
                    sendText = "Msg send to Server";
                    sendBuffer.put(sendText.getBytes("utf-8"));
                    sendBuffer.flip();
                    // 將数据传输到服务端
                    client.write(sendBuffer);
                    System.out.println("finish send to msg");
                    client.register(selector, SelectionKey.OP_READ);
                }

            }
            selectedKeys.clear();
        }
    }

}
