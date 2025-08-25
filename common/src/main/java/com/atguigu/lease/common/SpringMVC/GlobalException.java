package com.atguigu.lease.common.SpringMVC;

import com.atguigu.lease.common.Exception.notDeleteException;
import com.atguigu.lease.common.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(Exception.class)
    public Result exception(Exception e) {
        e.printStackTrace();
        return Result.fail();
    }


    @ExceptionHandler(notDeleteException.class)
    public Result notDeleteException(notDeleteException e) {
        e.printStackTrace();
        return Result.build(e.getCode(), e.getMessage());
    }
}
