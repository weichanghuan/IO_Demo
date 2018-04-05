package com.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class DiscardResponseClient {
    public static final String IP_ADDR = "localhost";// 服务器地址
    public static final int PORT = 8000;// 服务器端口号
    private static Socket socket;
    protected static OutputStream out;
    protected static InputStream in;

    public static void main(String[] args) {
        System.out.println("客户端启动...");
        System.out.println("当接收到服务器端字符为 \"OK\" 的时候, 客户端将终止\n");
        try {
            // 创建一个流套接字并将其连接到指定主机上的指定端口号
            socket = new Socket(IP_ADDR, PORT);

            // 读取服务器数据
            readServerMsg();
            // 向服务器端发送数据
            writeServerMsg();

        } catch (Exception e) {
            doException(e);
        }
    }

    private static void writeServerMsg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 向服务器端发送数据
                try {
                    out = socket.getOutputStream();
                    while (true) {
                        System.out.print("请输入: \t");
                        String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
                        out.write(str.getBytes("utf-8"));
                        readServerMsg();
                    }
                } catch (IOException e) {
                    doException(e);
                }
            }
        }).start();
    }

    private static void readServerMsg() {
        // TODO Auto-generated method stub
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 读取服务器端数据
                try {
                    while (true) {
                        in = socket.getInputStream();
                        byte[] b = new byte[1024];
                        in.read(b);
                        String ret = new String(b);
                        System.out.println("服务器端返回过来的是: " + ret);
                        // 如接收到 "OK" 则断开连接
                        if (ret != null && ret.contains("OK")) {
                            System.out.println("客户端将关闭连接");
                            sleep(500);
                            break;
                        }
                    }
                    closeSocket();
                } catch (IOException e) {
                    doException(e);
                }
            }
        }).start();
    }

    protected static void sleep(long millis) {
        // TODO Auto-generated method stub
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void doException(Exception e) {
        // TODO Auto-generated method stub
        System.out.println("客户端异常:" + e.getMessage());
        if (socket != null) {
            try {
                socket.close();
                socket = null;
            } catch (IOException e1) {
                socket = null;
                System.out.println("客户端 finally 异常:" + e1.getMessage());
            }
        }
    }

    protected static void closeSocket() throws IOException {
        // TODO Auto-generated method stub
        if (in != null) {
            in.close();
            in = null;
        }
        if (out != null) {
            out.close();
            out = null;
        }
        if (socket != null) {
            socket.close();
            socket = null;
        }
        System.out.println("关闭连接");
    }

}