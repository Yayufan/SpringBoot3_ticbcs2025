package tw.com.ticbcs.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tw.com.ticbcs.convert.AttendeesConvert;
import tw.com.ticbcs.convert.TagConvert;
import tw.com.ticbcs.exception.EmailException;
import tw.com.ticbcs.mapper.AttendeesMapper;
import tw.com.ticbcs.mapper.AttendeesTagMapper;
import tw.com.ticbcs.mapper.MemberMapper;
import tw.com.ticbcs.mapper.TagMapper;
import tw.com.ticbcs.pojo.BO.MemberExcelRaw;
import tw.com.ticbcs.pojo.DTO.SendEmailDTO;
import tw.com.ticbcs.pojo.DTO.addEntityDTO.AddAttendeesDTO;
import tw.com.ticbcs.pojo.DTO.addEntityDTO.AddTagDTO;
import tw.com.ticbcs.pojo.VO.AttendeesTagVO;
import tw.com.ticbcs.pojo.VO.AttendeesVO;
import tw.com.ticbcs.pojo.entity.Attendees;
import tw.com.ticbcs.pojo.entity.AttendeesTag;
import tw.com.ticbcs.pojo.entity.Member;
import tw.com.ticbcs.pojo.entity.Orders;
import tw.com.ticbcs.pojo.entity.Tag;
import tw.com.ticbcs.pojo.excelPojo.AttendeesExcel;
import tw.com.ticbcs.pojo.excelPojo.MemberExcel;
import tw.com.ticbcs.service.AsyncService;
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

	private static final String DAILY_EMAIL_QUOTA_KEY = "email:dailyQuota";

	private final MemberMapper memberMapper;
	private final AttendeesConvert attendeesConvert;
	private final AttendeesTagService attendeesTagService;
	private final AttendeesTagMapper attendeesTagMapper;

	private final TagService tagService;
	private final TagConvert tagConvert;
	private final TagMapper tagMapper;

	private final AsyncService asyncService;

	@Qualifier("businessRedissonClient")
	private final RedissonClient redissonClient;

	@Override
	public AttendeesVO getAttendees(Long id) {
		// 先查詢到與會者自己的紀錄
		Attendees attendees = baseMapper.selectById(id);

		// 從attendees的 attendeesId中找到與會者的基本資料
		LambdaQueryWrapper<Member> attendeesWrapper = new LambdaQueryWrapper<>();
		attendeesWrapper.eq(Member::getMemberId, attendees.getMemberId());
		Member member = memberMapper.selectOne(attendeesWrapper);

		AttendeesVO attendeesVO = attendeesConvert.entityToVO(attendees);
		attendeesVO.setMember(member);

		return attendeesVO;
	}

	@Override
	public List<AttendeesVO> getAttendeesList() {
		List<Attendees> attendeesList = baseMapper.selectList(null);

		// 從attendees的 attendeesId中找到與會者的基本資料
		List<Member> memberList = memberMapper.selectList(null);
		Map<Long, Member> memberIdToMemberMap = memberList.stream()
				.collect(Collectors.toMap(Member::getMemberId, Function.identity()));

		List<AttendeesVO> attendeesVOList = attendeesList.stream().map(attendees -> {
			AttendeesVO vo = attendeesConvert.entityToVO(attendees);
			vo.setMember(memberIdToMemberMap.get(attendees.getMemberId()));
			return vo;
		}).collect(Collectors.toList());

		return attendeesVOList;
	}

	@Override
	public IPage<AttendeesVO> getAttendeesPage(Page<Attendees> page) {
		// 查詢attendees 分頁對象
		Page<Attendees> attendeesPage = baseMapper.selectPage(page, null);

		// 從attendees的 memberId 中找到與會者的基本資料
		List<Member> memberList = memberMapper.selectList(null);
		Map<Long, Member> memberIdToMemberMap = memberList.stream()
				.collect(Collectors.toMap(Member::getMemberId, Function.identity()));

		// 資料轉換成VO
		List<AttendeesVO> attendeesVOList = attendeesPage.getRecords().stream().map(attendees -> {
			AttendeesVO vo = attendeesConvert.entityToVO(attendees);
			vo.setMember(memberIdToMemberMap.get(attendees.getMemberId()));
			return vo;
		}).collect(Collectors.toList());

		// 封裝成VOpage
		Page<AttendeesVO> attendeesVOPage = new Page<>(attendeesPage.getCurrent(), attendeesPage.getSize(),
				attendeesPage.getTotal());
		attendeesVOPage.setRecords(attendeesVOList);

		return attendeesVOPage;
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
	public void deleteAttendees(Long attendeesId) {
		baseMapper.deleteById(attendeesId);
	}

	@Override
	public void batchDeleteAttendees(List<Long> attendeesIds) {
		for (Long attendeesId : attendeesIds) {
			this.deleteAttendees(attendeesId);
		}

	}

	@Override
	public void downloadExcel(HttpServletResponse response) throws UnsupportedEncodingException, IOException {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setCharacterEncoding("utf-8");
		// 这里URLEncoder.encode可以防止中文乱码 ， 和easyexcel没有关系
		String fileName = URLEncoder.encode("與會者名單", "UTF-8").replaceAll("\\+", "%20");
		response.setHeader("Content-disposition", "attachment;filename*=" + fileName + ".xlsx");

		// 查詢所有會員，用來填充與會者的基本資訊
		List<Member> memberList = memberMapper.selectMembers();
		// 訂單轉成一對一 Map，key為 memberId, value為訂單本身
		Map<Long, Member> memberIdToMemberMap = memberList.stream()
				.collect(Collectors.toMap(Member::getMemberId, Function.identity()));

		// 獲取所有與會者
		List<Attendees> attendeesList = baseMapper.selectAttendees();

		// 資料轉換成Excel
		List<AttendeesExcel> excelData = attendeesList.stream().map(attendees -> {
			AttendeesVO attendeesVO = attendeesConvert.entityToVO(attendees);
			attendeesVO.setMember(memberIdToMemberMap.get(attendees.getMemberId()));
			AttendeesExcel attendeesExcel = attendeesConvert.voToExcel(attendeesVO);
			return attendeesExcel;

		}).collect(Collectors.toList());

		EasyExcel.write(response.getOutputStream(), AttendeesExcel.class).sheet("與會者列表").doWrite(excelData);

	}

	@Override
	public AttendeesTagVO getAttendeesTagVO(Long attendeesId) {

		// 1.獲取attendees 資料並轉換成 attendeesTagVO
		Attendees attendees = baseMapper.selectById(attendeesId);
		AttendeesTagVO attendeesTagVO = attendeesConvert.entityToAttendeesTagVO(attendees);

		// 2.查詢attendees 的基本資料，並放入Member屬性
		LambdaQueryWrapper<Member> memberWrapper = new LambdaQueryWrapper<>();
		memberWrapper.eq(Member::getMemberId, attendees.getMemberId());
		Member member = memberMapper.selectOne(memberWrapper);
		attendeesTagVO.setMember(member);

		// 3.查詢該attendees所有關聯的tag
		LambdaQueryWrapper<AttendeesTag> attendeesTagWrapper = new LambdaQueryWrapper<>();
		attendeesTagWrapper.eq(AttendeesTag::getAttendeesId, attendeesId);
		List<AttendeesTag> attendeesTagList = attendeesTagMapper.selectList(attendeesTagWrapper);

		// 如果沒有任何關聯,就可以直接返回了
		if (attendeesTagList.isEmpty()) {
			return attendeesTagVO;
		}

		// 4.獲取到所有attendeesTag的關聯關係後，提取出tagIds
		List<Long> tagIds = attendeesTagList.stream()
				.map(attendeesTag -> attendeesTag.getTagId())
				.collect(Collectors.toList());

		// 5.去Tag表中查詢實際的Tag資料，並轉換成Set集合
		LambdaQueryWrapper<Tag> tagWrapper = new LambdaQueryWrapper<>();
		tagWrapper.in(Tag::getTagId, tagIds);
		List<Tag> tagList = tagMapper.selectList(tagWrapper);
		Set<Tag> tagSet = new HashSet<>(tagList);

		// 5.最後填入attendeesTagVO對象並返回
		attendeesTagVO.setTagSet(tagSet);
		return attendeesTagVO;

	}

	@Override
	public IPage<AttendeesTagVO> getAttendeesTagVOPage(Page<Attendees> pageInfo) {

		IPage<AttendeesTagVO> voPage;

		// 1.以attendees當作基底查詢,越新的擺越前面
		LambdaQueryWrapper<Attendees> attendeesWrapper = new LambdaQueryWrapper<>();
		attendeesWrapper.orderByDesc(Attendees::getAttendeesId);

		// 2.查詢 AttendeesPage (分頁)
		IPage<Attendees> attendeesPage = baseMapper.selectPage(pageInfo, attendeesWrapper);

		// 3. 獲取所有 attendeesId 列表，
		List<Long> attendeesIds = attendeesPage.getRecords()
				.stream()
				.map(Attendees::getAttendeesId)
				.collect(Collectors.toList());

		if (attendeesIds.isEmpty()) {
			System.out.println("沒有與會者,所以直接返回");

			voPage = new Page<>(pageInfo.getCurrent(), pageInfo.getSize(), attendeesPage.getTotal());
			voPage.setRecords(null);

			return voPage;
		}

		// 4. 批量查詢 AttendeesTag 關係表，獲取 attendeesId 对应的 tagId
		List<AttendeesTag> attendeesTagList = attendeesTagMapper
				.selectList(new LambdaQueryWrapper<AttendeesTag>().in(AttendeesTag::getAttendeesId, attendeesIds));

		// 5. 將 attendeesId 對應的 tagId 歸類，key 為attendeesId , value 為 tagIdList
		Map<Long, List<Long>> attendeesTagMap = attendeesTagList.stream()
				.collect(Collectors.groupingBy(AttendeesTag::getAttendeesId,
						Collectors.mapping(AttendeesTag::getTagId, Collectors.toList())));

		// 6. 獲取所有 tagId 列表
		List<Long> tagIds = attendeesTagList.stream()
				.map(AttendeesTag::getTagId)
				.distinct()
				.collect(Collectors.toList());

		// 7. 批量查詢所有的 Tag，如果關聯的tagIds為空, 那就不用查了，直接返回
		if (tagIds.isEmpty()) {
			System.out.println("沒有任何tag關聯,所以直接返回");
			List<AttendeesTagVO> attendeesTagVOList = attendeesPage.getRecords().stream().map(attendees -> {
				AttendeesTagVO vo = attendeesConvert.entityToAttendeesTagVO(attendees);
				vo.setTagSet(new HashSet<>());

				// 看有沒有額外要補充的
				LambdaQueryWrapper<Member> memberWrapper = new LambdaQueryWrapper<>();
				memberWrapper.eq(Member::getMemberId, attendees.getMemberId());
				Member member = memberMapper.selectOne(memberWrapper);
				vo.setMember(member);

				return vo;
			}).collect(Collectors.toList());
			voPage = new Page<>(pageInfo.getCurrent(), pageInfo.getSize(), attendeesPage.getTotal());
			voPage.setRecords(attendeesTagVOList);

			return voPage;

		}

		List<Tag> tagList = tagMapper.selectList(new LambdaQueryWrapper<Tag>().in(Tag::getTagId, tagIds));

		// 8. 將 Tag 按 tagId 歸類
		Map<Long, Tag> tagMap = tagList.stream().collect(Collectors.toMap(Tag::getTagId, tag -> tag));

		// 9. 組裝 VO 數據
		List<AttendeesTagVO> voList = attendeesPage.getRecords().stream().map(attendees -> {
			AttendeesTagVO vo = attendeesConvert.entityToAttendeesTagVO(attendees);
			// 獲取attendees的基本資料
			LambdaQueryWrapper<Member> memberWrapper = new LambdaQueryWrapper<>();
			memberWrapper.eq(Member::getMemberId, attendees.getMemberId());
			Member member = memberMapper.selectOne(memberWrapper);
			vo.setMember(member);

			// 獲取該 attendeesId 關聯的 tagId 列表
			List<Long> relatedTagIds = attendeesTagMap.getOrDefault(attendees.getAttendeesId(),
					Collections.emptyList());
			// 獲取所有對應的 Tag
			List<Tag> tags = relatedTagIds.stream()
					.map(tagMap::get)
					.filter(Objects::nonNull) // 避免空值
					.collect(Collectors.toList());
			Set<Tag> tagSet = new HashSet<>(tags);
			vo.setTagSet(tagSet);

			return vo;
		}).collect(Collectors.toList());

		// 10. 重新封装 VO 的分頁對象
		voPage = new Page<>(pageInfo.getCurrent(), pageInfo.getSize(), attendeesPage.getTotal());
		voPage.setRecords(voList);

		return voPage;

	}

	@Override
	public IPage<AttendeesTagVO> getAttendeesTagVOPageByQuery(Page<Attendees> pageInfo, String queryText) {

		IPage<AttendeesTagVO> voPage;

		// 1.因為能進與會者其實沒有單獨的資訊了，所以是查詢會員資訊，queryText都是member的資訊
		LambdaQueryWrapper<Member> memberWrapper = new LambdaQueryWrapper<>();

		// 當 queryText 不為空字串、空格字串、Null 時才加入篩選條件
		// 且attendeesIdsByStatus裡面元素不為空，則加入篩選條件
		memberWrapper.and(StringUtils.isNotBlank(queryText),
				wrapper -> wrapper.like(Member::getChineseName, queryText)
						.or()
						.like(Member::getFirstName, queryText)
						.or()
						.like(Member::getLastName, queryText)
						.or()
						.like(Member::getPhone, queryText)
						.or()
						.like(Member::getIdCard, queryText)
						.or()
						.like(Member::getEmail, queryText));

		//就算沒資料也是空數組
		List<Member> memberList = memberMapper.selectList(memberWrapper);

		// 2. 同時建立 memberId → Member 映射，並提取 memberIds
		Map<Long, Member> memberIdToMemberMap = new HashMap<>();
		List<Long> memberIds = new ArrayList<>();
		for (Member member : memberList) {
			memberIdToMemberMap.put(member.getMemberId(), member);
			memberIds.add(member.getMemberId());
		}

		// 3.如果memberIds為空，直接返回一個空Page<AttendeesTagVO>對象
		if (memberIds.isEmpty()) {
			// 直接return 空voPage對象
			voPage = new Page<>(pageInfo.getCurrent(), pageInfo.getSize(), 0);
			voPage.setRecords(null);
			return voPage;
		}

		// 4.如果不為空，則查詢出符合的attendees (分頁)
		LambdaQueryWrapper<Attendees> attendeesWrapper = new LambdaQueryWrapper<>();
		attendeesWrapper.in(Attendees::getMemberId, memberIds);
		Page<Attendees> attendeesPage = baseMapper.selectPage(pageInfo, attendeesWrapper);

		List<Long> attendeesIds = attendeesPage.getRecords()
				.stream()
				.map(Attendees::getAttendeesId)
				.collect(Collectors.toList());

		// 這邊attendeesIds不可能沒有元素, 因為attendeesId 和 memberId是 1:1關係
		// 5. 批量查詢 AttendeesTag 關係表，獲取 attendeesId 对应的 tagId
		List<AttendeesTag> attendeesTagList = attendeesTagMapper
				.selectList(new LambdaQueryWrapper<AttendeesTag>().in(AttendeesTag::getAttendeesId, attendeesIds));

		// 6. 將 attendeesId 對應的 tagId 歸類，key 為attendeesId , value 為 tagIdList
		Map<Long, List<Long>> attendeesTagMap = attendeesTagList.stream()
				.collect(Collectors.groupingBy(AttendeesTag::getAttendeesId,
						Collectors.mapping(AttendeesTag::getTagId, Collectors.toList())));

		// 7. 獲取所有 tagId 列表
		List<Long> tagIds = attendeesTagList.stream()
				.map(AttendeesTag::getTagId)
				.distinct()
				.collect(Collectors.toList());

		// 8. 批量查询所有的 Tag，如果關聯的tagIds為空, 那就不用查了，直接返回
		if (tagIds.isEmpty()) {
			System.out.println("沒有任何tag關聯,所以直接返回");
			List<AttendeesTagVO> attendeesTagVOList = attendeesPage.getRecords().stream().map(attendees -> {

				// 轉換成VO對象後，透過map映射找到Member
				AttendeesTagVO vo = attendeesConvert.entityToAttendeesTagVO(attendees);
				Member member = memberIdToMemberMap.get(attendees.getMemberId());
				// 組裝vo後返回
				vo.setMember(member);
				vo.setTagSet(new HashSet<>());
				return vo;
			}).collect(Collectors.toList());
			voPage = new Page<>(pageInfo.getCurrent(), pageInfo.getSize(), attendeesPage.getTotal());
			voPage.setRecords(attendeesTagVOList);
			return voPage;

		}

		// 定義tagList
		List<Tag> tagList;
		tagList = tagMapper.selectList(new LambdaQueryWrapper<Tag>().in(Tag::getTagId, tagIds));

		// 9. 將 Tag 按 tagId 歸類
		Map<Long, Tag> tagMap = tagList.stream().collect(Collectors.toMap(Tag::getTagId, tag -> tag));

		// 10. 組裝 VO 數據
		List<AttendeesTagVO> voList = attendeesPage.getRecords().stream().map(attendees -> {

			// 將查找到的Attendees,轉換成VO對象
			AttendeesTagVO vo = attendeesConvert.entityToAttendeesTagVO(attendees);
			// 透過 mapping 找到member, 並組裝進VO
			Member member = memberIdToMemberMap.get(attendees.getMemberId());
			vo.setMember(member);

			// 獲取該 attendeesId 關聯的 tagId 列表
			List<Long> relatedTagIds = attendeesTagMap.getOrDefault(attendees.getAttendeesId(),
					Collections.emptyList());

			// 獲取所有對應的 Tag
			Set<Tag> tagSet = relatedTagIds.stream()
					.map(tagMap::get)
					.filter(Objects::nonNull) // 避免空值
					.collect(Collectors.toSet());

			// 將 tagSet 放入VO中
			vo.setTagSet(tagSet);

			return vo;
		}).collect(Collectors.toList());

		// 10. 重新封装 VO 的分頁對象
		voPage = new Page<>(pageInfo.getCurrent(), pageInfo.getSize(), attendeesPage.getTotal());
		voPage.setRecords(voList);

		return voPage;

	}

	@Override
	@Transactional
	public void assignTagToAttendees(List<Long> targetTagIdList, Long attendeesId) {

		// 1. 查詢當前 attendees 的所有關聯 tag
		LambdaQueryWrapper<AttendeesTag> currentQueryWrapper = new LambdaQueryWrapper<>();
		currentQueryWrapper.eq(AttendeesTag::getAttendeesId, attendeesId);
		List<AttendeesTag> currentAttendeesTags = attendeesTagMapper.selectList(currentQueryWrapper);

		// 2. 提取當前關聯的 tagId Set
		Set<Long> currentTagIdSet = currentAttendeesTags.stream()
				.map(AttendeesTag::getTagId)
				.collect(Collectors.toSet());

		// 3. 對比目標 attendeesIdList 和當前 attendeesIdList
		Set<Long> targetTagIdSet = new HashSet<>(targetTagIdList);

		// 4. 找出需要 刪除 的關聯關係
		Set<Long> tagsToRemove = new HashSet<>(currentTagIdSet);
		// 差集：當前有但目標沒有
		tagsToRemove.removeAll(targetTagIdSet);

		// 5. 找出需要 新增 的關聯關係
		Set<Long> tagsToAdd = new HashSet<>(targetTagIdSet);
		// 差集：目標有但當前沒有
		tagsToAdd.removeAll(currentTagIdSet);

		// 6. 執行刪除操作，如果 需刪除集合 中不為空，則開始刪除
		if (!tagsToRemove.isEmpty()) {
			LambdaQueryWrapper<AttendeesTag> deleteAttendeesTagWrapper = new LambdaQueryWrapper<>();
			deleteAttendeesTagWrapper.eq(AttendeesTag::getAttendeesId, attendeesId)
					.in(AttendeesTag::getTagId, tagsToRemove);
			attendeesTagMapper.delete(deleteAttendeesTagWrapper);
		}

		// 7. 執行新增操作，如果 需新增集合 中不為空，則開始新增
		if (!tagsToAdd.isEmpty()) {
			List<AttendeesTag> newAttendeesTags = tagsToAdd.stream().map(tagId -> {
				AttendeesTag attendeesTag = new AttendeesTag();
				attendeesTag.setTagId(tagId);
				attendeesTag.setAttendeesId(attendeesId);
				return attendeesTag;
			}).collect(Collectors.toList());

			// 批量插入
			for (AttendeesTag attendeesTag : newAttendeesTags) {
				attendeesTagMapper.insert(attendeesTag);
			}
		}
	}

	@Override
	public void sendEmailToAttendeess(List<Long> tagIdList, SendEmailDTO sendEmailDTO) {

		//從Redis中查看本日信件餘額
		RAtomicLong quota = redissonClient.getAtomicLong(DAILY_EMAIL_QUOTA_KEY);

		long currentQuota = quota.get();

		// 如果信件額度 小於等於 0，直接返回錯誤不要寄信
		if (currentQuota <= 0) {
			throw new EmailException("今日寄信配額已用完");
		}

		//初始化 attendeesIdSet ，用於去重attendeesId
		Set<Long> attendeesIdSet = new HashSet<>();

		// 先判斷tagIdList是否為空數組 或者 null ，如果true 則是要寄給所有會員
		Boolean hasNoTag = tagIdList == null || tagIdList.isEmpty();

		//初始化要寄信的會員人數
		Long attendeesCount = 0L;

		if (hasNoTag) {
			attendeesCount = baseMapper.selectCount(null);
		} else {
			// 透過tag先找到符合的attendees關聯
			LambdaQueryWrapper<AttendeesTag> attendeesTagWrapper = new LambdaQueryWrapper<>();
			attendeesTagWrapper.in(AttendeesTag::getTagId, tagIdList);
			List<AttendeesTag> attendeesTagList = attendeesTagMapper.selectList(attendeesTagWrapper);

			// 從關聯中取出attendeesId ，使用Set去重複的會員，因為會員有可能有多個Tag
			attendeesIdSet = attendeesTagList.stream().map(AttendeesTag::getAttendeesId).collect(Collectors.toSet());

			if (attendeesIdSet.isEmpty()) {
				throw new EmailException("沒有符合資格的與會者");
			}

			// 如果attendeesIdSet 至少有一個，則開始搜尋Attendees
			LambdaQueryWrapper<Attendees> attendeesWrapper = new LambdaQueryWrapper<>();
			attendeesWrapper.in(Attendees::getAttendeesId, attendeesIdSet);
			attendeesCount = baseMapper.selectCount(attendeesWrapper);

		}

		//這邊都先排除沒信件額度，和沒有收信者的情況
		if (attendeesCount <= 0) {
			throw new EmailException("沒有符合資格的與會者");
		}

		if (currentQuota < attendeesCount) {
			throw new EmailException("本日寄信額度剩餘: " + currentQuota + "，無法寄送 " + attendeesCount + " 封信");
		}

		// 查收信者名單 + member
		List<AttendeesVO> attendeesVOList = buildAttendeesVOList(hasNoTag ? null : attendeesIdSet);

		//前面已排除null 和 0 的狀況，開 異步線程 直接開始遍歷寄信
		asyncService.batchSendEmailToAttendeess(attendeesVOList, sendEmailDTO);

		// 額度直接扣除 查詢到的會員數量
		// 避免多用戶操作時，明明已經達到寄信額度，但異步線程仍未扣除完成
		quota.addAndGet(-attendeesCount);

	}

	// 提取通用代碼製成private function 
	private List<AttendeesVO> buildAttendeesVOList(Set<Long> attendeesIdSet) {
		List<Attendees> attendeesList;
		if (attendeesIdSet == null || attendeesIdSet.isEmpty()) {
			attendeesList = baseMapper.selectList(null);
		} else {
			LambdaQueryWrapper<Attendees> attendeesWrapper = new LambdaQueryWrapper<>();
			attendeesWrapper.in(Attendees::getAttendeesId, attendeesIdSet);
			attendeesList = baseMapper.selectList(attendeesWrapper);
		}

		// 只查需要的 member
		Set<Long> memberIds = attendeesList.stream()
				.map(Attendees::getMemberId)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());

		List<Member> memberList = memberMapper.selectBatchIds(memberIds);
		Map<Long, Member> memberIdToMemberMap = memberList.stream()
				.collect(Collectors.toMap(Member::getMemberId, Function.identity()));

		// 組裝 VO
		return attendeesList.stream().map(attendees -> {
			AttendeesVO vo = attendeesConvert.entityToVO(attendees);
			vo.setMember(memberIdToMemberMap.get(attendees.getMemberId()));
			return vo;
		}).collect(Collectors.toList());
	}

}
