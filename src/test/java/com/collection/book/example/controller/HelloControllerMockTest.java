package com.collection.book.example.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class HelloControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHelloEndpointWithMock() throws Exception {
        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk()) // 응답 상태가 200 OK인지 확인
                .andExpect(content().string("hello")); // 응답 본문이 "hello"인지 확인
    }

    @Test
    public void testHelloDtoWithMock() throws Exception {
        String name = "hello";
        int amount = 1000;
        mockMvc.perform(
                        get("/hello/dto")
                                .param("name", name)
                                .param("amount", String.valueOf(amount)))
                .andExpect(status().isOk()) // 응답 상태가 200 OK인지 확인
                .andExpect(jsonPath("$.name", is(name)))
                .andExpect(jsonPath("$.amount", is(amount)))
        ;
    }


}