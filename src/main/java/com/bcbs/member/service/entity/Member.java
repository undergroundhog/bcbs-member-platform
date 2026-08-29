package com.bcbs.member.service.entity;

import com.bcbs.member.service.domain.MemberStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="member_id", nullable=false, unique=true, length=30)
    private String memberId;

    @Column(name="first_name", nullable=false, length=100)
    private String firstName;

    @Column(name="last_name", nullable=false, length=100)
    private String lastName;

    @Column(name="date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable=false, length=20)
    private MemberStatus status;

    @Column(name = "created_at", nullable=false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable=false)
    private OffsetDateTime updatedAt;

    protected Member(){
        //required by JPA
    }

    public Member(
            String memberId,
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            MemberStatus status){
        this.memberId = memberId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.status = status;
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

    public void updateDetails(String firstName, String lastName, LocalDate dateOfBirth, MemberStatus status) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.status = status;
    }

    @PrePersist
    protected void onCreate(){
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt = OffsetDateTime.now();
    }

    public OffsetDateTime getCreatedAt(){
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt(){
        return updatedAt;
    }
}
