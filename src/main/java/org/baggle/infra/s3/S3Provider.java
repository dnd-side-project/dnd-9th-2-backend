package org.baggle.infra.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baggle.global.error.exception.ErrorCode;
import org.baggle.global.error.exception.InvalidValueException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Provider {
    // S3Config에 등록된 S3 접근 권한 객체입니다.
    private final AmazonS3Client amazonS3Client;

    // S3 bucket 이름입니다.
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // yml에 등록된 base url입니다.
    @Value("${cloud.aws.s3.uploadPath}")
    private String defaultUrl;

    /**
     * S3에 이미지 파일을 업로드하는 메서드입니다.
     * param: 이미지 파일, user idx, 이미지 타입(ex. profile or post ...)
     * return: S3에 저장된 파일 이름 (db 저장 용도)
     */
    public String uploadFile(MultipartFile multipartFile, String imageType) {
        validateMultipartFile(multipartFile);
        String savedFileName = getSavedFileName(multipartFile, imageType);
        ObjectMetadata metadata = new ObjectMetadata();
        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(bucketName, savedFileName, inputStream, metadata);
        } catch (IOException e) {
            log.error("Failed to upload image", e);
            throw new InvalidValueException(ErrorCode.INVALID_FILE_UPLOAD);
        }
        return getResourceUrl(savedFileName);
    }

    private void validateMultipartFile(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new InvalidValueException(ErrorCode.INVALID_MULTIPART_FILE);
        }
    }

    /**
     * S3에 등록된 사진을 삭제하는 메서드입니다.
     * param: 사진 url (db에서 확인 가능)
     * return: none
     */
    public void deleteFile(String fileUrl) {
        String fileName = getFileNameFromResourceUrl(fileUrl);
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
    }

    /**
     * S3에 저장할 파일 이름을 생성하는 메서드 입니다.
     * param: MultipartFile, user idx, 이미지 타입
     * return: 생성된 파일 이름 <--> user{번호}/{이미지 타입}/{UUID}-{입력된 파일 이름}
     */
    private String getSavedFileName(MultipartFile multipartFile, String imageType) {
        return String.format("%s/%s-%s",
                imageType, getRandomUUID(), multipartFile.getOriginalFilename());
    }

    /**
     * 랜덤 UUID를 생성합니다.
     * param: none
     * return: UUID
     */
    private String getRandomUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * S3에 저장된 파일에 접근할 수 있는 URL을 만듭니다.
     * param: 저장된 파일 이름 (db에서 확인 가능)
     * return: 파일 접근 url
     */
    private String getResourceUrl(String savedFileName) {
        return amazonS3Client.getResourceUrl(bucketName, savedFileName);
    }

    /**
     * URL에서 파일 이름을 추출하는 메서드입니다.
     * param: fileUrl (db에서 확인 가능)
     * return: 파일 이름
     */
    private String getFileNameFromResourceUrl(String fileUrl) {
        return fileUrl.replace(defaultUrl + "/", "");
    }
}