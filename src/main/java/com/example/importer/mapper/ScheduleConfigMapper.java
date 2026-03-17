package com.example.importer.mapper;

import com.example.importer.domain.ScheduleConfig;

import java.util.List;

public interface ScheduleConfigMapper {

    List<ScheduleConfig> selectEnabledSchedules();
}
