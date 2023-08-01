package com.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sms.common.Result;
import com.sms.dto.GradeEditDto;
import com.sms.dto.GradeSelectDto;
import com.sms.entity.Grade;
import com.sms.mapper.GradeMapper;
import com.sms.service.GradeService;
import com.sms.vo.GradeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade> implements GradeService {

    @Resource
    GradeMapper gradeMapper;

    @Override
    public Result<String> edit(GradeEditDto gradeEditDto) {
        log.info("修改成绩信息:{}", gradeEditDto);
        Grade grade = new Grade();
        BeanUtils.copyProperties(gradeEditDto, grade);
        LambdaUpdateWrapper<Grade> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Grade::getId, gradeEditDto.getId());
        update(grade, wrapper);
        return Result.success("修改成功");
    }

    @Override
    public Page<GradeVo> getVoPageByStudentId(Long id, Integer page, Integer pagesize, GradeSelectDto gradeSelectDto) {
        Page<GradeVo> gradePage = new Page<>(page, pagesize);
        return gradeMapper.selectStudentPage(gradePage, id, gradeSelectDto);
    }

    @Override
    public Page<GradeVo> getVoPageByTeacherId(Long id, Integer page, Integer pagesize, GradeSelectDto gradeSelectDto) {
        Page<GradeVo> gradeVopage = new Page<>(page, pagesize);
        return gradeMapper.selectTeacherPage(gradeVopage, id, gradeSelectDto);

    }
}
