package com.smart.framework.ai.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Book 模型类
 *
 * @author smart-framework
 */
@Data
public class Book implements Serializable {

    private String id;

    private String bookName;
}

