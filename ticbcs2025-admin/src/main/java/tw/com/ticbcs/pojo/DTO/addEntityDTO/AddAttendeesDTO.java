package tw.com.ticbcs.pojo.DTO.addEntityDTO;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AddAttendeesDTO {

	@Schema(description = "會員ID")
	private Long memberId;

	//預設為未簽到
	@Schema(description = "0為未簽到，1為已簽到，2為已簽退")
	private Integer lastCheckinStatus = 0;

	@Schema(description = "最後簽到/退時間")
	private LocalDateTime lastCheckinTime;

	@Schema(description = "與會者mail ， 新增時從會員拿到")
	private String email;
}
