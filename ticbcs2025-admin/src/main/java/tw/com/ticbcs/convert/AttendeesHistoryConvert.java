package tw.com.ticbcs.convert;

import org.mapstruct.Mapper;

import tw.com.ticbcs.pojo.DTO.addEntityDTO.AddAttendeesHistoryDTO;
import tw.com.ticbcs.pojo.entity.AttendeesHistory;

@Mapper(componentModel = "spring")
public interface AttendeesHistoryConvert {

	AttendeesHistory addDTOToEntity(AddAttendeesHistoryDTO addAttendeeHistoryDTO);

	
}
