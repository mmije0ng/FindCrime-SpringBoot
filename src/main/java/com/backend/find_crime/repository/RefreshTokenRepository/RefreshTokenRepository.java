package com.backend.find_crime.repository.RefreshTokenRepository;

import com.backend.find_crime.config.security.jwt.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
