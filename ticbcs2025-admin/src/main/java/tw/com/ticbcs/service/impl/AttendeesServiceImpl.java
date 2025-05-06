package tw.com.ticbcs.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

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

	@Override
	public void addAfterPayment(AddAttendeesDTO addAttendees) {
		Attendees attendees = attendeesConvert.addDTOToEntity(addAttendees);
		baseMapper.insert(attendees);
	}

	@Override
	public void addAttendees() {
		// TODO Auto-generated method stub
		
	}

	
}
