package com.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class DiscardResponseClient {
    public static final String IP_ADDR = "localhost";// ��������ַ
    public static final int PORT = 8000;// �������˿ں�
    private static Socket socket;
    protected static OutputStream out;
    protected static InputStream in;

    public static void main(String[] args) {
        System.out.println("�ͻ�������...");
        System.out.println("�����յ����������ַ�Ϊ \"OK\" ��ʱ��, �ͻ��˽���ֹ\n");
        try {
            // ����һ�����׽��ֲ��������ӵ�ָ�������ϵ�ָ���˿ں�
            socket = new Socket(IP_ADDR, PORT);

            // ��ȡ����������
            readServerMsg();
            // ��������˷�������
            writeServerMsg();

        } catch (Exception e) {
            doException(e);
        }
    }

    private static void writeServerMsg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // ��������˷�������
                try {
                    out = socket.getOutputStream();
                    while (true) {
                        System.out.print("������: \t");
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
                // ��ȡ������������
                try {
                    while (true) {
                        in = socket.getInputStream();
                        byte[] b = new byte[1024];
                        in.read(b);
                        String ret = new String(b);
                        System.out.println("�������˷��ع�������: " + ret);
                        // ����յ� "OK" ��Ͽ�����
                        if (ret != null && ret.contains("OK")) {
                            System.out.println("�ͻ��˽��ر�����");
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
        System.out.println("�ͻ����쳣:" + e.getMessage());
        if (socket != null) {
            try {
                socket.close();
                socket = null;
            } catch (IOException e1) {
                socket = null;
                System.out.println("�ͻ��� finally �쳣:" + e1.getMessage());
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
        System.out.println("�ر�����");
    }

}