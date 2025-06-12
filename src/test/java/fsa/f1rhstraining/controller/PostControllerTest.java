package fsa.f1rhstraining.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fsa.f1rhstraining.dto.PostDto;
import fsa.f1rhstraining.service.PostService;
import fsa.f1rhstraining.service.impl.PostServiceImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public static void setup() {
        // insert posts -> DB
    }

    @AfterAll
    public static void cleanup() {
        // delete posts from DB
    }

    @Test
    public void testGetAllPosts() throws Exception {
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("First Post"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Second Post"));
    }

    @Test
    public void testGetPostById() throws Exception {
        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Post"));
    }

    @Test
    public void testCreatePost() throws Exception {
        PostDto postToCreate = createSamplePost(null, "New Post", "New Content", "New Author");
        PostDto createdPost = createSamplePost(1L, "New Post", "New Content", "New Author");

        when(postService.createPost(any(PostDto.class))).thenReturn(createdPost);

        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postToCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Post"));
    }

    @Test
    public void testUpdatePost() throws Exception {
        PostDto postToUpdate = createSamplePost(1L, "Updated Post", "Updated Content", "Updated Author");

        when(postService.updatePost(eq(1L), any(PostDto.class))).thenReturn(Optional.of(postToUpdate));

        mockMvc.perform(put("/api/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Post"));
    }

    @Test
    public void testDeletePost() throws Exception {
        when(postService.deletePost(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/posts/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetPostsByUserId() throws Exception {
        PostDto post1 = createSamplePost(1L, "User 1 Post 1", "Content 1", "user1");
        PostDto post2 = createSamplePost(1L, "User 1 Post 2", "Content 2", "user1");

        when(postService.getPostsByUserId(1L)).thenReturn(Arrays.asList(post1, post2));

        mockMvc.perform(get("/api/posts/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("User 1 Post 1"))
                .andExpect(jsonPath("$[1].title").value("User 1 Post 2"));
    }

    private PostDto createSamplePost(Long id, String title, String content, String authorUsername) {
        return PostDto.builder()
                .id(id)
                .title(title)
                .content(content)
                .userId(id != null ? id : 1L) // Use the post id as the user id for simplicity in tests
                .authorUsername(authorUsername)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
