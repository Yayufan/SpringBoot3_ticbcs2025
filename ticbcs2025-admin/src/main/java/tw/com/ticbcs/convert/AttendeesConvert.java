package tw.com.ticbcs.convert;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import tw.com.ticbcs.pojo.DTO.addEntityDTO.AddAttendeesDTO;
import tw.com.ticbcs.pojo.VO.AttendeesTagVO;
import tw.com.ticbcs.pojo.VO.AttendeesVO;
import tw.com.ticbcs.pojo.entity.Attendees;
import tw.com.ticbcs.pojo.excelPojo.AttendeesExcel;

@Mapper(componentModel = "spring")
public interface AttendeesConvert {

	Attendees addDTOToEntity(AddAttendeesDTO addAttendeesDTO);

	// Attendees putDTOToEntity(PutAttendeesDTO putAttendeesDTO);
	
	AttendeesVO entityToVO(Attendees attendees);
	
	AttendeesTagVO entityToAttendeesTagVO(Attendees attendees);
	
    @Mapping(source = "member.memberId", target = "memberId")
    @Mapping(source = "member.idCard", target = "idCard")
    @Mapping(source = "member.chineseName", target = "chineseName")
    @Mapping(source = "member.groupCode", target = "groupCode")
    @Mapping(source = "member.groupRole", target = "groupRole")
    @Mapping(source = "member.email", target = "email")
    @Mapping(source = "member.title", target = "title")
    @Mapping(source = "member.firstName", target = "firstName")
    @Mapping(source = "member.lastName", target = "lastName")
    @Mapping(source = "member.country", target = "country")
    @Mapping(source = "member.remitAccountLast5", target = "remitAccountLast5")
    @Mapping(source = "member.affiliation", target = "affiliation")
    @Mapping(source = "member.jobTitle", target = "jobTitle")
    @Mapping(source = "member.phone", target = "phone")
    @Mapping(source = "member.receipt", target = "receipt")
    @Mapping(source = "member.food", target = "food")
    @Mapping(source = "member.foodTaboo", target = "foodTaboo")
    @Mapping(source = "member.category", target = "category", qualifiedByName = "convertCategory")
    @Mapping(source = "member.categoryExtra", target = "categoryExtra")
	AttendeesExcel voToExcel(AttendeesVO attendeesVO);
	
    @Named("convertCategory")
	default String convertCategory(Integer category) {
		switch (category) {
		case 1:
			return "會員";
		case 2:
			return "其他";
		case 3:
			return "非會員";
		case 4:
			return "MVP";
		default:
			return "";
		}
	}
    
}
