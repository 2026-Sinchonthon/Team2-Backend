package org.example.team2backend.response;

import org.example.team2backend.exception.BaseErrorCode;

public record ApiResponse<T>(
        boolean success,
        T data,
        ErrorDetail error
) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static ApiResponse<Void> noContent() {
        return new ApiResponse<>(true, null, null);
    }

    public static ApiResponse<Void> error(BaseErrorCode errorCode, String message) {
        return new ApiResponse<>(false, null, new ErrorDetail(errorCode.name(), message));
    }

    public record ErrorDetail(String code, String message) {
    }
}
