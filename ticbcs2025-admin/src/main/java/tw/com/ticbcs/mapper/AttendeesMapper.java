package tw.com.ticbcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import tw.com.ticbcs.pojo.entity.Attendees;

/**
 * <p>
 * 參加者表，在註冊並實際繳完註冊費後，會進入這張表中，用做之後發送QRcdoe使用 Mapper 接口
 * </p>
 *
 * @author Joey
 * @since 2025-04-24
 */
public interface AttendeesMapper extends BaseMapper<Attendees> {

}
