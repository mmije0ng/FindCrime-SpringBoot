package com.backend.find_crime.repository.PostRepository;

import com.backend.find_crime.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
}
