package com.example.product_store;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Error response format")
public class ErrorResponse {

    private String error;

    private String message;

    private int status;

    private String timestamp;
}
