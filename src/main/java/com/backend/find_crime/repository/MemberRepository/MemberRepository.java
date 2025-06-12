package com.backend.find_crime.repository.MemberRepository;

import com.backend.find_crime.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
