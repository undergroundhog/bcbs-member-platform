package com.bcbs.member.service.dto;

import com.bcbs.member.service.domain.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Schema(description="Response payload for creating a member")
public class MemberResponse {
    private Long id;

    @Schema(example="M10001")
    private String memberId;

    @Schema(example="John")
    private String firstName;

    @Schema(example="Doe")
    private String lastName;

    @Schema(example="1990-05-10")
    private LocalDate dateOfBirth;

    @Schema(example="ACTIVE")
    private MemberStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public MemberResponse(
            Long id,
            String memberId,
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            MemberStatus status,
            OffsetDateTime createAt,
            OffsetDateTime updatedAt){
        this.id = id;
        this.memberId = memberId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId(){
        return id;
    }

    public String getMemberId(){
        return memberId;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public LocalDate getDateOfBirth(){
        return dateOfBirth;
    }

    public MemberStatus getStatus(){
        return status;
    }

    public OffsetDateTime getCreatedAt(){
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt(){
        return updatedAt;
    }


}
