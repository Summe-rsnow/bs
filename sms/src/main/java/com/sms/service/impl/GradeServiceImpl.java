package com.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sms.common.BaseContext;
import com.sms.common.Result;
import com.sms.dto.GradeEditDto;
import com.sms.dto.GradeSelectDto;
import com.sms.entity.Course;
import com.sms.entity.Grade;
import com.sms.entity.User;
import com.sms.entity.VisualizationData;
import com.sms.mapper.CourseMapper;
import com.sms.mapper.GradeMapper;
import com.sms.mapper.UserMapper;
import com.sms.service.GradeService;
import com.sms.vo.GradeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author sssnow
 */
@Service
@Slf4j
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade> implements GradeService {

    @Resource
    GradeMapper gradeMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    CourseMapper courseMapper;

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

    @Override
    public List<VisualizationData> gradeSelfRanking(Long id) {
        return gradeMapper.gradeSelfRanking(id);
    }

    @Override
    public List<VisualizationData> passRanking(Long id, Integer flag) {
        return gradeMapper.passRanking(id, flag);
    }

    @Override
    public Result<String> addGrade(List<Grade> grades) {
        Long id = BaseContext.getCurrentId();
        for (Grade grade : grades) {
            grade.setTeacherId(id);
            Course course = courseMapper.selectById(grade.getCourseId());
            if (course == null || !course.getTeacherId().equals(grade.getTeacherId())) {
                return Result.error("课程id不存在或创建的成绩不为教师自己所授课程的成绩");
            }
            LambdaUpdateWrapper<Grade> gradeWrapper = new LambdaUpdateWrapper<>();
            gradeWrapper.eq(Grade::getStudentId, grade.getStudentId());
            gradeWrapper.eq(Grade::getCourseId, grade.getCourseId());
            Grade one = getOne(gradeWrapper);
            if (one != null) {
                return Result.error("该生的改门课程已经有成绩了，请勿重复添加");
            }
            User user = userMapper.selectById(grade.getStudentId());
            if (user == null) {
                return Result.error("该学生id不存在，请重试");
            }
        }
        saveBatch(grades);
        return Result.success("添加成功");
    }
}
