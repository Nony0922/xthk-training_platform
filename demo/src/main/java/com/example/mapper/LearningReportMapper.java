package com.example.mapper;

import com.example.entity.LearningReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LearningReportMapper {
    List<LearningReport> findAll();

    List<LearningReport> findByCreator(@Param("creatorRole") String creatorRole,
                                       @Param("creatorId") Integer creatorId);

    List<LearningReport> findVisibleForParent(@Param("parentId") Integer parentId);

    LearningReport findById(@Param("id") Integer id);

    int insert(LearningReport report);

    int deleteById(@Param("id") Integer id);
}
