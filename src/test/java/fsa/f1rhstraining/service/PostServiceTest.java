package fsa.f1rhstraining.service;

import fsa.f1rhstraining.dto.PostDto;
import fsa.f1rhstraining.entity.Post;
import fsa.f1rhstraining.entity.User;
import fsa.f1rhstraining.mapper.PostMapper;
import fsa.f1rhstraining.repository.PostRepository;
import fsa.f1rhstraining.repository.UserRepository;
import fsa.f1rhstraining.service.impl.PostServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

// Enable Mockito @Annotations
@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    // dependencies
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostMapper postMapper;

    // service under test
    @InjectMocks
    private PostServiceImpl postService;

    public static Stream<Arguments> getPostByIdArguments() {
        return Stream.of(
                Arguments.of(null, null, false, 1, 0),
                Arguments.of(Post.builder()
                                .id(1L)
                                .title("Original Title")
                                .content("Original Content")
                                .author(User.builder().id(1L).build())
                                .createdAt(LocalDateTime.now().minusDays(1))
                                .build(),
                        PostDto.builder()
                                .id(1L)
                                .title("Updated Title")
                                .content("Updated Content")
                                .userId(1L)
                                .build(), true, 1, 1)
        );
    }

    @BeforeEach
    void setUp() {
        // setup data
    }

    @AfterEach
    void tearDown() {
        // cleanup data
    }

    @Test
    void getAllPosts() {
    }

    @ParameterizedTest
    @MethodSource("getPostByIdArguments")
    void getPostById(Post post, PostDto postDto, boolean assertPostFound, int verify1, int verify2) {
        // Arrange
        long postId = 1L;
        Mockito.when(postRepository.findById(postId))
                .thenReturn(Optional.ofNullable(post));
        // 'lenient' strictness
        Mockito.lenient().when(postMapper.toDto(post)).thenReturn(postDto);

        // Act
        Optional<PostDto> retrievedPost = postService.getPostById(postId);

        // Assert
        Assertions.assertEquals(assertPostFound, retrievedPost.isPresent());

        // Verify interactions
        Mockito.verify(postRepository, Mockito.times(verify1)).findById(postId);
        Mockito.verify(postMapper, Mockito.times(verify2)).toDto(Mockito.any(Post.class));
    }

    @Test
    void getPostsByAuthor() {
    }

    @Test
    void getPostsByUserId() {
    }

    @Test
    void searchPostsByTitle() {
    }

    @Test
    void createPost() {
    }

    @Test
    void updatePost_HappyPath_NoAuthorChange() {
        // Arrange
        Long postId = 1L;
        Long userId = 2L;

        // Create existing post with author
        User existingAuthor = User.builder()
                .id(userId)
                .username("existingUser")
                .build();

        Post existingPost = Post.builder()
                .id(postId)
                .title("Original Title")
                .content("Original Content")
                .author(existingAuthor)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        // Create updated post DTO (same author)
        PostDto updateDto = PostDto.builder()
                .id(postId)
                .title("Updated Title")
                .content("Updated Content")
                .userId(userId)
                .build();

        // Create expected updated post
        Post updatedPost = Post.builder()
                .id(postId)
                .title("Updated Title")
                .content("Updated Content")
                .author(existingAuthor)
                .createdAt(existingPost.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        // Create expected DTO result
        PostDto expectedDto = PostDto.builder()
                .id(postId)
                .title("Updated Title")
                .content("Updated Content")
                .userId(userId)
                .authorUsername("existingUser")
                .createdAt(existingPost.getCreatedAt())
                .updatedAt(updatedPost.getUpdatedAt())
                .build();

        // Mock repository and mapper behavior
        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        Mockito.when(postRepository.save(Mockito.any(Post.class))).thenReturn(updatedPost);
        Mockito.when(postMapper.toDto(updatedPost)).thenReturn(expectedDto);

        // Act
        Optional<PostDto> result = postService.updatePost(postId, updateDto);

        // Assert
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(expectedDto.getTitle(), result.get().getTitle());
        Assertions.assertEquals(expectedDto.getContent(), result.get().getContent());
        Assertions.assertEquals(expectedDto.getUserId(), result.get().getUserId());

        // Verify interactions
        Mockito.verify(postRepository).findById(postId);
        Mockito.verify(postRepository).save(Mockito.any(Post.class));
        Mockito.verify(postMapper).toDto(updatedPost);
        // Verify userRepository was not called since author didn't change
        Mockito.verify(userRepository, Mockito.never()).findById(Mockito.anyLong());
    }

    @Test
    void updatePost_HappyPath_WithAuthorChange() {
        // Arrange
        Long postId = 1L;
        Long oldUserId = 2L;
        Long newUserId = 3L;

        // Create existing post with original author
        User oldAuthor = User.builder()
                .id(oldUserId)
                .username("oldUser")
                .build();

        Post existingPost = Post.builder()
                .id(postId)
                .title("Original Title")
                .content("Original Content")
                .author(oldAuthor)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        // Create new author
        User newAuthor = User.builder()
                .id(newUserId)
                .username("newUser")
                .build();

        // Create updated post DTO (with new author)
        PostDto updateDto = PostDto.builder()
                .id(postId)
                .title("Updated Title")
                .content("Updated Content")
                .userId(newUserId)
                .build();

        // Create expected updated post
        Post updatedPost = Post.builder()
                .id(postId)
                .title("Updated Title")
                .content("Updated Content")
                .author(newAuthor)
                .createdAt(existingPost.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        // Create expected DTO result
        PostDto expectedDto = PostDto.builder()
                .id(postId)
                .title("Updated Title")
                .content("Updated Content")
                .userId(newUserId)
                .authorUsername("newUser")
                .createdAt(existingPost.getCreatedAt())
                .updatedAt(updatedPost.getUpdatedAt())
                .build();

        // Mock repository and mapper behavior
        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        Mockito.when(userRepository.findById(newUserId)).thenReturn(Optional.of(newAuthor));
        Mockito.when(postRepository.save(Mockito.any(Post.class))).thenReturn(updatedPost);
        Mockito.when(postMapper.toDto(updatedPost)).thenReturn(expectedDto);

        // Act
        Optional<PostDto> result = postService.updatePost(postId, updateDto);

        // Assert
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(expectedDto.getTitle(), result.get().getTitle());
        Assertions.assertEquals(expectedDto.getContent(), result.get().getContent());
        Assertions.assertEquals(expectedDto.getUserId(), result.get().getUserId());
        Assertions.assertEquals(expectedDto.getAuthorUsername(), result.get().getAuthorUsername());

        // Verify interactions
        Mockito.verify(postRepository).findById(postId);
        Mockito.verify(userRepository).findById(newUserId);
        Mockito.verify(postRepository).save(Mockito.any(Post.class));
        Mockito.verify(postMapper).toDto(updatedPost);
    }

    @Test
    void updatePost_PostNotFound() {
        // Arrange
        Long postId = 1L;
        PostDto updateDto = PostDto.builder()
                .id(postId)
                .title("Updated Title")
                .content("Updated Content")
                .userId(2L)
                .build();

        // Mock repository behavior - post not found
        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act
        Optional<PostDto> result = postService.updatePost(postId, updateDto);

        // Assert
        Assertions.assertTrue(result.isEmpty());

        // Verify interactions
        Mockito.verify(postRepository).findById(postId);
        Mockito.verify(userRepository, Mockito.never()).findById(Mockito.anyLong());
        Mockito.verify(postRepository, Mockito.never()).save(Mockito.any(Post.class));
        Mockito.verify(postMapper, Mockito.never()).toDto(Mockito.any(Post.class));
    }

    @Test
    void updatePost_AuthorNotFound() {
        // Arrange
        Long postId = 1L;
        Long oldUserId = 2L;
        Long newUserId = 3L;

        // Create existing post with original author
        User oldAuthor = User.builder()
                .id(oldUserId)
                .username("oldUser")
                .build();

        Post existingPost = Post.builder()
                .id(postId)
                .title("Original Title")
                .content("Original Content")
                .author(oldAuthor)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        // Create updated post DTO (with new author that doesn't exist)
        PostDto updateDto = PostDto.builder()
                .id(postId)
                .title("Updated Title")
                .content("Updated Content")
                .userId(newUserId)
                .build();

        // Mock repository behavior
        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        Mockito.when(userRepository.findById(newUserId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            postService.updatePost(postId, updateDto);
        });

        // Verify interactions
        Mockito.verify(postRepository).findById(postId);
        Mockito.verify(userRepository).findById(newUserId);
        Mockito.verify(postRepository, Mockito.never()).save(Mockito.any(Post.class));
        Mockito.verify(postMapper, Mockito.never()).toDto(Mockito.any(Post.class));
    }

    @Test
    void deletePost() {
    }
}
