package repick.repickserver.domain.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Schema(name = "FileVo", description = "파일 정보")
public class FileVo {
    private MultipartFile mainImageFile;
    private List<MultipartFile> detailImageFiles;
    private String request;
    private List<Long> categoryIds;
}
