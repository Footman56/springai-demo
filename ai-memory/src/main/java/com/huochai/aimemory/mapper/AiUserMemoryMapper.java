package com.huochai.aimemory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huochai.aimemory.entity.AiUserMemory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户记忆 Mapper 接口
 *
 * @author peilizhi
 * @date 2026/3/22
 */
public interface AiUserMemoryMapper extends BaseMapper<AiUserMemory> {

    /**
     * 查询用户记忆，按重要性降序排序
     */
    List<AiUserMemory> selectByUserIdOrderByImportance(@Param("userId") Long userId);

    /**
     * 更新重要性分数
     */
    int updateImportance(@Param("id") Long id, @Param("importance") Double importance);
}
