package fsa.f1rhstraining.repository;

import fsa.f1rhstraining.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // Find posts by author
    List<Post> findByAuthor(String author);
    
    // Find posts containing title (case insensitive)
    List<Post> findByTitleContainingIgnoreCase(String title);
}