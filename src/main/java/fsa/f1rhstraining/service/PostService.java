package fsa.f1rhstraining.service;

import fsa.f1rhstraining.dto.PostDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PostService {
    // Get all posts
    List<PostDto> getAllPosts();

    // Get post by ID
    Optional<PostDto> getPostById(Long id);

    // Get posts by author username
    List<PostDto> getPostsByAuthor(String username);

    // Get posts by user ID
    List<PostDto> getPostsByUserId(Long userId);

    // Search posts by title
    List<PostDto> searchPostsByTitle(String title);

    // Create a new post
    @Transactional
    PostDto createPost(PostDto postDto);

    // Update an existing post
    @Transactional
    Optional<PostDto> updatePost(Long id, PostDto postDto);

    // Delete a post
    @Transactional
    boolean deletePost(Long id);
}
