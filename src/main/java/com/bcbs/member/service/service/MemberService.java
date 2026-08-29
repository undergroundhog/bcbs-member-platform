package com.bcbs.member.service.service;

import com.bcbs.member.service.domain.MemberStatus;
import com.bcbs.member.service.entity.Member;
import com.bcbs.member.service.repository.MemberRepository;
import org.springframework.stereotype.Service;
import com.bcbs.member.service.exception.DuplicateMemberException;
import com.bcbs.member.service.exception.MemberNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    public Member createMember(Member member){

        if(memberRepository.existsByMemberId(member.getMemberId())){
            throw new DuplicateMemberException(member.getMemberId()
            );
        }
        return memberRepository.save(member);
    }

    public Member getMemberByMemberId(String memberId){
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
