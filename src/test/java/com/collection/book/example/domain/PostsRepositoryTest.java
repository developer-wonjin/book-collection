package com.collection.book.example.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class PostsRepositoryTest {

    @Autowired
    private PostsRepository postsRepository;

    @Test
    void 게시글_저장하고_조회하기() {
        // given
        String title = "테스트 게시글";
        String content = "테스트 내용";
        postsRepository.save(Posts.builder().title(title).content(content).build());

        // when
        boolean postExists = postsRepository.findAll().stream()
                .anyMatch(post -> post.getTitle().equals(title) && post.getContent().equals(content));

        // then
        assertThat(postExists).isEqualTo(true);
    }
}