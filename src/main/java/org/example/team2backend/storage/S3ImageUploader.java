package org.example.team2backend.storage;

import org.example.team2backend.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * EC2에 붙은 IAM 역할을 그대로 사용합니다 (SDK 기본 자격증명 체인이 인스턴스
 * 메타데이터에서 자격증명을 가져오므로 액세스 키를 코드/설정에 두지 않습니다).
 */
@Slf4j
@Component
public class S3ImageUploader {

    private static final Duration API_CALL_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration API_CALL_ATTEMPT_TIMEOUT = Duration.ofSeconds(10);
    private static final List<String> ALLOWED_CONTENT_TYPES =
            List.of("image/jpeg", "image/png", "image/webp");

    private final S3Client s3;
    private final String bucket;
    private final String publicBaseUrl;

    public S3ImageUploader(
            @Value("${storage.s3.bucket}") String bucket,
            @Value("${storage.s3.region}") String region,
            @Value("${storage.s3.public-base-url:}") String publicBaseUrl
    ) {
        this.bucket = bucket;
        this.s3 = S3Client.builder()
                .region(Region.of(region))
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .apiCallTimeout(API_CALL_TIMEOUT)
                        .apiCallAttemptTimeout(API_CALL_ATTEMPT_TIMEOUT)
                        .build())
                .build();
        this.publicBaseUrl = publicBaseUrl.isBlank()
                ? "https://%s.s3.%s.amazonaws.com".formatted(bucket, region)
                : publicBaseUrl.replaceAll("/+$", "");
    }

    /**
     * @param keyPrefix 저장 경로 접두사. 예: {@code restaurants/12}
     */
    public String upload(String keyPrefix, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(StorageErrorCode.EMPTY_FILE);
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new BusinessException(StorageErrorCode.UNSUPPORTED_IMAGE_TYPE);
        }

        String key = "%s/%s%s".formatted(keyPrefix, UUID.randomUUID(), extensionOf(file));

        try {
            s3.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes()));
        } catch (IOException | RuntimeException e) {
            throw new BusinessException(StorageErrorCode.IMAGE_UPLOAD_FAILED, e.getMessage());
        }

        log.debug("S3 업로드 완료: {}/{} ({} bytes)", bucket, key, file.getSize());
        return publicBaseUrl + "/" + key;
    }

    /**
     * 저장된 이미지를 지웁니다. 우리 버킷 URL이 아니거나 이미 없는 이미지는 조용히 넘어갑니다
     * (지우는 요청이 실패해서 새 이미지 등록 자체가 막히는 것보다 낫습니다).
     */
    public void deleteByUrl(String imageUrl) {
        String prefix = publicBaseUrl + "/";
        if (imageUrl == null || !imageUrl.startsWith(prefix)) {
            return;
        }

        String key = imageUrl.substring(prefix.length());
        try {
            s3.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
        } catch (RuntimeException e) {
            log.warn("S3 이미지 삭제 실패: {} ({})", key, e.toString());
        }
    }

    private String extensionOf(MultipartFile file) {
        String filename = file.getOriginalFilename();
        int dot = (filename == null) ? -1 : filename.lastIndexOf('.');
        return (dot < 0) ? "" : filename.substring(dot);
    }
}
