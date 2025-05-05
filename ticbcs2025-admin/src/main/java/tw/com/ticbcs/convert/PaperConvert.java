package tw.com.ticbcs.convert;

import org.mapstruct.Mapper;

import tw.com.ticbcs.pojo.DTO.PutPaperForAdminDTO;
import tw.com.ticbcs.pojo.DTO.addEntityDTO.AddPaperDTO;
import tw.com.ticbcs.pojo.DTO.putEntityDTO.PutPaperDTO;
import tw.com.ticbcs.pojo.VO.PaperVO;
import tw.com.ticbcs.pojo.entity.Paper;

@Mapper(componentModel = "spring")
public interface PaperConvert {

	Paper addDTOToEntity(AddPaperDTO addPaperDTO);

	Paper putDTOToEntity(PutPaperDTO putPaperDTO);
	
	Paper putForAdminDTOToEntity(PutPaperForAdminDTO putPaperForAdminDTO);
	
	PaperVO entityToVO(Paper paper);
	
	
	
	
}
