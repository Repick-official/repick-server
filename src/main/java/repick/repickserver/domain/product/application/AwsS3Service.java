package repick.repickserver.domain.product.application;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import repick.repickserver.domain.product.domain.AwsS3;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import repick.repickserver.global.error.exception.CustomException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import static repick.repickserver.global.error.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AwsS3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public AwsS3 upload(MultipartFile multipartFile, String dirName) throws IOException {
        File file = convertMultipartFileToFile(multipartFile)
                .orElseThrow(() -> new CustomException(S3_FILE_CONVERT_FAIL));

        return upload(file, dirName);
    }

    private AwsS3 upload(File file, String dirName) {
        String key = randomFileName(file, dirName);
        String path = putS3(file, key);
        removeFile(file); // 로컬에 생성된 File 삭제 (MultipartFile -> File 변환 할 때 로컬에 파일 생성됨)

        return AwsS3
                .builder()
                .key(key)
                .path(path)
                .build();
    }

    private String randomFileName(File file, String dirName) {
        return dirName + "/" + UUID.randomUUID() + file.getName();
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return getS3(bucket, fileName);
    }

    private String getS3(String bucket, String fileName) {
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private void removeFile(File file) {
        file.delete();
    }

    public Optional<File> convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(System.getProperty("user.dir") + "/" + multipartFile.getOriginalFilename());

        if (file.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(file)){
                fos.write(multipartFile.getBytes());
            }
            return Optional.of(file);
        }
        return Optional.empty();
    }

    // TODO: 한글 이름 파일도 삭제 되는지 확인하기
    public void remove(AwsS3 awsS3) {
        if (!amazonS3.doesObjectExist(bucket, awsS3.getKey())) {
            throw new AmazonS3Exception("객체 " +awsS3.getKey()+ " 이/가 존재하지 않습니다.");
        }
        amazonS3.deleteObject(bucket, awsS3.getKey());
    }
}
