package com.smart.framework.ai.model;

/**
 * 学生记录
 *
 * @param name 姓名
 * @param age  年龄
 * @author smart-framework
 * @apiNote record = entity + lombok
 */
public record StudentRecord(String name, int age) {
}

