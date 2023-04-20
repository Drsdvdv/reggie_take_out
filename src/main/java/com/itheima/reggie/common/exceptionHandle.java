package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
@Slf4j
public class exceptionHandle {
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandle(CustomException exception) {
        log.error(exception.getMessage()); //报错记得打日志
        //这里拿到的message是业务类抛出的异常信息，我们把它显示到前端
        return R.error(exception.getMessage());
    }
}

