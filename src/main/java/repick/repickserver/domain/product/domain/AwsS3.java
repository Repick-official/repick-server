package repick.repickserver.domain.product.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AwsS3 {

    private String key; // 파일명
    private String path; // 파일 경로 (객체 URL)

    @Builder
    public AwsS3(String key, String path) {
        this.key = key;
        this.path = path;
    }
}
