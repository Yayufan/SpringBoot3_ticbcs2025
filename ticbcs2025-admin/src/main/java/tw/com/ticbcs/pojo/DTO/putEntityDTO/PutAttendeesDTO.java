package tw.com.ticbcs.pojo.DTO.putEntityDTO;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PutAttendeesDTO {

	@Schema(description = "主鍵ID")
	private Long attendeesId;

	@Schema(description = "會員ID")
	private Long memberId;

	@Schema(description = "0為未簽到，1為已簽到，2為已簽退")
	private Integer lastCheckinStatus;

	@Schema(description = "最後簽到/退時間")
	private LocalDateTime lastCheckinTime;

	@Schema(description = "與會者mail ， 新增時從會員拿到")
	private String email;
}
