package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.Activity.ActivityResponseDto;
import com.example.WorkWite_Repo_BE.dtos.Activity.CreatAvtivityRequestDto;
import com.example.WorkWite_Repo_BE.entities.Activity;
import com.example.WorkWite_Repo_BE.entities.Resume;
import com.example.WorkWite_Repo_BE.repositories.ActivityJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.ResumeJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {
    private final ActivityJpaRepository activityJpaRepository;
    private final ResumeJpaRepository resumeJpaRepository;

    public ActivityService(ActivityJpaRepository activityJpaRepository, ResumeJpaRepository resumeJpaRepository) {
        this.activityJpaRepository = activityJpaRepository;
        this.resumeJpaRepository = resumeJpaRepository;
    }

    // convert activity thanh ActivityReponseDto
    public ActivityResponseDto convertDto (Activity activity){
        return new ActivityResponseDto(
                activity.getActivityName(),
                activity.getRole(),
                activity.getStartYear(),
                activity.getEndYear()
        );
    }

    // creat
    public ActivityResponseDto createActivity(CreatAvtivityRequestDto  createAwardDto, Long resumeId) {
        Resume resume = resumeJpaRepository.findById(resumeId).orElse(null);
        Activity activity = new Activity();
        activity.setActivityName(createAwardDto.getActivityName());
        activity.setRole(createAwardDto.getRole());
        activity.setStartYear(createAwardDto.getStartYear());
        activity.setEndYear(createAwardDto.getEndYear());
        activity.setResume(resume);

        Activity activityAdd = activityJpaRepository.save(activity);
        return convertDto(activityAdd);
    }

    // Get all activities by resumeId
    public List<ActivityResponseDto> getAllActivitiesByResumeId(Long resumeId) {
        var activities = activityJpaRepository.findByResumeId(resumeId);
        return activities.stream().map(this::convertDto).collect(java.util.stream.Collectors.toList());
    }

    // Get activity by id
    public ActivityResponseDto getActivityById(Long id) {
        Activity activity = activityJpaRepository.findById(id).orElse(null);
        if (activity == null) return null;
        return convertDto(activity);
    }

    // Delete activity by id
    public boolean deleteActivity(Long id) {
        if (!activityJpaRepository.existsById(id)) return false;
        activityJpaRepository.deleteById(id);
        return true;
    }
}
