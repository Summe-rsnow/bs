package com.sms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sms.common.Result;
import com.sms.dto.GradeEditDto;
import com.sms.dto.GradeSelectDto;
import com.sms.entity.Grade;
import com.sms.entity.VisualizationData;
import com.sms.vo.GradeVo;

import java.util.List;

/**
 * @author sssnow
 */
public interface GradeService extends IService<Grade> {

    Result<String> edit(GradeEditDto gradeEditDto);

    //根据学生id查询成绩信息
    Page<GradeVo> getVoPageByStudentId(Long id, Integer page, Integer pagesize, GradeSelectDto gradeSelectDto);

    //根据教师id查询成绩信息
    Page<GradeVo> getVoPageByTeacherId(Long id, Integer page, Integer pagesize, GradeSelectDto gradeSelectDto);

    List<VisualizationData> gradeSelfRanking(Long id);

    List<VisualizationData> passRanking(Long id, Integer flag);

    Result<String> addGrade(List<Grade> grades);
}
