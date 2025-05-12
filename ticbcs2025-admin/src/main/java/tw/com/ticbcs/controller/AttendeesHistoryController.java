package tw.com.ticbcs.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tw.com.ticbcs.service.AttendeesHistoryService;
import tw.com.ticbcs.utils.R;

/**
 * <p>
 * 往年與會者名單 前端控制器
 * </p>
 *
 * @author Joey
 * @since 2025-05-12
 */
@Tag(name = "往年與會者API")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/attendees-history")
public class AttendeesHistoryController {

	private final AttendeesHistoryService attendeesHistoryService;

	/**
	 * 清除所有往年與會者資料
	 */
	@Operation(summary = "清除所有往年與會者資料")
	@DeleteMapping("/clear")
	public R<Void> clearAttendeesHistory() {
		attendeesHistoryService.clearAllAttendeesHistory();
		return R.ok("已清除所有資料");
	}

	/**
	 * 下載Excel匯入模板
	 * 
	 * @throws IOException
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@Operation(summary = "下載往年與會者匯入模板 (Excel)")
	@GetMapping("/excel-template")
	public void downloadTemplate(HttpServletResponse response) throws IOException {
		// 由 Service 生出 excel 模板內容 (byte array)
		attendeesHistoryService.generateImportTemplate(response);

	}

	/**
	 * 匯入往年與會者名單 (Excel或CSV檔)
	 * 
	 * @throws IOException
	 */
	@Operation(summary = "匯入往年與會者名單 (Excel/CSV)")
	@PostMapping("/import")
	public R<Void> importAttendeesHistory(@RequestParam("file") MultipartFile file) throws IOException {
		attendeesHistoryService.importAttendeesHistory(file);
		return R.ok("匯入成功");
	}

}
