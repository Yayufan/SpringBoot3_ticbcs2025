package tw.com.ticbcs.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import tw.com.ticbcs.pojo.DTO.addEntityDTO.AddCheckinRecordDTO;
import tw.com.ticbcs.pojo.DTO.putEntityDTO.PutCheckinRecordDTO;
import tw.com.ticbcs.pojo.VO.CheckinRecordVO;
import tw.com.ticbcs.pojo.entity.CheckinRecord;

/**
 * <p>
 * 簽到退紀錄 服务类
 * </p>
 *
 * @author Joey
 * @since 2025-05-07
 */
public interface CheckinRecordService extends IService<CheckinRecord> {

	/**
	 * 根據 checkinRecordId 獲取簽到/退紀錄
	 * 
	 * @param checkinRecordId
	 * @return
	 */
	CheckinRecordVO getCheckinRecord(Long checkinRecordId);

	/**
	 * 查詢所有簽到/退 紀錄
	 * 
	 * @return
	 */
	List<CheckinRecordVO> getCheckinRecordList();

	/**
	 * 查詢所有簽到/退 紀錄(分頁)
	 * 
	 * @param page
	 * @return
	 */
	IPage<CheckinRecordVO> getCheckinRecordPage(Page<CheckinRecord> page);

	/**
	 * 新增簽到/退紀錄
	 * 
	 * @param addCheckinRecordDTO
	 */
	CheckinRecordVO addCheckinRecord(AddCheckinRecordDTO addCheckinRecordDTO);

	/**
	 * 修改簽到/退紀錄
	 * 
	 * @param putCheckinRecordDTO
	 */
	void updateCheckinRecord(PutCheckinRecordDTO putCheckinRecordDTO);

	/**
	 * 刪除簽到/退紀錄
	 * 
	 * @param checkinRecordId
	 */
	void deleteCheckinRecord(Long checkinRecordId);

	/**
	 * 批量刪除簽到/退紀錄
	 * 
	 * @param checkinRecordIds
	 */
	void deleteCheckinRecordList(List<Long> checkinRecordIds);

}
