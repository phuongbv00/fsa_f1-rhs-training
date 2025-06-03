package fsa.f1rhstraining.mapper;

import fsa.f1rhstraining.dto.PostDto;
import fsa.f1rhstraining.entity.Post;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostMapper {

    public PostDto toDto(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getAuthor())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public Post toEntity(PostDto postDto) {
        return Post.builder()
                .id(postDto.getId())
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .author(postDto.getAuthor())
                .createdAt(postDto.getCreatedAt())
                .updatedAt(postDto.getUpdatedAt())
                .build();
    }

    public List<PostDto> toDtoList(List<Post> posts) {
        return posts.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}