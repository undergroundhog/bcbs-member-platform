package com.bcbs.member.service.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description="Current status of a member")
public enum MemberStatus {
    ACTIVE,
    INACTIVE,
    SUSPEND,
    TERMINATED
}
