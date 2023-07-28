package com.sms.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sms.entity.Grade;
import com.sms.mapper.GradeMapper;
import com.sms.service.GradeService;
import com.sms.vo.GradeVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade> implements GradeService {

    @Resource
    GradeMapper gradeMapper;

    @Override
    public Page<GradeVo> getVoPageByStudentId(Long id, Integer page, Integer pagesize) {
        Page<GradeVo> gradePage = new Page<>(page, pagesize);
        return gradeMapper.selectStudentPage(gradePage, id);
    }

    @Override
    public Page<GradeVo> getVoPageByTeacherId(Long id, Integer page, Integer pagesize) {
        Page<GradeVo> gradeVOpage = new Page<>(page, pagesize);
        return gradeMapper.selectTeacherPage(gradeVOpage, id);

    }
}
