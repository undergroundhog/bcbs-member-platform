package com.bcbs.member.service;

import com.bcbs.member.entity.Member;
import com.bcbs.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    public Member createMember(Member member){

        if(memberRepository.existsByMemberId(member.getMemberId())){
            throw new IllegalArgumentException(
                    "Member already exists: " + member.getMemberId()
            );
        }
        return memberRepository.save(member);
    }
}
