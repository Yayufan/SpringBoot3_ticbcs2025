package tw.com.ticbcs.system.pojo.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 前端往後端傳送的登入信息
 */

@Data
public class LoginInfo {

	@NotBlank 
	@Email
	private String email;
	
	@NotBlank
	private String password;
	
}
