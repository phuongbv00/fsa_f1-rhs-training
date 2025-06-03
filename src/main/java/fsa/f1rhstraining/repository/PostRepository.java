package fsa.f1rhstraining.repository;

import fsa.f1rhstraining.entity.Post;
import fsa.f1rhstraining.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Find posts by author
    List<Post> findByAuthor(User author);

    // Find posts by author id
    List<Post> findByAuthorId(Long userId);

    // Find posts containing title (case insensitive)
    List<Post> findByTitleContainingIgnoreCase(String title);
}
