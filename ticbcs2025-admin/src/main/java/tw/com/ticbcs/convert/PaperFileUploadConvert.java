package tw.com.ticbcs.convert;

import org.mapstruct.Mapper;

import tw.com.ticbcs.pojo.DTO.addEntityDTO.AddPaperFileUploadDTO;
import tw.com.ticbcs.pojo.DTO.putEntityDTO.PutPaperFileUploadDTO;
import tw.com.ticbcs.pojo.entity.PaperFileUpload;

@Mapper(componentModel = "spring")
public interface PaperFileUploadConvert {

	PaperFileUpload addDTOToEntity(AddPaperFileUploadDTO addPaperFileUploadDTO);

	PaperFileUpload putDTOToEntity(PutPaperFileUploadDTO putPaperFileUploadDTO);
	
	
	
}
