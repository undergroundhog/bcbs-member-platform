package com.bcbs.member.service;

import com.bcbs.member.service.domain.MemberStatus;
import com.bcbs.member.service.entity.Member;
import com.bcbs.member.service.exception.MemberNotFoundException;
import com.bcbs.member.service.repository.MemberRepository;
import com.bcbs.member.service.service.MemberService;
import com.bcbs.member.service.exception.DuplicateMemberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.RequestEntity.put;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private Member member;

    @BeforeEach
    void setup() {
        member = new Member(
                "MBR-2001",
                "Jane",
                "Doe",
                LocalDate.of(1992, 8, 20),
                MemberStatus.ACTIVE
        );
    }

    @Test
    void shouldCreateMember() {

        when(memberRepository.existsByMemberId("MBR-2001"))
                .thenReturn(false);

        when(memberRepository.save(member))
                .thenReturn(member);

        Member result = memberService.createMember(member);

        assertNotNull(result);
        assertEquals("MBR-2001", result.getMemberId());

        verify(memberRepository).existsByMemberId("MBR-2001");
        verify(memberRepository, times(1))
                .save(member);
    }

    @Test
    void shouldRejectDuplicateMember() {
        when(memberRepository.existsByMemberId("MBR-2001"))
                .thenReturn(true);

        DuplicateMemberException exception = assertThrows(
                DuplicateMemberException.class,
                () -> memberService.createMember(member)
        );

        assertEquals(
                "Member already exists: MBR-2001",
                exception.getMessage()
        );

        verify(memberRepository).existsByMemberId("MBR-2001");

        verify(memberRepository, never())
                .save(any(Member.class));
    }

    @Test
    void shouldFindMemberByMemberId() {
        when(memberRepository.findByMemberId("MBR-2001"))
                .thenReturn(Optional.of(member));

        Member result = memberService.getMemberByMemberId("MBR-2001");

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("Doe", result.getLastName());

        verify(memberRepository, times(1))
                .findByMemberId("MBR-2001");
    }

    @Test
    void shouldThrowExceptionWhenMemberDoesNotExist() {
        when(memberRepository.findByMemberId("UNKNOWN"))
                .thenReturn(Optional.empty());

        MemberNotFoundException exception = assertThrows(
                MemberNotFoundException.class,
                () -> memberService.getMemberByMemberId("UNKNOWN")
        );

        assertEquals(
                "Member not found: UNKNOWN",
                exception.getMessage()
        );

        verify(memberRepository)
                .findByMemberId("UNKNOWN");
    }


    @Test
    void createMember_shouldSaveMember_whenMemberDoesNotExist() {

        Member member = new Member(
                "M10001",
                "John",
                "Doe",
                LocalDate.of(1990, 5, 10),
                MemberStatus.ACTIVE
        );

        when(memberRepository.existsByMemberId("M10001"))
                .thenReturn(false);

        when(memberRepository.save(member))
                .thenReturn(member);

        Member result = memberService.createMember(member);

        assertNotNull(result);
        assertEquals("M10001", result.getMemberId());

        verify(memberRepository).existsByMemberId("M10001");
        verify(memberRepository).save(member);
    }

    @Test
    void createMember_shouldThrowException_whenMemberAlreadyExists() {
        Member member = new Member(
                "M10001",
                "John",
                "Doe",
                LocalDate.of(1990, 5, 10),
                MemberStatus.ACTIVE
        );

        when(memberRepository.existsByMemberId("M10001"))
                .thenReturn(true);

        DuplicateMemberException exception = assertThrows(
                DuplicateMemberException.class,
                () -> memberService.createMember(member)
        );

        assertEquals(
                "Member already exists: M10001",
                exception.getMessage()
        );

        verify(memberRepository).existsByMemberId("M10001");

        verify(memberRepository, never())
                .save(any(Member.class));
    }

    @Test
    void updateMember_shouldUpdateExistingMember() {

        Member existingMember = new Member(
                "MBR-2001",
                "Jane",
                "Doe",
                LocalDate.of(1992, 8, 20),
                MemberStatus.ACTIVE
        );

        Member update = new Member(
                "MBR-2001",
                "Janet",
                "Smith",
                LocalDate.of(1992, 8, 20),
                MemberStatus.INACTIVE
        );

        when(memberRepository.findByMemberId("MBR-2001"))
                .thenReturn(Optional.of(existingMember));

        when(memberRepository.save(existingMember))
                .thenReturn(existingMember);

        Member result =
                memberService.updateMember("MBR-2001", update);

        assertEquals("Janet", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals(MemberStatus.INACTIVE, result.getStatus());

        verify(memberRepository).findByMemberId("MBR-2001");
        verify(memberRepository).save(existingMember);
    }

    @Test
    void updateMember_shouldThrowException_whenMemberDoesNotExist() {
        Member update = new Member(
                "UNKNOWN",
                "Jane",
                "Doe",
                LocalDate.of(1992, 8, 20),
                MemberStatus.ACTIVE
        );

        when(memberRepository.findByMemberId("UNKNOWN"))
                .thenReturn(Optional.empty());

        MemberNotFoundException exception = assertThrows(
                MemberNotFoundException.class,
                () -> memberService.updateMember("UNKNOWN", update)
        );

        assertEquals(
                "Member not found: UNKNOWN",
                exception.getMessage()
        );

        verify(memberRepository).findByMemberId("UNKNOWN");
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void deleteMember_shouldDeleteExistingMember(){
        Member member = new Member(
                "M10001",
                "John",
                "Doe",
                LocalDate.of(1990, 5, 10),
                MemberStatus.ACTIVE
        );

        when(memberRepository.findByMemberId("M10001"))
                .thenReturn(Optional.of(member));

        memberService.deleteMember("M10001");

        verify(memberRepository).findByMemberId("M10001");
        verify(memberRepository).delete(member);
    }

    @Test
    void deleteMember_shouldThrowException_whenMemberDoesNotExist(){

        when(memberRepository.findByMemberId("M99999"))
                .thenReturn(Optional.empty());

        MemberNotFoundException exception = assertThrows(
                MemberNotFoundException.class,
                () -> memberService.deleteMember("M99999")
        );

        assertEquals(
                "Member not found: M99999",
                exception.getMessage()
        );

        verify(memberRepository).findByMemberId("M99999");
        verify(memberRepository, never()).delete(any(Member.class));
    }

    @Test
    void getMembers_shouldReturnPagedMembers(){
        Pageable pageable = PageRequest.of(0,10);

        Member member = new Member(
                "M10001",
                "John",
                "Doe",
                LocalDate.of(1990,1,1),
                MemberStatus.ACTIVE
        );

        Page<Member> page = new PageImpl<>(List.of(member), pageable, 1);

        when(memberRepository.findAll(pageable))
                .thenReturn(page);

        Page<Member> result = memberService.getMembers(null,pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("M10001", result.getContent().get(0).getMemberId());

        verify(memberRepository).findAll(pageable);

    }

    @Test
    void getMembers_shouldFilterByStatus(){
        Pageable pageable = PageRequest.of(0,10);

        Member member = new Member(
                "M10001",
                "John",
                "Doe",
                LocalDate.of(1990,1,1),
                MemberStatus.ACTIVE
        );

        Page<Member> page =
                new PageImpl<>(List.of(member), pageable, 1);

        when(memberRepository.findByStatus(MemberStatus.ACTIVE, pageable))
                .thenReturn(page);

        Page<Member> result =
                memberService.getMembers(MemberStatus.ACTIVE, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(MemberStatus.ACTIVE, result.getContent().get(0).getStatus());

        verify(memberRepository)
                .findByStatus(MemberStatus.ACTIVE, pageable);
    }
}
