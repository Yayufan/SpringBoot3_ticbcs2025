package tw.com.ticbcs.controller;

import java.io.ByteArrayInputStream;

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
     * 下載Excel匯入模板 (CSV格式)
     */
    @Operation(summary = "下載往年與會者匯入模板 (CSV)")
    @GetMapping("/excel-template")
    public ResponseEntity<InputStreamResource> downloadTemplate() {
        // 由 Service 生出 CSV 內容 (byte array)
        byte[] templateContent = attendeesHistoryService.generateImportTemplate();

        ByteArrayInputStream bis = new ByteArrayInputStream(templateContent);
        InputStreamResource resource = new InputStreamResource(bis);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attendees_history_template.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    /**
     * 匯入往年與會者名單 (Excel或CSV檔)
     */
    @Operation(summary = "匯入往年與會者名單 (Excel/CSV)")
    @PostMapping("/import")
    public R<Void> importAttendeesHistory(@RequestParam("file") MultipartFile file) {
        attendeesHistoryService.importAttendeesHistory(file);
        return R.ok("匯入成功");
    }

    /**
     * 清除所有往年與會者資料
     */
    @Operation(summary = "清除所有往年與會者資料")
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearAttendeesHistory() {
        attendeesHistoryService.clearAllAttendeesHistory();
        return ResponseEntity.ok("已清除所有資料");
    }
	
}
