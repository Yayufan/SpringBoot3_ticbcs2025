package tw.com.ticbcs.service;

import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.extension.service.IService;

import tw.com.ticbcs.pojo.entity.MemberTag;

/**
 * <p>
 * member表 和 tag表的關聯表 服务类
 * </p>
 *
 * @author Joey
 * @since 2025-01-23
 */
public interface MemberTagService extends IService<MemberTag> {
	/**
	 * 根據 tagId 查詢與之有關的所有Member關聯
	 * 
	 * @param tagId
	 * @return
	 */
	List<MemberTag> getMemberTagByTagId(Long tagId);

	/**
	 * 透過MemberTag 建立關聯
	 * 
	 * @param memberTag
	 */
	void addMemberTag(MemberTag memberTag);
	
	/**
	 * 透過 memberId 和 tagId 建立關聯
	 * 
	 * @param memberId 會員ID
	 * @param tagId 標籤ID
	 */
	void addMemberTag(Long memberId, Long tagId);
	
	/**
	 * 移除此 tag 與多位 member 關聯
	 * 
	 * @param tagId
	 * @param membersToRemove
	 */
	void removeTagRelationsForMembers(Long tagId, Set<Long> membersToRemove);

}
