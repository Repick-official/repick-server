package repick.repickserver.domain.product.api;

import repick.repickserver.domain.product.domain.AwsS3;
import repick.repickserver.domain.product.application.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class AwsS3Controller {

    private final AwsS3Service awsS3Service;

    /**
     * S3에 파일 업로드, 삭제 기능 테스트용 API
     */
    @PostMapping("/test-image")
    public AwsS3 upload(@RequestPart("file") MultipartFile multipartFile) throws IOException {
        return awsS3Service.upload(multipartFile,"test");
    }

    @DeleteMapping("/test-image")
    public void remove(AwsS3 awsS3) {
        awsS3Service.remove(awsS3);
    }
}
