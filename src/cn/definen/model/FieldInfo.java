package cn.definen.model;

import lombok.Data;

import java.beans.PropertyDescriptor;

@Data
public class FieldInfo {

    /**
     * 属性名
     */
    private String name;

    /**
     * 属性类型
     */
    private String type;

    /**
     * 备用
     */
    private String patchInfo;

    /**
     * 样例
     */
    private String demo;


    private PropertyDescriptor origin;

}
