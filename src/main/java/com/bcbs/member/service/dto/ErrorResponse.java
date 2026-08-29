package com.bcbs.member.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.Map;

@Schema(description="Standard API error response")
public class ErrorResponse {

    @Schema(example="2026-08-28T16:30:00-05:00")
    private OffsetDateTime timestamp;

    @Schema(example="400")
    private int status;

    @Schema(example="Bad Request")
    private String error;

    @Schema(example="Validation failed")
    private String message;

    @Schema(
            description="Field-level validation erros",
            example="{\"firstName\":\"must not be blank\"}"
    )
    private Map<String, String> validationErrors;

    public ErrorResponse(){

    }

    public ErrorResponse(
            OffsetDateTime timestamp,
            int status,
            String error,
            String message,
            Map<String, String> validationErrors){
                this.timestamp = timestamp;
                this.status = status;
                this.error = error;
                this.message = message;
                this.validationErrors = validationErrors;
    }

    public OffsetDateTime getTimestamp(){
        return timestamp;
    }

    public int getStatus(){
        return status;
    }

    public String getMessage(){
        return message;
    }

    public Map<String, String> getValidationErrors(){
        return validationErrors;
    }

}
