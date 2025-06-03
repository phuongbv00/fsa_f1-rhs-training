package fsa.f1rhstraining.integration;

import fsa.f1rhstraining.dto.PostDto;
import fsa.f1rhstraining.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PostIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/posts";
    }

    private String getUserBaseUrl() {
        return "http://localhost:" + port + "/api/users";
    }

    @Test
    public void testCrudOperations() {
        // Create a test user first
        UserDto userToCreate = UserDto.builder()
                .username("integration_tester")
                .email("integration@test.com")
                .password("password123")
                .fullName("Integration Tester")
                .build();

        ResponseEntity<UserDto> userCreateResponse = restTemplate.postForEntity(
                getUserBaseUrl(), userToCreate, UserDto.class);

        assertThat(userCreateResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(userCreateResponse.getBody()).isNotNull();
        assertThat(userCreateResponse.getBody().getId()).isNotNull();

        Long userId = userCreateResponse.getBody().getId();

        // Create a post
        PostDto postToCreate = PostDto.builder()
                .title("Integration Test Post")
                .content("This is a test post for integration testing")
                .userId(userId)
                .authorUsername("integration_tester")
                .build();

        ResponseEntity<PostDto> createResponse = restTemplate.postForEntity(
                getBaseUrl(), postToCreate, PostDto.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().getId()).isNotNull();
        assertThat(createResponse.getBody().getTitle()).isEqualTo("Integration Test Post");

        Long postId = createResponse.getBody().getId();

        // Get the post by ID
        ResponseEntity<PostDto> getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + postId, PostDto.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getId()).isEqualTo(postId);
        assertThat(getResponse.getBody().getTitle()).isEqualTo("Integration Test Post");

        // Update the post
        PostDto postToUpdate = getResponse.getBody();
        postToUpdate.setTitle("Updated Integration Test Post");
        postToUpdate.setContent("This post has been updated");

        ResponseEntity<PostDto> updateResponse = restTemplate.exchange(
                getBaseUrl() + "/" + postId,
                HttpMethod.PUT,
                new HttpEntity<>(postToUpdate),
                PostDto.class);

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).isNotNull();
        assertThat(updateResponse.getBody().getTitle()).isEqualTo("Updated Integration Test Post");
        assertThat(updateResponse.getBody().getContent()).isEqualTo("This post has been updated");

        // Get all posts
        ResponseEntity<List<PostDto>> getAllResponse = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PostDto>>() {});

        assertThat(getAllResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getAllResponse.getBody()).isNotNull();
        assertThat(getAllResponse.getBody()).hasSize(1);

        // Delete the post
        restTemplate.delete(getBaseUrl() + "/" + postId);

        // Verify the post is deleted
        ResponseEntity<PostDto> getAfterDeleteResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + postId, PostDto.class);

        assertThat(getAfterDeleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
