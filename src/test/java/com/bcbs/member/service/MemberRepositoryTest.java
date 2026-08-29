package com.bcbs.member.service;

import com.bcbs.member.service.domain.MemberStatus;
import com.bcbs.member.service.entity.Member;
import com.bcbs.member.service.repository.MemberRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(
        replace=AutoConfigureTestDatabase.Replace.NONE
)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void shouldSaveandFindMember(){
        Member member = new Member(
                "MBR-1001",
                "John",
                "Smith",
                LocalDate.of(1990,5,15),
                MemberStatus.ACTIVE
        );

        Member savedMember = memberRepository.save(member);

        assertNotNull(savedMember.getId());

        Optional<Member> foundMember =
                memberRepository.findByMemberId("MBR-1001");

        assertTrue(foundMember.isPresent());
        assertEquals("John", foundMember.get().getFirstName());
        assertEquals("Smith", foundMember.get().getLastName());
        assertEquals(MemberStatus.ACTIVE, foundMember.get().getStatus());

    }

    @Test
    void shouldPopulateAuditTimestampsWhenMemberIsCreated(){
        Member member = new Member(
                "M-AUDIT-001",
                "AUDIT",
                "Test",
                LocalDate.of(1990,1,1),
                MemberStatus.ACTIVE
        );

        Member savedMember = memberRepository.saveAndFlush(member);

        assertNotNull(savedMember.getCreatedAt());
        assertNotNull(savedMember.getUpdatedAt());
    }

    @Test
    void shouldUpdateUpdatedAtWhenMemberIsModified() throws InterruptedException{
        Member member = new Member(
                "M-AUDIT-002",
                "Audit",
                "Test",
                LocalDate.of(1990,1,1),
                MemberStatus.ACTIVE
        );

        Member savedMember = memberRepository.saveAndFlush(member);

        OffsetDateTime originalCreatedAt = savedMember.getCreatedAt();
        OffsetDateTime originalUpdatedAt = savedMember.getUpdatedAt();

        Thread.sleep(10);

        savedMember.updateDetails(
                "Audit",
                "Updated",
                LocalDate.of(1990,1,1),
                MemberStatus.INACTIVE
        );

        Member updatedMember = memberRepository.saveAndFlush(savedMember);
        assertEquals(originalCreatedAt, updatedMember.getCreatedAt());

        assertTrue(updatedMember.getUpdatedAt().isAfter(originalUpdatedAt));
    }
}
