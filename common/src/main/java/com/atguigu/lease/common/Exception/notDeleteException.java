package com.atguigu.lease.common.Exception;

import com.atguigu.lease.common.result.ResultCodeEnum;
import lombok.Data;

@Data
public class notDeleteException extends RuntimeException {

    private Integer code;

    public notDeleteException(String message, Integer code) {
        super(message);
        this.code = code;
    }


    public notDeleteException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }
}
