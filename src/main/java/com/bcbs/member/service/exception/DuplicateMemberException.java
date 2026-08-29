package com.bcbs.member.service.exception;

public class DuplicateMemberException extends RuntimeException{

    public DuplicateMemberException(String memberId){
        super("Member already exists: " + memberId);
    }
}
