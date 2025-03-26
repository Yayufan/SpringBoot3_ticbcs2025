package tw.com.ticbcs.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import tw.com.ticbcs.mapper.PublishFileMapper;
import tw.com.ticbcs.pojo.entity.PublishFile;
import tw.com.ticbcs.service.PublishFileService;

import org.springframework.stereotype.Service;

/**
 * <p>
 * 發佈檔案表 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2025-02-05
 */
@Service
public class PublishFileServiceImpl extends ServiceImpl<PublishFileMapper, PublishFile> implements PublishFileService {

}
