package tw.com.ticbcs.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import tw.com.ticbcs.pojo.entity.CheckinRecord;

/**
 * <p>
 * 簽到退紀錄 Mapper 接口
 * </p>
 *
 * @author Joey
 * @since 2025-05-07
 */
public interface CheckinRecordMapper extends BaseMapper<CheckinRecord> {

	// 因為是For Excel 匯出使用，所以按照與會者排序
	@Select("SELECT * FROM checkin_record WHERE is_deleted = 0 ORDER BY attendees_id")
	List<CheckinRecord> selectCheckinRecords();
}
