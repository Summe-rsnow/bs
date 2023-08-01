package com.sms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sms.common.Result;
import com.sms.dto.GradeEditDto;
import com.sms.dto.GradeSelectDto;
import com.sms.entity.Grade;
import com.sms.vo.GradeVo;
import com.sms.vo.UserVo;

public interface GradeService extends IService<Grade> {

    Result<UserVo> edit(GradeEditDto gradeEditDto);

    //根据学生id查询成绩信息
    Page<GradeVo> getVoPageByStudentId(Long id, Integer page, Integer pagesize, GradeSelectDto gradeSelectDto);

    //根据教师id查询成绩信息
    Page<GradeVo> getVoPageByTeacherId(Long id, Integer page, Integer pagesize, GradeSelectDto gradeSelectDto);
}
