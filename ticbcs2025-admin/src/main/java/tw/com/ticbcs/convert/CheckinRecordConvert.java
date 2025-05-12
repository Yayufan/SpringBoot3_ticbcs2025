package tw.com.ticbcs.convert;

import java.util.List;

import org.mapstruct.Mapper;

import tw.com.ticbcs.pojo.DTO.addEntityDTO.AddCheckinRecordDTO;
import tw.com.ticbcs.pojo.DTO.putEntityDTO.PutCheckinRecordDTO;
import tw.com.ticbcs.pojo.VO.CheckinRecordVO;
import tw.com.ticbcs.pojo.entity.CheckinRecord;
import tw.com.ticbcs.pojo.excelPojo.AttendeesExcel;
import tw.com.ticbcs.pojo.excelPojo.CheckinRecordExcel;

@Mapper(componentModel = "spring")
public interface CheckinRecordConvert {

	CheckinRecord addDTOToEntity(AddCheckinRecordDTO addCheckinRecordDTO);

	CheckinRecord putDTOToEntity(PutCheckinRecordDTO putCheckinRecordDTO);

	CheckinRecordVO entityToVO(CheckinRecord checkinRecord);

	List<CheckinRecordVO> entityListToVOList(List<CheckinRecord> checkinRecordList);

	CheckinRecordExcel attendeesExcelToCheckinRecordExcel(AttendeesExcel attendeesExcel);
	
}
