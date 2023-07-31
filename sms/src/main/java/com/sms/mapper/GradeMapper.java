package com.sms.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sms.dto.GradeSelectDto;
import com.sms.entity.Grade;
import com.sms.vo.GradeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GradeMapper extends BaseMapper<Grade> {
    Page<GradeVo> selectTeacherPage(@Param("page") Page<GradeVo> page, @Param("id") Long id, @Param("gradeSelectDto") GradeSelectDto gradeSelectDto);

    Page<GradeVo> selectStudentPage(@Param("page") Page<GradeVo> gradePage, @Param("id") Long id);
}
