package com.bio.other;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import org.junit.Test;

public class Tcp_IPfile {
    @Test
    public void client() throws Exception {// 应用TCP/ip充当客户端来传输文件
        Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), 9898);
        OutputStream os = socket.getOutputStream();
        File file = new File("H:\\1.jpg");
        FileInputStream fis = new FileInputStream(file);
        byte[] b = new byte[1024];
        int len;
        while ((len = fis.read(b)) != -1) {
            os.write(b, 0, len);
        }
        socket.shutdownOutput();
        InputStream is1 = socket.getInputStream();
        byte b1[] = new byte[1024];
        int len1;
        while ((len1 = is1.read(b1)) != -1) {
            String str = new String(b1, 0, len1);
            System.out.print(str);
        }
        is1.close();
        os.close();
        fis.close();
        socket.close();

    }

    @Test
    public void server() throws Exception {// 充当服务端
        ServerSocket ss = new ServerSocket(9898);
        Socket s = ss.accept();
        InputStream is = s.getInputStream();
        File file1 = new File("I:\\2.jpg");
        FileOutputStream fos = new FileOutputStream(file1);
        byte b[] = new byte[1024];
        int len;
        while ((len = is.read(b)) != -1) {
            fos.write(b, 0, len);
        }
        System.out.println("接收来自" + s.getInetAddress().getHostAddress());
        OutputStream os = s.getOutputStream();
        os.write("我是服务端，我已经收到你的图片了".getBytes());
        os.close();
        fos.close();
        is.close();
        s.close();
        ss.close();

    }
}
