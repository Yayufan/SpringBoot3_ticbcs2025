package tw.com.ticbcs.convert;

import org.mapstruct.Mapper;

import tw.com.ticbcs.pojo.DTO.addEntityDTO.AddArticleAttachmentDTO;
import tw.com.ticbcs.pojo.entity.ArticleAttachment;

@Mapper(componentModel = "spring")
public interface ArticleAttachmentConvert {
	ArticleAttachment insertDTOToEntity(AddArticleAttachmentDTO insertArticleAttachmentDTO);
}
