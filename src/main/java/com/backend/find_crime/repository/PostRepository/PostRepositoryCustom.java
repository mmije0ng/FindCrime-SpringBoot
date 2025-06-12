package com.backend.find_crime.repository.PostRepository;

import com.backend.find_crime.domain.Post;
import com.backend.find_crime.domain.mapping.CrimeArea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<Post> findPostPagesByCrimeArea(CrimeArea crimeArea, Pageable pageable);
}
