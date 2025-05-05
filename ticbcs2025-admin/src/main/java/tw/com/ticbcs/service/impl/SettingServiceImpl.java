package tw.com.ticbcs.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;
import tw.com.ticbcs.convert.SettingConvert;
import tw.com.ticbcs.mapper.SettingMapper;
import tw.com.ticbcs.pojo.DTO.addEntityDTO.AddSettingDTO;
import tw.com.ticbcs.pojo.DTO.putEntityDTO.PutSettingDTO;
import tw.com.ticbcs.pojo.entity.Setting;
import tw.com.ticbcs.service.SettingService;

@Service
@RequiredArgsConstructor
public class SettingServiceImpl extends ServiceImpl<SettingMapper, Setting> implements SettingService {
	private final SettingConvert settingConvert;

	@Override
	public Setting getSetting(Long settingId) {
		Setting setting = baseMapper.selectById(settingId);
		return setting;
	}

	@Override
	public List<Setting> getSettingList() {
		List<Setting> settingList = baseMapper.selectList(null);
		return settingList;
	}

	@Override
	public IPage<Setting> getSettingPage(Page<Setting> page) {
		Page<Setting> settingPage = baseMapper.selectPage(page, null);
		return settingPage;
	}

	@Override
	public void addSetting(AddSettingDTO addSettingDTO) {
		Setting setting = settingConvert.addDTOToEntity(addSettingDTO);
		baseMapper.insert(setting);
	}

	@Override
	public void updateSetting(PutSettingDTO putSettingDTO) {
		Setting setting = settingConvert.putDTOToEntity(putSettingDTO);
		baseMapper.updateById(setting);
	}

	@Override
	public void deleteSetting(Long settingId) {
		baseMapper.deleteById(settingId);

	}

	@Override
	public void deleteSettingList(List<Long> settingIds) {
		baseMapper.deleteBatchIds(settingIds);
	}

}
