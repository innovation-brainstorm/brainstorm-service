package org.brainstorm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.brainstorm.model.dto.AIUpdateDTO;
import org.brainstorm.model.dto.NewSessionDto;
import org.brainstorm.model.dto.ResponseDto;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
class SessionStatusControllerTest {

    @Test
    void generatedData() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        ObjectMapper mapper = new ObjectMapper();
        NewSessionDto request = mapper.readValue("{\n" +
                "  \"schema\": \"brainstorm\",\n" +
                "  \"table\": \"student\",\n" +
                "  \"quantity\": 4,\n" +
                "  \"destination\": \"view\",\n" +
                "  \"columns\": [\n" +
                "    {\"name\": \"name\", \"strategy\": 2},\n" +
                "    {\"name\": \"age\", \"strategy\": 1},\n" +
                "    {\"name\": \"id\", \"strategy\": 0}\n" +
                "  ]\n" +
                "}", NewSessionDto.class);
        ResponseEntity<ResponseDto> entity = restTemplate.postForEntity("http://localhost:8080/api/session/generatedData", request, ResponseDto.class);
        System.out.println(entity.getBody().getData());
        Assert.assertNotNull(entity);
    }

    @Test
    void updateStatus() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        ObjectMapper mapper = new ObjectMapper();
        AIUpdateDTO request = mapper.readValue("{\n" +
                "    \"sessionId\":117,\n" +
                "    \"taskId\":399,\n" +
                "    \"columnName\":\"columnName2\",\n" +
                "    \"actualCount\":1000,\n" +
                "    \"status\":\"COMPLETED\",\n" +
                "    \"filePath\":\"id.csv\"\n" +
                "}", AIUpdateDTO.class);

        ResponseEntity<ResponseDto> entity = restTemplate.postForEntity("http://localhost:8080/api/task/updateStatus", request, ResponseDto.class);
        Assert.assertNotNull(entity);
    }
}