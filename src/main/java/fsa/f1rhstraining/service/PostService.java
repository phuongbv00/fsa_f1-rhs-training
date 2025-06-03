package fsa.f1rhstraining.service;

import fsa.f1rhstraining.dto.PostDto;
import fsa.f1rhstraining.entity.Post;
import fsa.f1rhstraining.mapper.PostMapper;
import fsa.f1rhstraining.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Autowired
    public PostService(PostRepository postRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
    }

    // Get all posts
    public List<PostDto> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return postMapper.toDtoList(posts);
    }

    // Get post by ID
    public Optional<PostDto> getPostById(Long id) {
        return postRepository.findById(id)
                .map(postMapper::toDto);
    }

    // Get posts by author
    public List<PostDto> getPostsByAuthor(String author) {
        List<Post> posts = postRepository.findByAuthor(author);
        return postMapper.toDtoList(posts);
    }

    // Search posts by title
    public List<PostDto> searchPostsByTitle(String title) {
        List<Post> posts = postRepository.findByTitleContainingIgnoreCase(title);
        return postMapper.toDtoList(posts);
    }

    // Create a new post
    @Transactional
    public PostDto createPost(PostDto postDto) {
        Post post = postMapper.toEntity(postDto);
        post.setCreatedAt(LocalDateTime.now());
        Post savedPost = postRepository.save(post);
        return postMapper.toDto(savedPost);
    }

    // Update an existing post
    @Transactional
    public Optional<PostDto> updatePost(Long id, PostDto postDto) {
        return postRepository.findById(id)
                .map(existingPost -> {
                    existingPost.setTitle(postDto.getTitle());
                    existingPost.setContent(postDto.getContent());
                    existingPost.setAuthor(postDto.getAuthor());
                    existingPost.setUpdatedAt(LocalDateTime.now());
                    Post updatedPost = postRepository.save(existingPost);
                    return postMapper.toDto(updatedPost);
                });
    }

    // Delete a post
    @Transactional
    public boolean deletePost(Long id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
