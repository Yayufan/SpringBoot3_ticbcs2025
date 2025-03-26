package tw.com.ticbcs.convert;

import org.mapstruct.Mapper;

import tw.com.ticbcs.pojo.DTO.addEntityDTO.AddEmailTemplateDTO;
import tw.com.ticbcs.pojo.DTO.putEntityDTO.UpdateEmailTemplateDTO;
import tw.com.ticbcs.pojo.entity.EmailTemplate;

@Mapper(componentModel = "spring")
public interface EmailTemplateConvert {

	EmailTemplate insertDTOToEntity(AddEmailTemplateDTO addArticleDTO);

	EmailTemplate updateDTOToEntity(UpdateEmailTemplateDTO updateArticleDTO);
	
}
