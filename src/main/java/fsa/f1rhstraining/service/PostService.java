package fsa.f1rhstraining.service;

import fsa.f1rhstraining.dto.PostDto;
import fsa.f1rhstraining.entity.Post;
import fsa.f1rhstraining.entity.User;
import fsa.f1rhstraining.mapper.PostMapper;
import fsa.f1rhstraining.repository.PostRepository;
import fsa.f1rhstraining.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
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

    // Get posts by author username
    public List<PostDto> getPostsByAuthor(String username) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
        List<Post> posts = postRepository.findByAuthor(author);
        return postMapper.toDtoList(posts);
    }

    // Get posts by user ID
    public List<PostDto> getPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
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

                    // Update author if userId has changed
                    if (postDto.getUserId() != null && 
                        (existingPost.getAuthor() == null || !existingPost.getAuthor().getId().equals(postDto.getUserId()))) {
                        User author = userRepository.findById(postDto.getUserId())
                                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + postDto.getUserId()));
                        existingPost.setAuthor(author);
                    }

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
