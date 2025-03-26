package tw.com.ticbcs.system.convert;

import org.mapstruct.Mapper;

import tw.com.ticbcs.system.pojo.VO.SysUserVO;
import tw.com.ticbcs.system.pojo.entity.SysUser;

@Mapper(componentModel = "spring")
public interface SysUserConvert {

	//最後返回為SysUserVo對象, 方法名為entityToVO, 參數為SysUser對象
	SysUserVO entityToVO(SysUser sysUser);
	
}
