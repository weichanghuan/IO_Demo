package com.nio.other;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @author 52762
 * @description:(nio服务端)
 * @date 2017年10月23日 下午10:42:07
 * @since JDK 1.6
 */
public class NioServer {
    private Logger logger = LoggerFactory.getLogger(NioServer.class);

    /* 缓冲区大小 */
    private static final int BLOCK_SIZE = 4096;
    /* 接收数据缓冲区大小 */
    private ByteBuffer recieveBuffer = ByteBuffer.allocate(BLOCK_SIZE);
    /* 发送数据缓冲区大小 */
    private ByteBuffer sendBuffer = ByteBuffer.allocate(BLOCK_SIZE);
    /* 持有一个Selector */
    private Selector selector;

    public NioServer() {

    }

    public NioServer(int port) throws IOException {
        /* SocketServerChannel：可以监听新进来的TCP连接的通道, 就像标准IO中的ServerSocket一样 */
        /* 创建服务器套接字通道：SocketServerChannel，内部就是实例化了一个SocketServerChannelImpl */
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        /* 服务器配置为非阻塞 */
        serverSocketChannel.configureBlocking(Boolean.FALSE);
        /* 获取一个socket */
        ServerSocket serverSocket = serverSocketChannel.socket();
        /* 通过socket 绑定地址 */
        serverSocket.bind(new InetSocketAddress("localhost", port));
        /* 获取Selector:实际上window是创建了WindowsSelectorImpl实例 */
        selector = Selector.open();
        /* 注册到accept事件到 selector，等待连接 */
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        logger.info("NIO Server Start.........................");
    }

    /**
     * 一直不断的监听
     */
    public void listen() throws IOException {
        while (true) {
            // 监控注册在Selector上的ServerSocketChannel，返回值代表有多少channel已经就绪，可以进行I/O操作了
            int ready = selector.select();
            if (ready > 0) {
                /*
                 * selectedKeys()返回一个SelectionKey的集合，
                 * 因为完全有可能在这个Selector注册多个SelectionKey
                 * 比如OP_ACCEPT,OP_READ,OP_WRITE等
                 * 其中每个SelectionKey代表了一个可以进行IO操作的channel
                 * 一个ServerSocketChannel可以进行IO操作意味着有新的TCP连接连入了
                 */
                Set<SelectionKey> keys = selector.selectedKeys();
                SelectionKey key = null;
                for (Iterator<SelectionKey> it = keys.iterator(); it.hasNext(); ) {
                    key = (SelectionKey) it.next();
                    // 需要将处理过的key从selectedKeys这个集合中删除
                    it.remove();
                    handleKey(key);
                }
            }
        }
    }

    /**
     * 处理请求
     */
    public void handleKey(SelectionKey key) throws IOException {
        if (key == null) {
            logger.info("SelectionKey is null");
            return;
        }
        ServerSocketChannel serverSocketChannel = null;
        SocketChannel socketChannel = null;
        Socket socket = null;
        int count = 0;
        /* 判断这个SelectionKey对应的channel是否已准备好接收新的TCP连接 */
        if (key.isAcceptable()) {
            /* 从SelectionKey得到对应的channel */
            serverSocketChannel = (ServerSocketChannel) key.channel();
            /* 接受新的TCP连接(来自客户端) */
            socketChannel = serverSocketChannel.accept();
            /* 配置为非阻塞 */
            socketChannel.configureBlocking(Boolean.FALSE);
            /* 注册到selector，等待读取 */
            socketChannel.register(selector, SelectionKey.OP_READ);
        } else if (key.isReadable()) { // 判断该通道是否已准备好读
            /* 返回为之创建此键的通道 */
            socketChannel = (SocketChannel) key.channel();
            /* 将接收数据的缓冲区清空以备下次读取 */
            recieveBuffer.clear();
            /* 读取服务器发送的数据到recieveBuffer */
            count = socketChannel.read(recieveBuffer);
            if (count > 0) {
                String recvText = new String(recieveBuffer.array(), 0, count);
                logger.info("服务器端接受客户端数据--: " + recvText);
                socketChannel.register(selector, SelectionKey.OP_WRITE);
            }
        } else if (key.isWritable()) {// 判断该通道是否已准备好写
            socketChannel = (SocketChannel) key.channel();
            /* 将缓冲区清空以备下次写入 */
            sendBuffer.clear();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String data = dateFormat.format(new Date());
            /* 向缓冲区中输入数据 */
            sendBuffer.put(data.getBytes(Charset.defaultCharset()));
            // 反转缓冲区。首先将限制设置为当前位置，然后将位置设置为 0。如果已定义了标记，则丢弃该标记。
            // 常与compact方法一起使用。通常情况下，在准备从缓冲区中读取数据时调用flip方法
            // 重点
            sendBuffer.flip();
            /* 将数据输出到通道 */
            socketChannel.write(sendBuffer);
            logger.info("服务器端向客户端发送数据--: " + data);
            /* 又注册到selector，等待读取 */
            socketChannel.register(selector, SelectionKey.OP_READ);
        }
    }

    public static void main(String[] args) throws IOException {
        int port = 10110;
        NioServer server = new NioServer(port);
        server.listen();
    }
}
