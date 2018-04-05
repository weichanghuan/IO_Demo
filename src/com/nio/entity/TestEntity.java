package com.nio.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @description:(测试实体)
 * @author 52762
 * @date 2017年11月4日 上午11:18:33
 * @since JDK 1.6
 */
public class TestEntity implements Serializable {

    /**
     * serialVersionUID:TODO（用一句话描述这个变量表示什么）
     *
     * @since Ver 1.1
     */

    private static final long serialVersionUID = -6220652041313670051L;

    private String id;

    private List<String> idList = new ArrayList<String>(0);

    private Map<Integer, String> idMap = new HashMap<>(0);

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getIdList() {
        return idList;
    }

    public void setIdList(List<String> idList) {
        this.idList = idList;
    }

    public Map<Integer, String> getIdMap() {
        return idMap;
    }

    public void setIdMap(Map<Integer, String> idMap) {
        this.idMap = idMap;
    }

}
