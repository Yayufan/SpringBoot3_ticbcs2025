package tw.com.ticbcs.pojo.VO;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tw.com.ticbcs.pojo.entity.Member;

@Data
public class AttendeesVO {

	@Schema(description = "主鍵ID")
	private Long attendeesId;
	
	@Schema(description = "0為未簽到，1為已簽到，2為已簽退")
	private Integer lastCheckinStatus;

	@Schema(description = "最後簽到/退時間")
	private LocalDateTime lastCheckinTime;
	
	@Schema(description = "參與者流水序號")
	private Integer sequenceNo;
	
	@Schema(description = "會員資訊")
	private Member member;
	
	@Schema(description = "是否為去年與會者,true為是,false為否")
	private Boolean isLastYearAttendee = false;
	
}
