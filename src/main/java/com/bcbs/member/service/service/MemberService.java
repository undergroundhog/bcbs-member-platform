package com.bcbs.member.service.service;

import com.bcbs.member.service.domain.MemberStatus;
import com.bcbs.member.service.dto.MemberRequest;
import com.bcbs.member.service.dto.MemberResponse;
import com.bcbs.member.service.entity.Member;
import com.bcbs.member.service.repository.MemberRepository;
import org.springframework.stereotype.Service;
import com.bcbs.member.service.exception.DuplicateMemberException;
import com.bcbs.member.service.exception.MemberNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final MeterRegistry meterRegistry;

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);

    public MemberService(MemberRepository memberRepository,
                         MeterRegistry meterRegistry
    ){
        this.memberRepository = memberRepository;
        this.meterRegistry = meterRegistry;
    }

    public Member createMember(Member member){
        log.info("Creating Member with email={}", member.getMemberId());

        if(memberRepository.existsByMemberId(member.getMemberId())){
            log.warn("Duplicate member detected memberId={}", member.getMemberId());
            throw new DuplicateMemberException(member.getMemberId()
            );
        }

        Member savedMember = memberRepository.save(member);
        meterRegistry.counter("members.created",
                "status",
                savedMember.getStatus().name()).increment();
        log.info("Member created successfully memberId={}", savedMember.getMemberId());

        return savedMember;
    }

    public Member getMemberByMemberId(String memberId){
        log.debug("Looking up member memberId={}", memberId);
        return memberRepository.findByMemberId(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
    }

    public Optional<Member> getMemberById(Long id){

        return memberRepository.findById(id);
    }

    public Member updateMember(String memberId, Member updatedMember){
        Member existingMember = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        existingMember.updateDetails(
                updatedMember.getFirstName(),
                updatedMember.getLastName(),
                updatedMember.getDateOfBirth(),
                updatedMember.getStatus()
        );

        return memberRepository.save(existingMember);
    }

    public void deleteMember(String memberId){
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        memberRepository.delete(member);
        meterRegistry.counter("members.deleted",
                "status",
                member.getStatus().name()).increment();
        log.info("Member deleted successfully memberId={}", memberId);
    }

    public Page<Member> getMembers(Pageable pageable){
        return memberRepository.findAll(pageable);
    }

    public Page<Member> getMembers(MemberStatus status, Pageable pageable){
        if(status == null){
            return memberRepository.findAll(pageable);
        }

        return memberRepository.findByStatus(status, pageable);
    }
}
