package com.bio.other;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class TestTemp {

    public static void main(String[] args) throws Exception {
        RandomAccessFile raf = new RandomAccessFile("E:\\a.txt", "rw");
        FileChannel fc = raf.getChannel();
        // 将文件映射到内存中
        MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_WRITE, 0, raf.length());
        while (mbb.hasRemaining()) {
            System.out.print((char) mbb.get());
        }
        mbb.put(0, (byte) 98); // 修改文件
        raf.close();
    }

    /**
     * 
     * readFileByBIO:(通过BIO读取文件的内容)
     * 
     * @param fileName
     * @author 52762
     * @date 2017年10月15日 上午12:23:45
     */
    public void readFileByBIO(String fileName) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fileName);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                System.out.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     * readByNIO:(通过NIO)
     * 
     * @param file
     * @author 52762
     * @date 2017年10月15日 上午12:25:14
     */
    public void readByNIO(String file) {
        // 第一步 获取通道
        FileInputStream fis = null;
        FileChannel channel = null;
        try {
            fis = new FileInputStream(file);
            channel = fis.getChannel();
            // 文件内容的大小
            int size = (int) channel.size();

            // 第二步 指定缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            // 第三步 将通道中的数据读取到缓冲区中
            channel.read(buffer);

            Buffer bf = buffer.flip();
            System.out.println("limt:" + bf.limit());

            byte[] bt = buffer.array();
            System.out.println(new String(bt, 0, size));

            buffer.clear();
            buffer = null;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                channel.close();
                fis.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    public void writeFileByNIO(String file) {
        FileOutputStream fos = null;
        FileChannel fc = null;
        ByteBuffer buffer = null;
        try {
            fos = new FileOutputStream(file);
            // 第一步 获取一个通道
            fc = fos.getChannel();
            // buffer=ByteBuffer.allocate(1024);
            // 第二步 定义缓冲区
            buffer = ByteBuffer.wrap("Hello World 2".getBytes());
            // 将内容写到缓冲区
            fos.flush();
            fc.write(buffer);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                fc.close();
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public static void nioCopyFile(String resource, String destination) throws IOException {
        FileInputStream fis = new FileInputStream(resource);
        FileOutputStream fos = new FileOutputStream(destination);
        FileChannel readChannel = fis.getChannel(); // 读文件通道
        FileChannel writeChannel = fos.getChannel(); // 写文件通道
        ByteBuffer buffer = ByteBuffer.allocate(1024); // 读入数据缓存
        // 文件内容的大小
        int size = (int) readChannel.size();
        while (true) {
            buffer.clear();
            int len = readChannel.read(buffer); // 读入数据
            if (len == -1) {
                break; // 读取完毕
            }
            System.out.println(new String(buffer.array(), 0, size));
            buffer.flip();// 重置position
            System.out.println(new String(buffer.array(), 0, size));
            writeChannel.write(buffer); // 写入文件
        }
        readChannel.close();
        writeChannel.close();
    }

    /**
     * 文件复制 <br>
     * ------------------------------<br>
     * 
     * @param srcfilePath
     * @param targetPath
     * @throws IOException
     */
    private static void copy(String srcfilePath, String targetPath) throws IOException {
        File file = new File(targetPath);
        if (!file.getParentFile().exists()) {
            file.mkdirs();
        }
        RandomAccessFile inRandomAccessFile = new RandomAccessFile(srcfilePath, "r");
        FileChannel inFileChannel = inRandomAccessFile.getChannel();
        MappedByteBuffer inMappedByteBuffer = inFileChannel.map(MapMode.READ_ONLY, 0, inFileChannel.size());
        inRandomAccessFile.close();
        inFileChannel.close();

        RandomAccessFile outRandomAccessFile = new RandomAccessFile(targetPath, "rw");
        FileChannel outFileChannel = outRandomAccessFile.getChannel();
        MappedByteBuffer outMappedByteBuffer = outFileChannel.map(MapMode.READ_WRITE, 0, inMappedByteBuffer.capacity());
        outMappedByteBuffer.put(inMappedByteBuffer);
        outMappedByteBuffer.force();
        outRandomAccessFile.close();
        outFileChannel.close();
        outMappedByteBuffer.flip();
    }

    /**
     * 追加内容 <br>
     * ------------------------------<br>
     * 
     * @param path
     * @param str
     * @return
     * @throws IOException
     */
    private static MappedByteBuffer append(String path, String str) throws IOException {
        if (str == null || str.length() == 0)
            return null;
        RandomAccessFile randomAccessFile = new RandomAccessFile(path, "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();
        byte[] bytes = str.getBytes();
        long size = fileChannel.size() + bytes.length;
        MappedByteBuffer mappedByteBuffer = fileChannel.map(MapMode.READ_WRITE, 0, size);
        fileChannel.close();
        randomAccessFile.close();

        int position = mappedByteBuffer.limit() - bytes.length;
        mappedByteBuffer.position(position);
        mappedByteBuffer.put(bytes);
        mappedByteBuffer.force();
        mappedByteBuffer.flip();
        return mappedByteBuffer;
    }
}