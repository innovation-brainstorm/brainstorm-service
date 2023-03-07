package org.brainstorm.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.brainstorm.model.Student;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
class RestTemplateHandlerTest {
    @Autowired
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void test() {

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("https://www.baidu.com/", String.class);

        String body = responseEntity.getBody();
        HttpStatus statusCode = responseEntity.getStatusCode();
        int statusCodeValue = responseEntity.getStatusCodeValue();
        HttpHeaders headers = responseEntity.getHeaders();

        System.out.println("body:" + body);
        System.out.println("statusCode:" + statusCode);
        System.out.println("statusCodeValue" + statusCodeValue);
        System.out.println("headers" + headers);
    }

    @Test
    public void testPost() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject personJsonObject = new JSONObject();
        personJsonObject.put("id", 1);
        personJsonObject.put("name", "John");

        HttpEntity<String> request =
                new HttpEntity<>(personJsonObject.toString(), headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:8080/student/1", request, String.class);

        System.out.println(responseEntity.getBody());
    }
}