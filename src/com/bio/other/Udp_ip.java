package com.bio.other;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import org.junit.Test;

public class Udp_ip {// UDP编程的实现
    @Test
    public void send() {// 发送端
        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket();
            byte[] b = "你好，我是要发送的数据".getBytes();
            // 创建一个数据报：每一个数据报不能大于64k，都记录着数据信息，发送端的IP、端口号,以及要发送到
            // 的接收端的IP、端口号。
            DatagramPacket pack = new DatagramPacket(b, 0, b.length, InetAddress.getByName("127.0.0.1"), 9090);

            ds.send(pack);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (ds != null) {
                ds.close();
            }
        }

    }

    @Test
    public void rceive() {// 接收端
        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket(9090);
            byte[] b = new byte[1024];
            DatagramPacket pack = new DatagramPacket(b, 0, b.length);
            ds.receive(pack);

            String str = new String(pack.getData(), 0, pack.getLength());
            System.out.println(str);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (ds != null) {
                ds.close();

            }
        }
    }
}
