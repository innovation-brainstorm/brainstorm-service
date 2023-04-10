package org.brainstorm.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ResponseDto<T> implements Serializable {

    private T data;
    private String msg;//error msg
    private boolean success;

    public ResponseDto(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public ResponseDto(boolean success, T data) {
        this.success = success;
        this.data = data;
    }
}
