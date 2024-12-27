package com.collection.book.example.controller;

import com.collection.book.example.dto.HelloResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/hello/dto")
    public HelloResponseDto helloDto(@RequestParam String name, @RequestParam int amount) {
        log.info("name: {}, amount: {}", name, amount);

        return new HelloResponseDto(name, amount);
    }


}
