package com.example.sentinel;

/**
 * @author zbm
 * @date 2024/3/1120:00
 */
import lombok.Data;
@Data
public class CommonResponse<T> {

    /**
     * 响应码
     */
    private String code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;
}
