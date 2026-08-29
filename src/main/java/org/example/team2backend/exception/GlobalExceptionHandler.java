package org.example.team2backend.exception;

import org.example.team2backend.response.ApiResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        BaseErrorCode errorCode = e.getErrorCode();
        HttpStatus status = errorCode.getStatus();

        if (status.is5xxServerError()) {
            log.error("[{}] {}", errorCode.name(), e.getMessage(), e);
        } else {
            log.info("[{}] {}", errorCode.name(), e.getMessage());
        }

        return ResponseEntity.status(status)
                .body(ApiResponse.error(errorCode, e.getMessage()));
    }

    // 기존 서비스 코드(RestaurantService 등)가 도메인 검증 실패를 IllegalArgumentException으로
    // 던지므로, BusinessException으로 전부 바꾸기 전까지는 여기서도 400으로 받아줍니다.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.info("[BAD_REQUEST] {}", e.getMessage());
        return ResponseEntity.status(GlobalErrorCode.BAD_REQUEST.getStatus())
                .body(ApiResponse.error(GlobalErrorCode.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFoundException(NotFoundException e) {
        log.info("[NOT_FOUND] {}", e.getMessage());
        return ResponseEntity.status(GlobalErrorCode.NOT_FOUND.getStatus())
                .body(ApiResponse.error(GlobalErrorCode.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException e) {
        log.info("[VALIDATION_FAILED] {}", e.getMessage());

        Map<String, String> fieldErrors = new HashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.merge(
                        error.getField(), error.getDefaultMessage(),
                        (existing, added) -> existing + ", " + added));

        return ResponseEntity.status(GlobalErrorCode.VALIDATION_FAILED.getStatus())
                .body(new ApiResponse<>(false, fieldErrors,
                        new ApiResponse.ErrorDetail(GlobalErrorCode.VALIDATION_FAILED.name(),
                                GlobalErrorCode.VALIDATION_FAILED.getMessage())));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.info("[BAD_REQUEST] {}", e.getMessage());
        return ResponseEntity.status(GlobalErrorCode.BAD_REQUEST.getStatus())
                .body(ApiResponse.error(GlobalErrorCode.BAD_REQUEST, GlobalErrorCode.BAD_REQUEST.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.info("[PAYLOAD_TOO_LARGE] {}", e.getMessage());
        return ResponseEntity.status(GlobalErrorCode.PAYLOAD_TOO_LARGE.getStatus())
                .body(ApiResponse.error(GlobalErrorCode.PAYLOAD_TOO_LARGE, GlobalErrorCode.PAYLOAD_TOO_LARGE.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception e) {
        log.error("[INTERNAL_SERVER_ERROR] {}", e.getMessage(), e);
        return ResponseEntity.status(GlobalErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiResponse.error(GlobalErrorCode.INTERNAL_SERVER_ERROR, GlobalErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }
}
