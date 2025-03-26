package tw.com.ticbcs.convert;

import org.mapstruct.Mapper;

import tw.com.ticbcs.pojo.DTO.addEntityDTO.AddPaperReviewerDTO;
import tw.com.ticbcs.pojo.DTO.putEntityDTO.PutPaperReviewerDTO;
import tw.com.ticbcs.pojo.entity.PaperReviewer;

@Mapper(componentModel = "spring")
public interface PaperReviewerConvert {


	PaperReviewer addDTOToEntity(AddPaperReviewerDTO addPaperReviewerDTO);

	PaperReviewer putDTOToEntity(PutPaperReviewerDTO putPaperReviewerDTO);
	
	
}
