package com.sms.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sms.dto.UserSelectDto;
import com.sms.entity.User;
import com.sms.vo.UserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    Page<UserVo> selectUserVoPage(@Param("page") Page<UserVo> page, @Param("userSelectDto")UserSelectDto userSelectDto);
}
