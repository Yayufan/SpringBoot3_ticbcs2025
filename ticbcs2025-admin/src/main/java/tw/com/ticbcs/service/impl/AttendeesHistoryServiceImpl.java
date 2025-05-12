package tw.com.ticbcs.service.impl;

import tw.com.ticbcs.pojo.entity.AttendeesHistory;
import tw.com.ticbcs.mapper.AttendeesHistoryMapper;
import tw.com.ticbcs.service.AttendeesHistoryService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 往年與會者名單 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2025-05-12
 */
@Service
@RequiredArgsConstructor
public class AttendeesHistoryServiceImpl extends ServiceImpl<AttendeesHistoryMapper, AttendeesHistory>
		implements AttendeesHistoryService {

	@Override
	public Boolean existsAttendeesHistory(Integer year, String idCard, String email) {
		LambdaQueryWrapper<AttendeesHistory> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(AttendeesHistory::getYear, year);

		if (idCard != null && !idCard.isBlank()) {
			wrapper.eq(AttendeesHistory::getIdCard, idCard);
		} else {
			wrapper.eq(AttendeesHistory::getEmail, email);
		}

		// 有可能為null 有可能查詢有值
		AttendeesHistory result = baseMapper.selectOne(wrapper);

		System.out.println("result 的值為" + result);

		// 回傳 true：資料庫有符合條件的紀錄 (result 不為 null)
		// 回傳 false：資料庫無符合條件的紀錄 (result 為 null)
		return result != null;
	}

	@Override
	public void importAttendeesHistory(MultipartFile file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearAllAttendeesHistory() {
		// TODO Auto-generated method stub
		
	}

}
