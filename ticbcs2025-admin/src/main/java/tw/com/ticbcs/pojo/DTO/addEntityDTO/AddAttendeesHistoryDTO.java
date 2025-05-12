package tw.com.ticbcs.pojo.DTO.addEntityDTO;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddAttendeesHistoryDTO {

	@Schema(description = "參與時的年份")
	@NotBlank
	@JsonFormat(pattern = "yyyy")
	private LocalDate year;

	@Schema(description = "身分證字號 OR 護照號碼, 用於當作比對的第一標準")
	private String idCard;

	@Schema(description = "與會者的Email,用於用於當作比對的第二標準")
	@NotBlank
	private String email;

	@Schema(description = "姓名,不參與比對,只是方便辨識誰參與而已")
	private String name;
}
