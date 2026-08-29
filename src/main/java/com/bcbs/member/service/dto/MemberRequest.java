package com.bcbs.member.service.dto;

import com.bcbs.member.service.domain.MemberStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description="Request payload for creating a member")
public class MemberRequest {

    @NotBlank
    @Size(max = 30)
    @Schema(example="M10001")
    private String memberId;

    @NotBlank
    @Size(max = 100)
    @Schema(example="John")
    private String firstName;

    @NotBlank
    @Size(max = 100)
    @Schema(example="Doe")
    private String lastName;

    @Schema(example="1990-05-10")
    private LocalDate dateOfBirth;

    @NotNull
    @Schema(example="ACTIVE")
    private MemberStatus status;

    public MemberRequest(){

    }

    public String getMemberId(){
        return memberId;
    }

    public void setMemberId(String memberId){
        this.memberId = memberId;
    }

    public String getFirstName(){
        return firstName;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth(){
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth){
        this.dateOfBirth = dateOfBirth;
    }

    public MemberStatus getStatus(){
        return status;
    }

    public void setStatus(MemberStatus status){
        this.status = status;
    }

}


