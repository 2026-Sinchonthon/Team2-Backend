package org.example.team2backend.storage;

import org.example.team2backend.exception.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum StorageErrorCode implements BaseErrorCode {

    EMPTY_FILE(HttpStatus.BAD_REQUEST, "업로드된 파일이 없습니다."),
    UNSUPPORTED_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "jpg, png, webp 형식의 이미지만 업로드할 수 있습니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다."),
    ;

    private final HttpStatus status;
    private final String message;

    StorageErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
