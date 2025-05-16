package tw.com.ticbcs.manager;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import lombok.RequiredArgsConstructor;
import tw.com.ticbcs.mapper.CheckinRecordMapper;
import tw.com.ticbcs.pojo.BO.CheckinInfoBO;
import tw.com.ticbcs.pojo.entity.CheckinRecord;

@Component
@RequiredArgsConstructor
public class CheckinRecordManager {

	private final CheckinRecordMapper checkinRecordMapper;

	/**
	 * 根據 attendeesId 找到與會者所有簽到/退紀錄
	 * 
	 * @param attendeesId
	 * @return
	 */
	public List<CheckinRecord> getCheckinRecordByAttendeesId(Long attendeesId) {
		// 找到這個與會者所有的checkin紀錄
		LambdaQueryWrapper<CheckinRecord> checkinRecordWrapper = new LambdaQueryWrapper<>();
		checkinRecordWrapper.eq(CheckinRecord::getAttendeesId, attendeesId);
		List<CheckinRecord> checkinRecordList = checkinRecordMapper.selectList(checkinRecordWrapper);
		return checkinRecordList;
	}

	/**
	 * 根據attendeesId , 找到這位與會者簡易的簽到退紀錄
	 * <p>
	 * (已最早的簽到紀錄 和 最晚的簽退紀錄組成)
	 * 
	 * @param attendeesId
	 * @return
	 */
	public CheckinInfoBO getLastCheckinRecordByAttendeesId(Long attendeesId) {
		// 先找到這個與會者所有的checkin紀錄
		LambdaQueryWrapper<CheckinRecord> checkinRecordWrapper = new LambdaQueryWrapper<>();
		checkinRecordWrapper.eq(CheckinRecord::getAttendeesId, attendeesId);
		List<CheckinRecord> checkinRecordList = checkinRecordMapper.selectList(checkinRecordWrapper);

		// 創建簡易簽到/退紀錄的BO對象
		CheckinInfoBO checkinInfoBO = new CheckinInfoBO();
		LocalDateTime checkinTime = null;
		LocalDateTime checkoutTime = null;

		// 遍歷所有簽到/退紀錄
		for (CheckinRecord record : checkinRecordList) {
			// 如果此次紀錄為 '簽到'
			if (record.getActionType() == 1) {
				// 在簽到時間為null 或者 遍歷對象的執行時間 早於 當前簽到時間的數值
				if (checkinTime == null || record.getActionTime().isBefore(checkinTime)) {
					// checkinTime的值進行覆蓋
					checkinTime = record.getActionTime();
				}
				// 如果此次紀錄為 '簽退'
			} else if (record.getActionType() == 2) {
				// 在簽到時間為null 或者 遍歷對象的執行時間 晚於 當前簽退時間的數值
				if (checkoutTime == null || record.getActionTime().isAfter(checkoutTime)) {
					checkoutTime = record.getActionTime();
				}
			}
		}

		// 將最早的簽到時間 和 最晚的簽退時間,組裝到BO對象中
		checkinInfoBO.setCheckinTime(checkinTime);
		checkinInfoBO.setCheckoutTime(checkoutTime);

		return checkinInfoBO;
	}

}
