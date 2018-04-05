package com.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TimeClient {

    Socket client;
    PrintWriter pw;

    public TimeClient() throws UnknownHostException, IOException {
        client = new Socket("localhost", 8000);
        pw = new PrintWriter(client.getOutputStream());
        // BufferedReader br=new BufferedReader(new
        // InputStreamReader(System.in));
        // pw.write(br.readLine());
        // pw.close();
        InputStream in = client.getInputStream();
        byte[] b = new byte[1024];
        in.read(b);
        System.out.println("dd=" + new String(b));
        in.close();
    }

    public static void main(String[] args) {
        try {
            new TimeClient();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
