package com.bcbs.member.service.controller;

import com.bcbs.member.service.domain.MemberStatus;
import com.bcbs.member.service.dto.MemberRequest;
import com.bcbs.member.service.dto.MemberResponse;
import com.bcbs.member.service.dto.UpdateMemberRequest;
import com.bcbs.member.service.entity.Member;
import com.bcbs.member.service.service.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.web.bind.annotation.RequestParam;
import com.bcbs.member.service.dto.PagedResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.bcbs.member.service.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Tag(
        name="Members",
        description="APIs for managing BCBS members"
)
@RestController
@RequestMapping("api/v1/members")
@Validated
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService){
        this.memberService = memberService;
    }

    @Operation(
            summary="Create a member",
            description="Creates a new BCBS member"
    )
    @ApiResponse(responseCode = "201", description="Member created successfully",
            content= @Content(
                    mediaType="application/json",
                    schema=@Schema(implementation = ErrorResponse.class)
            ))
    @ApiResponse(responseCode="400", description="Invalid request",
    content= @Content(
            mediaType="application/json",
            schema=@Schema(implementation = ErrorResponse.class)
    ))
    @ApiResponse(responseCode="409", description="Member already exists",
            content= @Content(
                    mediaType="application/json",
                    schema=@Schema(implementation = ErrorResponse.class)
            ))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse createMember(@Valid @RequestBody MemberRequest request) {
        Member member = new Member(
                request.getMemberId(),
                request.getFirstName(),
                request.getLastName(),
                request.getDateOfBirth(),
                request.getStatus()
        );


        Member savedMember = memberService.createMember(member);
        return toResponse(savedMember);
    }


    @Operation(summary="Get member by member ID")
    @ApiResponse(responseCode = "200", description="Member found",
            content= @Content(
                    mediaType="application/json",
                    schema=@Schema(implementation = ErrorResponse.class)
            ))
    @ApiResponse(responseCode = "404", description="Member not found",
    content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)
    ))
    @GetMapping("/{memberId}")
    public MemberResponse getMember(@PathVariable String memberId){

        Member member = memberService.getMemberByMemberId(memberId);

        return toResponse(member);
    }

    private MemberResponse toResponse(Member member){
        return new MemberResponse(
                member.getId(),
                member.getMemberId(),
                member.getFirstName(),
                member.getLastName(),
                member.getDateOfBirth(),
                member.getStatus(),
                member.getCreatedAt(),
                member.getUpdatedAt()
        );
    }

//    @PutMapping("/{memberId}")
//    public MemberResponse updateMember(
//            @PathVariable String memberId,
//            @Valid @RequestBody MemberRequest request) {
//        Member updatedMember = new Member(
//                request.getMemberId(),
//                request.getFirstName(),
//                request.getLastName(),
//                request.getDateOfBirth(),
//                request.getStatus()
//        );
//
//        Member savedMember = memberService.updateMember(memberId, updatedMember);
//
//        return toResponse(savedMember);
//    }

    @Operation(summary="Update a member")
    @ApiResponse(responseCode="200", description="Member updated successully",
            content= @Content(
                    mediaType="application/json",
                    schema=@Schema(implementation = ErrorResponse.class)
            ))
    @ApiResponse(responseCode="400", description="Invalid request",
            content= @Content(
                    mediaType="application/json",
                    schema=@Schema(implementation = ErrorResponse.class)
            ))
    @ApiResponse(responseCode="404", description="Member not found",
            content= @Content(
                    mediaType="application/json",
                    schema=@Schema(implementation = ErrorResponse.class)
            ))
    @PutMapping("/{memberId}")
    public MemberResponse updateMember(
            @PathVariable String memberId,
            @Valid @RequestBody UpdateMemberRequest request) {
        Member updatedMember = new Member(
                memberId,
                request.getFirstName(),
                request.getLastName(),
                request.getDateOfBirth(),
                request.getStatus()
        );

        Member savedMember =
                memberService.updateMember(memberId, updatedMember);

        return toResponse(savedMember);

    }

    @Operation(summary="Delete a member")
    @ApiResponse(responseCode="204", description="Member deleted successfully",
            content= @Content(
                    mediaType="application/json",
                    schema=@Schema(implementation = ErrorResponse.class)
            ))
    @ApiResponse(responseCode="404", description="Member not found",
            content= @Content(
                    mediaType="application/json",
                    schema=@Schema(implementation = ErrorResponse.class)
            ))
    @DeleteMapping("/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMember(@PathVariable String memberId){
        memberService.deleteMember(memberId);
    }

//    @GetMapping
//    public PagedResponse<MemberResponse> getMembers(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size){
//        Pageable pageable = PageRequest.of(page, size);
//
//        Page<Member> memberPage = memberService.getMembers(pageable);
//
//        List<MemberResponse> content = memberPage.getContent()
//                .stream()
//                .map(this::toResponse)
//                .toList();
//
//        return new PagedResponse<>(
//                content,
//                memberPage.getNumber(),
//                memberPage.getSize(),
//                memberPage.getTotalElements(),
//                memberPage.getTotalPages()
//        );
//    }

    @Operation(
            summary="Get members",
            description="Returns a paginated list of members, optionally filtered by status"
    )
    @ApiResponse(responseCode = "200", description="Members retreived successfully",
            content= @Content(
                    mediaType="application/json",
                    schema=@Schema(implementation = ErrorResponse.class)
            ))
    @ApiResponse(responseCode = "400", description="Invalid status or pagingation parameters",
            content= @Content(
                    mediaType="application/json",
                    schema=@Schema(implementation = ErrorResponse.class)
            ))
    @GetMapping
    public PagedResponse<MemberResponse> getMembers(
            @RequestParam(required = false) MemberStatus status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10")  @Min(1) @Max(100) int size){

        Pageable pageable = PageRequest.of(page, size);

        Page<Member> memberPage =
                memberService.getMembers(status, pageable);

        List<MemberResponse> content = memberPage.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return new PagedResponse<>(
                content,
                memberPage.getNumber(),
                memberPage.getSize(),
                memberPage.getTotalElements(),
                memberPage.getTotalPages()
        );
    }



}

