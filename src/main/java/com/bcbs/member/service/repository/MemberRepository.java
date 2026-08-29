package com.bcbs.member.service.repository;

import com.bcbs.member.service.domain.MemberStatus;
import com.bcbs.member.service.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberId(String memberId);
    boolean existsByMemberId(String memberId);

    Page<Member> findByStatus(MemberStatus status, Pageable pageable);
}
