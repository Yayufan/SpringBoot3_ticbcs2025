package tw.com.ticbcs.service;

import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.IService;

import tw.com.ticbcs.pojo.entity.AttendeesHistory;

/**
 * <p>
 * 往年與會者名單 服务类
 * </p>
 *
 * @author Joey
 * @since 2025-05-12
 */
public interface AttendeesHistoryService extends IService<AttendeesHistory> {

	/**
	 * 根據年份 和 (ID card 或者 email)查詢往年與會者
	 * 
	 * @param year
	 * @param idCard
	 * @param email
	 * @return
	 */
	Boolean existsAttendeesHistory(Integer year, String idCard, String email);


	void importAttendeesHistory(MultipartFile file);
	
	void clearAllAttendeesHistory();

}
