package com.kotlin.foodkiosk.error.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class ErrorResponse {
    private String message;

    // 생성자 추가 (오류 메시지를 매개변수로 받음)
    public ErrorResponse(String message) {
        this.message = message;
    }
}
