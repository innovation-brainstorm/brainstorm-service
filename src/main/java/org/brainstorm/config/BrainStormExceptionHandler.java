package org.brainstorm.config;

import lombok.extern.slf4j.Slf4j;
import org.brainstorm.model.dto.ResponseDto;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class BrainStormExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseDto<Boolean> exceptionHandler(Exception e) {
        log.error(e.getMessage());
        return new ResponseDto<>(false, "Something is wrong in the server.");
    }
}
