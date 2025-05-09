package tw.com.ticbcs.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;
import tw.com.ticbcs.convert.CheckinRecordConvert;
import tw.com.ticbcs.exception.CheckinRecordException;
import tw.com.ticbcs.manager.AttendeesManager;
import tw.com.ticbcs.mapper.CheckinRecordMapper;
import tw.com.ticbcs.pojo.DTO.addEntityDTO.AddCheckinRecordDTO;
import tw.com.ticbcs.pojo.DTO.putEntityDTO.PutCheckinRecordDTO;
import tw.com.ticbcs.pojo.VO.AttendeesVO;
import tw.com.ticbcs.pojo.VO.CheckinRecordVO;
import tw.com.ticbcs.pojo.entity.CheckinRecord;
import tw.com.ticbcs.service.AttendeesService;
import tw.com.ticbcs.service.CheckinRecordService;

/**
 * <p>
 * 簽到退紀錄 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2025-05-07
 */
@Service
@RequiredArgsConstructor
public class CheckinRecordServiceImpl extends ServiceImpl<CheckinRecordMapper, CheckinRecord>
		implements CheckinRecordService {

	private final CheckinRecordConvert checkinRecordConvert;
	private final AttendeesService attendeesService;
	private final AttendeesManager attendeesManager;

	@Override
	public CheckinRecordVO getCheckinRecord(Long checkinRecordId) {

		// 1.查詢簽到/退紀錄
		CheckinRecord checkinRecord = baseMapper.selectById(checkinRecordId);

		// 2.查詢此簽到者的基本資訊
		AttendeesVO attendeesVO = attendeesService.getAttendees(checkinRecord.getAttendeesId());

		// 3.實體類轉換成VO
		CheckinRecordVO checkinRecordVO = checkinRecordConvert.entityToVO(checkinRecord);

		// 4.vo中填入與會者VO對象
		checkinRecordVO.setAttendeesVO(attendeesVO);

		return checkinRecordVO;
	}

	@Override
	public List<CheckinRecordVO> getCheckinRecordList() {

		// 1.查詢所有簽到/退紀錄
		List<CheckinRecord> checkinRecordList = baseMapper.selectList(null);

		// 2.使用私有方法獲取CheckinRecordVOList
		List<CheckinRecordVO> checkinRecordVOList = this.convertToCheckinRecordVOList(checkinRecordList);

		return checkinRecordVOList;
	}

	@Override
	public IPage<CheckinRecordVO> getCheckinRecordPage(Page<CheckinRecord> page) {

		// 1.先獲取Page的資訊
		Page<CheckinRecord> checkinRecordPage = baseMapper.selectPage(page, null);

		// 2.使用私有方法獲取CheckinRecordVOList
		List<CheckinRecordVO> checkinRecordVOList = this.convertToCheckinRecordVOList(checkinRecordPage.getRecords());

		// 3.封裝成VOpage
		Page<CheckinRecordVO> checkinRecordVOPage = new Page<>(checkinRecordPage.getCurrent(),
				checkinRecordPage.getSize(), checkinRecordPage.getTotal());
		checkinRecordVOPage.setRecords(checkinRecordVOList);

		return checkinRecordVOPage;
	}

	@Override
	public CheckinRecordVO addCheckinRecord(AddCheckinRecordDTO addCheckinRecordDTO) {

		// 1.查詢指定 AttendeesId 最新的一筆
		CheckinRecord latestRecord = baseMapper.selectOne(new LambdaQueryWrapper<CheckinRecord>()
				.eq(CheckinRecord::getAttendeesId, addCheckinRecordDTO.getAttendeesId())
				.orderByDesc(CheckinRecord::getCheckinRecordId)
				.last("LIMIT 1"));

		// 2.最新數據不為null，判斷是否操作行為一致，如果一致，拋出異常，告知不可連續簽到 或 簽退
		if (latestRecord != null && latestRecord.getActionType().equals(addCheckinRecordDTO.getActionType())) {
			throw new CheckinRecordException("不可連續簽到 或 連續簽退");
		}

		// 3.轉換成entity對象
		CheckinRecord checkinRecord = checkinRecordConvert.addDTOToEntity(addCheckinRecordDTO);
		checkinRecord.setActionTime(LocalDateTime.now());

		// 4.新增進資料庫
		baseMapper.insert(checkinRecord);

		// 5.準備返回的數據
		return this.getCheckinRecord(checkinRecord.getCheckinRecordId());

	}

	@Override
	public void updateCheckinRecord(PutCheckinRecordDTO putCheckinRecordDTO) {
		CheckinRecord checkinRecord = checkinRecordConvert.putDTOToEntity(putCheckinRecordDTO);
		baseMapper.updateById(checkinRecord);
	}

	@Override
	public void deleteCheckinRecord(Long checkinRecordId) {
		baseMapper.deleteById(checkinRecordId);
	}

	@Override
	public void deleteCheckinRecordList(List<Long> checkinRecordIds) {
		for (Long checkinRecordId : checkinRecordIds) {
			this.deleteCheckinRecord(checkinRecordId);
		}
	}

	private List<CheckinRecordVO> convertToCheckinRecordVOList(List<CheckinRecord> checkinRecordList) {

		// 1.獲取與會者的ID(去重)
		Set<Long> attendeesIdSet = checkinRecordList.stream()
				.map(CheckinRecord::getAttendeesId)
				.collect(Collectors.toSet());

		// 2.透過去重的與會者ID拿到資料
		List<AttendeesVO> attendeesVOList = attendeesManager.getAttendeesVOByIds(attendeesIdSet);

		// 3.做成資料映射attendeesID 對應 AttendeesVO
		Map<Long, AttendeesVO> AttendeesVOMap = attendeesVOList.stream()
				.collect(Collectors.toMap(AttendeesVO::getAttendeesId, Function.identity()));

		// 4.checkinRecordList stream轉換後映射組裝成VO對象
		List<CheckinRecordVO> checkinRecordVOList = checkinRecordList.stream().map(checkinRecord -> {
			CheckinRecordVO vo = checkinRecordConvert.entityToVO(checkinRecord);
			vo.setAttendeesVO(AttendeesVOMap.get(checkinRecord.getAttendeesId()));
			return vo;
		}).collect(Collectors.toList());

		return checkinRecordVOList;
	};

}
