package com.bcbs.member.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="member_id", nullable=false, unique=true, length=30)
    private String memberId;

    @Column(name="first_name", nullable=false, length=100)
    private String firstName;

    @Column(name="last_name", nullable=false, length=100)
    private String lastName;

    @Column(name="date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name="status", nullable=false, length=20)
    private String status;

    protected Member(){
        //required by JPA
    }

    public Member(
            String memberId,
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            String status){
        this.memberId = memberId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.status = status;
    }

    public long getId(){
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

    public String getStatus(){
        return status;
    }
}
