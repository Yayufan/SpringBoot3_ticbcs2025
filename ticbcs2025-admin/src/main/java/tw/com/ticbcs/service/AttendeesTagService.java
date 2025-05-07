package tw.com.ticbcs.service;

import tw.com.ticbcs.pojo.entity.AttendeesTag;
import tw.com.ticbcs.pojo.entity.AttendeesTag;

import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 與會者 與 標籤 的關聯表 服务类
 * </p>
 *
 * @author Joey
 * @since 2025-05-07
 */
public interface AttendeesTagService extends IService<AttendeesTag> {
	/**
	 * 根據 tagId 查詢與之有關的所有Attendees關聯
	 * 
	 * @param tagId
	 * @return
	 */
	List<AttendeesTag> getAttendeesTagByTagId(Long tagId);

	/**
	 * 為一個tag和attendees新增關聯
	 * 
	 * @param attendeesTag
	 */
	void addAttendeesTag(AttendeesTag attendeesTag);

	/**
	 * 移除此 tag 與多位 attendees 關聯
	 * 
	 * @param tagId
	 * @param attendeessToRemove
	 */
	void removeTagRelationsForAttendeess(Long tagId, Set<Long> attendeessToRemove);
}
