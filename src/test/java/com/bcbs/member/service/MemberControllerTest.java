package com.bcbs.member.service;

import com.bcbs.member.service.controller.MemberController;
import com.bcbs.member.service.domain.MemberStatus;
import com.bcbs.member.service.entity.Member;
import com.bcbs.member.service.exception.MemberNotFoundException;
import com.bcbs.member.service.service.MemberService;
import com.bcbs.member.service.exception.DuplicateMemberException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bcbs.member.service.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@WebMvcTest(MemberController.class)
@Import(GlobalExceptionHandler.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @Test
    void createMember_shouldReturn201_whenRequestIsValid() throws Exception {
        Member member = new Member(
                "M10001",
                "John",
                "Doe",
                LocalDate.of(1990, 5, 10),
                MemberStatus.ACTIVE
        );

        when(memberService.createMember(any(Member.class)))
                .thenReturn(member);

        mockMvc.perform(post("/api/v1/members")
                .contentType("application/json")
                .content("""
                        {
                            "memberId": "M10001",
                            "firstName": "John",
                            "lastName": "Doe",
                            "dateOfBirth": "1990-05-10",
                            "status": "ACTIVE"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId").value("M10001"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void createMember_shouldReturn409_whenMemberAlreadyExists() throws Exception{
        when(memberService.createMember(any(Member.class)))
                .thenThrow(new DuplicateMemberException("M10001"));

        mockMvc.perform(post("/api/v1/members")
                .contentType("application/json")
                .content("""
                        {
                            "memberId": "M10001",
                            "firstName": "John",
                            "lastName": "Doe",
                            "dateOfBirth": "1990-05-10",
                            "status": "ACTIVE"
                        }
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Member already exists: M10001"))
                .andExpect(jsonPath("$.timestamp").exists());
    }


    @Test
    void createMember_shouldReturn400_whenRequestIsInvalid() throws Exception {

        mockMvc.perform(post("/api/v1/members")
                .contentType("application/json")
                .content("""
                        {
                            "memberId": "",
                            "firstName": "",
                            "lastName": "Doe",
                            "dateOfBirth": "1990-05-10"
                            
                        }
                        """))
                .andDo(print())
                .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.error").value("Bad Request"))
                                        .andExpect(jsonPath("$.message").value("Validation failed"))
                                                .andExpect(jsonPath("$.validationErrors.memberId").exists())
                                                        .andExpect(jsonPath("$.validationErrors.firstName").exists())
                                                                .andExpect(jsonPath("$.validationErrors.status").exists());

        verifyNoInteractions(memberService);
    }

    @Test
    void getMember_shouldReturn200_whenMemberExists() throws Exception{
        Member member = new Member(
                "M10001",
                "John",
                "Doe",
                LocalDate.of(1990, 05, 10),
                MemberStatus.ACTIVE
        );

        when(memberService.getMemberByMemberId("M10001"))
                .thenReturn(member);

        mockMvc.perform(get("/api/v1/members/M10001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value("M10001"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.dateOfBirth").value("1990-05-10"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(memberService).getMemberByMemberId("M10001");
    }

    @Test
    void getMember_shouldReturn404_whenMemberDoesNotExist() throws Exception {
        when(memberService.getMemberByMemberId("M99999"))
                .thenThrow(new MemberNotFoundException("M99999"));

        mockMvc.perform(get("/api/v1/members/M99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Member not found: M99999"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(memberService).getMemberByMemberId("M99999");
    }

    @Test
    void updateMember_shouldReturn200_whenMemberExists() throws Exception {

        Member updatedMember = new Member(
                "M10001",
                "Johnny",
                "Doe",
                LocalDate.of(1990,5,10),
                MemberStatus.ACTIVE
        );

        when(memberService.updateMember(
                eq("M10001"),
                any(Member.class)))
                .thenReturn(updatedMember);

        mockMvc.perform(put("/api/v1/members/M10001")
                        .contentType("application/json")
                        .content("""
                        {
                            
                            "firstName": "Johnny",
                            "lastName": "Doe",
                            "dateOfBirth": "1990-05-10",
                            "status": "ACTIVE"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value("M10001"))
                .andExpect(jsonPath("$.firstName").value("Johnny"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.dateOfBirth").value("1990-05-10"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(memberService).updateMember(
                eq("M10001"),
                any(Member.class));
    }

    @Test
    void updateMember_shouldReturn404_whenMemberDoesNotExist() throws Exception {

        when(memberService.updateMember(
                eq("M99999"),
                any(Member.class)))
                .thenThrow(new MemberNotFoundException("M99999"));

        mockMvc.perform(put("/api/v1/members/M99999")
                        .contentType("application/json")
                        .content("""
                        {
                            
                            "firstName": "John",
                            "lastName": "Doe",
                            "dateOfBirth": "1990-05-10",
                            "status": "ACTIVE"
                        }
                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Member not found: M99999"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(memberService).updateMember(
                eq("M99999"),
                any(Member.class));

    }

    @Test
    void deleteMember_shouldReturn204_whenMemberExists() throws Exception{
        mockMvc.perform(delete("/api/v1/members/M10001"))
                .andExpect(status().isNoContent());

        verify(memberService).deleteMember("M10001");
    }

    @Test
    void deleteMember_shouldReturn404_whenMemberDoesNotExist() throws Exception{
        doThrow(new MemberNotFoundException("M99999"))
                .when(memberService)
                .deleteMember("M99999");

        mockMvc.perform(delete("/api/v1/members/M99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Member not found: M99999"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(memberService).deleteMember("M99999");
    }

    @Test
    void getMembers_shouldReturnPagedMembers() throws Exception{
        Pageable pageable = PageRequest.of(0,10);

        Member member = new Member(
                "M10001",
                "John",
                "Doe",
                LocalDate.of(1990,1,1),
                MemberStatus.ACTIVE
        );

        Page<Member> memberPage =
                new PageImpl<>(List.of(member), pageable, 1);

        when(memberService.getMembers(null,pageable))
                .thenReturn(memberPage);

        mockMvc.perform(get("/api/v1/members")
                .param("page", "0")
                .param("size", "10"))
        .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].memberId").value("M10001"))
                .andExpect(jsonPath("$.content[0].firstName").value("John"))
                .andExpect(jsonPath("$.content[0].lastName").value("Doe"))
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(memberService).getMembers(null,pageable);

    }

    @Test
    void getMembers_shouldFilterByStatus() throws Exception {
        Pageable pageable = PageRequest.of(0,10);

        Member member = new Member(
                "M10001",
                "John",
                "Doe",
                LocalDate.of(1990,1,1),
                MemberStatus.ACTIVE
        );

        Page<Member> memberPage =
                new PageImpl<>(List.of(member), pageable, 1);

        when(memberService.getMembers(MemberStatus.ACTIVE, pageable))
                .thenReturn(memberPage);

        mockMvc.perform(get("/api/v1/members")
                .param("status", "ACTIVE")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].memberId").value("M10001"))
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(memberService)
                .getMembers(MemberStatus.ACTIVE, pageable);
    }

    @Test
    void getMembers_shouldReturn400_whenPageIsNegative() throws Exception {
        mockMvc.perform(get("/api/v1/members")
                .param("page", "-1")
                .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMembers_shouldReturn400_whenSizeExceedsMaximum() throws Exception {
        mockMvc.perform(get("/api/v1/members")
                .param("page", "0")
                .param("size", "101"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMembers_shouldReturn400_whenStatusIsInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/members")
                .param("status", "BANANA")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Invalid value 'BANANA' for parameter 'status'"))
                .andExpect(jsonPath("$.timestamp").exists());
    }


}
