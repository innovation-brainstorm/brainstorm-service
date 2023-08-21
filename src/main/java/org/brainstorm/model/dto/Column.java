package org.brainstorm.model.dto;

import com.mysql.cj.jdbc.Blob;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
public class Column implements Serializable {
    private String name;
    private int strategy;
    //private byte[] shellFile;
    private String modelId;
    private Boolean isPretrained;
}

