package tw.com.ticbcs.convert;

import org.mapstruct.Mapper;

import tw.com.ticbcs.pojo.DTO.addEntityDTO.AddAttendeesDTO;
import tw.com.ticbcs.pojo.VO.AttendeesVO;
import tw.com.ticbcs.pojo.entity.Attendees;

@Mapper(componentModel = "spring")
public interface AttendeesConvert {

	Attendees addDTOToEntity(AddAttendeesDTO addAttendeesDTO);

	// Attendees putDTOToEntity(PutAttendeesDTO putAttendeesDTO);
	
	AttendeesVO entityToVO(Attendees attendees);
	
	
	
}
