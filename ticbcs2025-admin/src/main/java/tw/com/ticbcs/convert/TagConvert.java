package tw.com.ticbcs.convert;

import org.mapstruct.Mapper;

import tw.com.ticbcs.pojo.DTO.addEntityDTO.AddTagDTO;
import tw.com.ticbcs.pojo.DTO.putEntityDTO.UpdateTagDTO;
import tw.com.ticbcs.pojo.entity.Tag;

@Mapper(componentModel = "spring")
public interface TagConvert {

	Tag insertDTOToEntity(AddTagDTO addTagDTO);
	
	Tag updateDTOToEntity(UpdateTagDTO updateTagDTO);
	
}
