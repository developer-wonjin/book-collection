package com.collection.book.example.controller;

import com.collection.book.example.dto.HelloResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelloControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testHello() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                                            "/hello",
                                                String.class
                                          );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("hello", response.getBody());
    }

    @Test
    public void testHelloDto() {
        String name = "John";
        int amount = 100;

        ResponseEntity<HelloResponseDto> response = restTemplate.getForEntity(
                                                    "/hello/dto?name={name}&amount={amount}",
                                                        HelloResponseDto.class,
                                                        name, amount
                                                    );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(name, response.getBody().getName());
        assertEquals(amount, response.getBody().getAmount());
    }
}