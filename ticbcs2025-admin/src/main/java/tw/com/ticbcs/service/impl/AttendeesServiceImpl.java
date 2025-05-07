package tw.com.ticbcs.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;
import tw.com.ticbcs.convert.AttendeesConvert;
import tw.com.ticbcs.mapper.AttendeesMapper;
import tw.com.ticbcs.pojo.DTO.addEntityDTO.AddAttendeesDTO;
import tw.com.ticbcs.pojo.VO.AttendeesVO;
import tw.com.ticbcs.pojo.entity.Attendees;
import tw.com.ticbcs.service.AttendeesService;

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

	private final AttendeesConvert attendeesConvert;

	@Qualifier("businessRedissonClient")
	private final RedissonClient redissonClient;

	@Override
	public AttendeesVO getAttendees(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AttendeesVO> getAllAttendees() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPage<AttendeesVO> getAllAttendees(Page<Attendees> page) {
		// TODO Auto-generated method stub
		return null;
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
	public void addAttendees() {
		// TODO Auto-generated method stub

	}

}
