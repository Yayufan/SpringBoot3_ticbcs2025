package tw.com.ticbcs.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tw.com.ticbcs.convert.AttendeesConvert;
import tw.com.ticbcs.convert.TagConvert;
import tw.com.ticbcs.mapper.AttendeesMapper;
import tw.com.ticbcs.mapper.MemberMapper;
import tw.com.ticbcs.pojo.DTO.SendEmailDTO;
import tw.com.ticbcs.pojo.DTO.addEntityDTO.AddAttendeesDTO;
import tw.com.ticbcs.pojo.DTO.addEntityDTO.AddTagDTO;
import tw.com.ticbcs.pojo.DTO.putEntityDTO.PutAttendeesDTO;
import tw.com.ticbcs.pojo.VO.AttendeesTagVO;
import tw.com.ticbcs.pojo.VO.AttendeesVO;
import tw.com.ticbcs.pojo.entity.Attendees;
import tw.com.ticbcs.pojo.entity.AttendeesTag;
import tw.com.ticbcs.pojo.entity.Member;
import tw.com.ticbcs.pojo.entity.Tag;
import tw.com.ticbcs.service.AttendeesService;
import tw.com.ticbcs.service.AttendeesTagService;
import tw.com.ticbcs.service.TagService;

/**
 * <p>
 * 參加者表，在註冊並實際繳完註冊費後，會進入這張表中，用做之後發送QRcdoe使用 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2025-04-24
 */
@Service
@RequiredArgsConstructor
public class AttendeesServiceImpl extends ServiceImpl<AttendeesMapper, Attendees> implements AttendeesService {

	private final MemberMapper memberMapper;

	private final AttendeesConvert attendeesConvert;

	private final TagService tagService;
	private final TagConvert tagConvert;

	private final AttendeesTagService attendeesTagService;

	@Qualifier("businessRedissonClient")
	private final RedissonClient redissonClient;

	@Override
	public AttendeesVO getAttendees(Long id) {
		// 先查詢到與會者自己的紀錄
		Attendees attendees = baseMapper.selectById(id);

		// 從attendees的 memberId中找到與會者的基本資料
		LambdaQueryWrapper<Member> memberWrapper = new LambdaQueryWrapper<>();
		memberWrapper.eq(Member::getMemberId, attendees.getMemberId());
		Member member = memberMapper.selectOne(memberWrapper);

		return null;
	}

	@Override
	public List<AttendeesVO> getAttendeesList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPage<AttendeesVO> getAttendeesPage(Page<Attendees> page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addAttendees(AddAttendeesDTO addAttendees) {
		// TODO Auto-generated method stub

	}

	@Transactional
	@Override
	public void addAfterPayment(AddAttendeesDTO addAttendees) {

		Attendees attendees = attendeesConvert.addDTOToEntity(addAttendees);
		RLock lock = redissonClient.getLock("attendee:sequence_lock");
		boolean isLocked = false;

		try {
			// 10秒鐘內不斷嘗試獲取鎖，20秒後必定釋放鎖
			isLocked = lock.tryLock(10, 20, TimeUnit.SECONDS);

			if (isLocked) {
				// 鎖內查一次最大 sequence_no
				Integer lockedMax = baseMapper.selectMaxSequenceNo();
				int nextSeq = (lockedMax != null) ? lockedMax + 1 : 1;

				// 如果 設定城當前最大sequence_no
				attendees.setSequenceNo(nextSeq);
				baseMapper.insert(attendees);

				//每200名與會者(Attendees)設置一個tag, A-group-01, M-group-02(補零兩位數)
				String baseTagName = "A-group-%02d";
				// 分組數量
				Integer groupSize = 200;
				// groupIndex組別索引
				Integer groupIndex;

				//當前數量，上面已經新增過至少一人，不可能為0
				Long currentCount = baseMapper.selectCount(null);

				// 2. 計算組別 (向上取整，例如 201人 → 第2組)
				groupIndex = (int) Math.ceil(currentCount / (double) groupSize);

				// 3. 生成 Tag 名稱 (補零兩位數)
				String tagName = String.format(baseTagName, groupIndex);
				String tagType = "attendees";

				// 4. 查詢是否已有該 Tag
				Tag existingTag = tagService.getTagByTypeAndName(tagType, tagName);

				// 5. 如果沒有就創建 Tag
				if (existingTag == null) {
					AddTagDTO addTagDTO = new AddTagDTO();
					addTagDTO.setType(tagType);
					addTagDTO.setName(tagName);
					addTagDTO.setDescription("會員分組標籤 (第 " + groupIndex + " 組)");
					addTagDTO.setStatus(0);
					String adjustColor = tagService.adjustColor("#001F54", groupIndex, 5);
					addTagDTO.setColor(adjustColor);
					Long insertTagId = tagService.insertTag(addTagDTO);
					Tag currentTag = tagConvert.addDTOToEntity(addTagDTO);
					currentTag.setTagId(insertTagId);
					existingTag = currentTag;
				}

				// 6.透過tagId 去 關聯表 進行關聯新增
				AttendeesTag attendeesTag = new AttendeesTag();
				attendeesTag.setAttendeesId(attendees.getAttendeesId());
				attendeesTag.setTagId(existingTag.getTagId());
				attendeesTagService.addAttendeesTag(attendeesTag);

			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (isLocked) {
				lock.unlock();
			}

		}

	}

	@Override
	public void updateAttendees(PutAttendeesDTO putAttendeesDTO) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAttendees(Long attendeesId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void batchDeleteAttendees(List<Long> attendeesIds) {
		// TODO Auto-generated method stub

	}

	@Override
	public void downloadExcel(HttpServletResponse response) throws UnsupportedEncodingException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public AttendeesTagVO getAttendeesTagVO(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPage<AttendeesTagVO> getAttendeesTagVOPage(Page<Attendees> pageInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPage<AttendeesTagVO> getAttendeesTagVOPageByQuery(Page<Attendees> pageInfo, String queryText) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void assignTagToAttendees(List<Long> targetTagIdList, Long memberId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendEmailToAttendeess(List<Long> tagIdList, SendEmailDTO sendEmailDTO) {
		// TODO Auto-generated method stub

	}

}
