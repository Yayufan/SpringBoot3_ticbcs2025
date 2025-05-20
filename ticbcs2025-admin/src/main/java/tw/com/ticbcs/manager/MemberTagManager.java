package tw.com.ticbcs.manager;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import tw.com.ticbcs.mapper.MemberTagMapper;
import tw.com.ticbcs.pojo.entity.MemberTag;

@Component
@RequiredArgsConstructor
public class MemberTagManager {
	
	private final MemberTagMapper memberTagMapper;

	public void addMemberTag(Long memberId, Long tagId) {
        MemberTag memberTag = new MemberTag();
        memberTag.setMemberId(memberId);
        memberTag.setTagId(tagId);
        memberTagMapper.insert(memberTag);
    }
	
}
