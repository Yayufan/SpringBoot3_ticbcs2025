package tw.com.ticbcs.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import tw.com.ticbcs.mapper.AttendeesTagMapper;
import tw.com.ticbcs.pojo.entity.AttendeesTag;
import tw.com.ticbcs.service.AttendeesTagService;

/**
 * <p>
 * 與會者 與 標籤 的關聯表 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2025-05-07
 */
@Service
public class AttendeesTagServiceImpl extends ServiceImpl<AttendeesTagMapper, AttendeesTag>
		implements AttendeesTagService {

	@Override
	public List<AttendeesTag> getAttendeesTagByAttendeesId(Long attendeesId) {
		LambdaQueryWrapper<AttendeesTag> currentQueryWrapper = new LambdaQueryWrapper<>();
		currentQueryWrapper.eq(AttendeesTag::getAttendeesId, attendeesId);
		List<AttendeesTag> attendeesTagList = baseMapper.selectList(currentQueryWrapper);

		return attendeesTagList;
	}

	@Override
	public List<AttendeesTag> getAttendeesTagByTagId(Long tagId) {
		LambdaQueryWrapper<AttendeesTag> currentQueryWrapper = new LambdaQueryWrapper<>();
		currentQueryWrapper.eq(AttendeesTag::getTagId, tagId);
		List<AttendeesTag> attendeesTagList = baseMapper.selectList(currentQueryWrapper);

		return attendeesTagList;
	}

	@Override
	public List<AttendeesTag> getAttendeesTagByAttendeesIds(Collection<Long> attendeesIds) {
		LambdaQueryWrapper<AttendeesTag> currentQueryWrapper = new LambdaQueryWrapper<>();
		currentQueryWrapper.eq(AttendeesTag::getAttendeesTagId, attendeesIds);
		List<AttendeesTag> attendeesTagList = baseMapper.selectList(currentQueryWrapper);

		return attendeesTagList;
	}

	@Override
	public List<AttendeesTag> getAttendeesTagByTagIds(Collection<Long> tagIds) {
		LambdaQueryWrapper<AttendeesTag> attendeesTagWrapper = new LambdaQueryWrapper<>();
		attendeesTagWrapper.in(AttendeesTag::getTagId, tagIds);
		List<AttendeesTag> attendeesTagList = baseMapper.selectList(attendeesTagWrapper);

		return attendeesTagList;
	}

	@Override
	public void addAttendeesTag(AttendeesTag attendeesTag) {
		baseMapper.insert(attendeesTag);

	}

	@Override
	public void removeAttendeesFromTag(Long tagId, Set<Long> attendeessToRemove) {
		LambdaQueryWrapper<AttendeesTag> deleteAttendeesTagWrapper = new LambdaQueryWrapper<>();
		deleteAttendeesTagWrapper.eq(AttendeesTag::getTagId, tagId)
				.in(AttendeesTag::getAttendeesId, attendeessToRemove);
		baseMapper.delete(deleteAttendeesTagWrapper);

	}

	@Override
	public void removeTagsFromAttendee(Long attendeesId, Set<Long> tagsToRemove) {
		LambdaQueryWrapper<AttendeesTag> deleteAttendeesTagWrapper = new LambdaQueryWrapper<>();
		deleteAttendeesTagWrapper.eq(AttendeesTag::getAttendeesId, attendeesId)
				.in(AttendeesTag::getTagId, tagsToRemove);
		baseMapper.delete(deleteAttendeesTagWrapper);
		
	}

}
