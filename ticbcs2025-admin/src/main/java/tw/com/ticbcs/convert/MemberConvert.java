package tw.com.ticbcs.convert;

import java.util.List;

import org.mapstruct.Mapper;

import tw.com.ticbcs.pojo.DTO.AddGroupMemberDTO;
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

	Member putDTOToEntity(PutMemberDTO putMemberDTO);
	
	MemberVO entityToVO(Member member);
	
	List<MemberVO> entityListToVOList(List<Member> memberList);
	
	MemberTagVO entityToMemberTagVO(Member member);
	
	MemberOrderVO entityToMemberOrderVO(Member member);

	MemberExcel entityToExcel(Member member);




	
}
