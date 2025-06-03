package fsa.f1rhstraining.controller;

import fsa.f1rhstraining.dto.PostDto;
import fsa.f1rhstraining.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    // Get all posts
    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    // Get post by ID
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
        return postService.getPostById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get posts by author
    @GetMapping("/author/{author}")
    public ResponseEntity<List<PostDto>> getPostsByAuthor(@PathVariable String author) {
        List<PostDto> posts = postService.getPostsByAuthor(author);
        return posts.isEmpty() 
                ? ResponseEntity.noContent().build() 
                : ResponseEntity.ok(posts);
    }

    // Search posts by title
    @GetMapping("/search")
    public ResponseEntity<List<PostDto>> searchPostsByTitle(@RequestParam String title) {
        List<PostDto> posts = postService.searchPostsByTitle(title);
        return posts.isEmpty() 
                ? ResponseEntity.noContent().build() 
                : ResponseEntity.ok(posts);
    }

    // Create a new post
    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto) {
        PostDto createdPost = postService.createPost(postDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    // Update an existing post
    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(@PathVariable Long id, @RequestBody PostDto postDto) {
        return postService.updatePost(id, postDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete a post
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        return postService.deletePost(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
