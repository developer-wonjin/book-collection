package com.collection.book.example.service;

import com.collection.book.example.domain.PostsRepository;
import com.collection.book.example.dto.PostsResponseDto;
import com.collection.book.example.dto.PostsSaveRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostsService {

    private final PostsRepository postsRepository;

    public Long save(PostsSaveRequestDto requestDto) {
        return postsRepository.save(requestDto.toEntity()).getId();
    }

    public List<PostsResponseDto> findAll() {

        return postsRepository.findAll()
                .stream()
                .map(post -> PostsResponseDto.builder()
                        .titile(post.getTitle())
                        .content(post.getContent())
                        .build())
                .collect(Collectors.toList())
                ;
    }
}
