package tw.com.ticbcs.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import tw.com.ticbcs.enums.MemberCategoryEnum;
import tw.com.ticbcs.enums.OrderStatusEnum;
import tw.com.ticbcs.pojo.BO.MemberExcelRaw;
import tw.com.ticbcs.pojo.DTO.AddGroupMemberDTO;
import tw.com.ticbcs.pojo.DTO.AddMemberForAdminDTO;
import tw.com.ticbcs.pojo.DTO.addEntityDTO.AddMemberDTO;
import tw.com.ticbcs.pojo.DTO.putEntityDTO.PutMemberDTO;
import tw.com.ticbcs.pojo.VO.MemberOrderVO;
import tw.com.ticbcs.pojo.VO.MemberTagVO;
import tw.com.ticbcs.pojo.VO.MemberVO;
import tw.com.ticbcs.pojo.entity.Member;
import tw.com.ticbcs.pojo.excelPojo.MemberExcel;

@Mapper(componentModel = "spring")
public interface MemberConvert {

	Member addDTOToEntity(AddMemberDTO addMemberDTO);

	Member addGroupDTOToEntity(AddGroupMemberDTO addGroupMemberDTO);

	Member forAdminAddDTOToEntity(AddMemberForAdminDTO addMemberForAdminDTO);

	Member putDTOToEntity(PutMemberDTO putMemberDTO);

	MemberVO entityToVO(Member member);

	List<MemberVO> entityListToVOList(List<Member> memberList);

	MemberTagVO entityToMemberTagVO(Member member);

	MemberOrderVO entityToMemberOrderVO(Member member);

	//實體類先轉成BO，這個BO之後要setStatus 手動塞入訂單狀態的
	MemberExcelRaw entityToExcelRaw(Member member);

	// BO對象轉成真正的Excel 對象
	@Mapping(target = "status", source = "status", qualifiedByName = "convertStatus")
	@Mapping(target = "category", source = "category", qualifiedByName = "convertCategory")
	MemberExcel memberExcelRawToExcel(MemberExcelRaw memberExcelRaw);

	@Named("convertStatus")
	default String convertStatus(Integer status) {
		return OrderStatusEnum.fromValue(status).getLabelZh();

	}

	@Named("convertCategory")
	default String convertCategory(Integer category) {
		return MemberCategoryEnum.fromValue(category).getLabelZh();

	}

}
