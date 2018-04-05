package com.nio.utils;

import com.nio.entity.TestEntity;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

/**
 * 
 * @description:(将对象转化成io)
 * @author 52762
 * @date 2017年11月4日 上午11:23:46
 * @since JDK 1.6
 */
public class ObjToByteBufferUtil {

    /**
     * 
     * readEntity:(将ByteBuffer转化成序列化对象)
     * 
     * @param buffer
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     * @author 52762
     * @date 2017年11月4日 上午11:31:02
     */
    public static TestEntity readEntity(ByteBuffer buffer) throws IOException, ClassNotFoundException {
        ByteArrayInputStream input = new ByteArrayInputStream(buffer.array());
        ObjectInputStream objInput = new ObjectInputStream(input);
        return (TestEntity) objInput.readObject();

    }

    /**
     * 
     * writeEntity:(将序列化实体转化成ByteBuffer)
     * 
     * @param testEntity
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     * @author 52762
     * @date 2017年11月4日 上午11:32:07
     */
    public static ByteBuffer writeEntity(TestEntity testEntity) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bOut);
        out.writeObject(testEntity);
        out.flush();
        return ByteBuffer.wrap(bOut.toByteArray());
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        TestEntity test = new TestEntity();
        test.setId("1");
        ByteBuffer writeEntity = ObjToByteBufferUtil.writeEntity(test);
        TestEntity readEntity = ObjToByteBufferUtil.readEntity(writeEntity);
        System.out.println(readEntity.getId());
    }
}
