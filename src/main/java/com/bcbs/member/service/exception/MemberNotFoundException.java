package com.bcbs.member.service.exception;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(String memberId){
        super("Member not found: " + memberId);
    }
}
