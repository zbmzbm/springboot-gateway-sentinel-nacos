package com.example.sentinel;

import lombok.Getter;

/**
 * @author zbm
 * @date 2024/3/1215:18
 */
@Getter
public enum ResCodeEnum {
    ok(0, "成功"),
    NO_PARAM(201, "传入参数缺失,请检查"),
    ERR_PARAM(202, "传入参数异常,请检查"),
    VALID_FAIL_PARAM(203, "参数校验异常"),
    SENTINEL_FLOW_ERROR(427, "请求过于频繁，被限流了"),
    SENTINEL_DEGRADE_ERROR(428, "请求过于频繁，被降级了"),
    SENTINEL_PARAM_FLOW_ERROR(429, "热点数据请求过于频繁"),
    SENTINEL_SYSTEM_ERROR(430, "请求过于频繁，触发系统限制"),
    SENTINEL_AUTHORITY_ERROR(431, "授权规则不通过"),
    UNKNOWN(999, "操作失败！！");
    private Integer code;
    private String msg;

    ResCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static ResCodeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ResCodeEnum resMsgEnum : values()) {
            if(resMsgEnum.code.equals(code)) {
                return resMsgEnum;
            }
        }
        return UNKNOWN;
    }

}
