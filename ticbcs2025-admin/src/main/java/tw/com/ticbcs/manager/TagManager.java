package tw.com.ticbcs.manager;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import lombok.RequiredArgsConstructor;
import tw.com.ticbcs.mapper.TagMapper;
import tw.com.ticbcs.pojo.entity.Tag;
import tw.com.ticbcs.utils.TagColorUtil;

@Component
@RequiredArgsConstructor
public class TagManager {

	private final TagMapper tagMapper;

	public Tag getTagByTypeAndName(String type, String name) {
		LambdaQueryWrapper<Tag> tagQueryWrapper = new LambdaQueryWrapper<>();
		tagQueryWrapper.eq(Tag::getType, type).eq(Tag::getName, name);
		Tag tag = tagMapper.selectOne(tagQueryWrapper);
		return tag;
	}

	public Tag createTag(String type, String name, String description, String color) {
		Tag tag = new Tag();
		tag.setType(type);
		tag.setName(name);
		tag.setDescription(description);
		tag.setStatus(0);
		tag.setColor(color);
		tagMapper.insert(tag);
		return tag;
	}

	/**
	 * 獲取或創建MemberGroupTag
	 * 
	 * @param groupIndex 分組的索引,需 >= 1
	 * @return
	 */
	public Tag getOrCreateMemberGroupTag(int groupIndex) {
		String tagType = "member";
		String tagName = String.format("M-group-%02d", groupIndex);
		Tag tag = this.getTagByTypeAndName(tagType, tagName);

		if (tag != null)
			return tag;

		String color = TagColorUtil.adjustColor("#4A7056", groupIndex, 5);
		String desc = "會員分組標籤 (第 " + groupIndex + " 組)";
		return this.createTag(tagType, tagName, desc, color);
	}

}
