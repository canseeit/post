package com.post.post.service;

import com.post.post.dto.PostRequestDto;
import com.post.post.dto.PostResponseDto;
import com.post.post.entity.Post;
import com.post.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<PostResponseDto> getAllPosts() {
        // 전체 조회
        return postRepository.findAllByOrderByModifiedAtDesc().stream().map(PostResponseDto::new).toList();
    }

    public PostResponseDto findPost(Long id) {
        Post post = findPostById(id);
        return new PostResponseDto(post);
    }

    public PostResponseDto createPost(PostRequestDto requestDto) {
        // RequestDto -> Entity
        Post post = new Post(requestDto);
        // DB 저장
        Post savePost = postRepository.save(post);
        // Entity -> ResponseDto
        return new PostResponseDto(savePost);
    }

    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto requestDto) {
        Post post = findPostById(id);

        // 비밀번호 일치 여부 확인
        if (!post.getPassword().equals(requestDto.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        // 게시글 수정
        post.update(requestDto);

        return new PostResponseDto(post);
    }

    public Map<String, Object> deletePost(Long id, PostRequestDto requestDto) {
        Post post = findPostById(id);

        // 비밀번호 일치 여부 확인
        if (!post.getPassword().equals(requestDto.getPassword())) {
           throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        // 게시글 삭제
        postRepository.delete(post);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);

        return response;
    }

    private Post findPostById(Long id) {
        // 선택한 게시글 존재 확인
        return postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택한 게시글은 존재하지 않습니다."));
    }
}
