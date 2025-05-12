package tw.com.ticbcs.manager;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import tw.com.ticbcs.convert.AttendeesConvert;
import tw.com.ticbcs.mapper.AttendeesMapper;
import tw.com.ticbcs.mapper.MemberMapper;
import tw.com.ticbcs.pojo.VO.AttendeesVO;
import tw.com.ticbcs.pojo.entity.Attendees;
import tw.com.ticbcs.pojo.entity.Member;

@Component
@RequiredArgsConstructor
public class AttendeesManager {

	private final AttendeesMapper attendeesMapper;
	private final AttendeesConvert attendeesConvert;
	private final MemberMapper memberMapper;

	public List<Attendees> getAttendeesList(){
		List<Attendees> attendeesList = attendeesMapper.selectAttendees();
		return attendeesList;
	}
	
	public List<AttendeesVO> getAttendeesVOByIds(Collection<Long> ids) {
		// 根據ids 查詢與會者列表
		List<Attendees> attendeesList = attendeesMapper.selectBatchIds(ids);

		// 根據與會者列表對應的memberId 整合成List,並拿到memberList 
		List<Long> memberIds = attendeesList.stream().map(Attendees::getMemberId).collect(Collectors.toList());
		List<Member> memberList = memberMapper.selectBatchIds(memberIds);

		// 透過Member製成映射關係
		Map<Long, Member> memberMap = memberList.stream()
				.collect(Collectors.toMap(Member::getMemberId, Function.identity()));

		// 最後組裝成AttendeesVO列表
		List<AttendeesVO> attendeesVOList = attendeesList.stream().map(attendees -> {
			AttendeesVO vo = attendeesConvert.entityToVO(attendees);
			vo.setMember(memberMap.get(attendees.getMemberId()));
			return vo;
		}).collect(Collectors.toList());

		return attendeesVOList;
	};

}
