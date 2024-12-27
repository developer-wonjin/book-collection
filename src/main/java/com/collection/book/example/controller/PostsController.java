package com.collection.book.example.controller;

import com.collection.book.example.dto.PostsResponseDto;
import com.collection.book.example.dto.PostsSaveRequestDto;
import com.collection.book.example.service.PostsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostsController {

    private final PostsService postsService;

    @PostMapping("/api/v1/posts")
    public Long save(@RequestBody PostsSaveRequestDto requestDto) {
        log.info("Save posts request: {}", requestDto);
        return postsService.save(requestDto);
    }

    @GetMapping("/api/v1/posts")
    public List<PostsResponseDto> findAll() {
        List<PostsResponseDto> all = postsService.findAll();
        log.info("all: {}", all.size());
        return all;
    }

}
