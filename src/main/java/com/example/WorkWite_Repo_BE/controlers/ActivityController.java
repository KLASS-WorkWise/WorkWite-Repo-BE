package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.Activity.ActivityResponseDto;
import com.example.WorkWite_Repo_BE.dtos.Activity.CreatAvtivityRequestDto;
import com.example.WorkWite_Repo_BE.dtos.Activity.UpdateActivityRequestDto;
import com.example.WorkWite_Repo_BE.services.ActivityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {
    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping("/{resumeId}")
    public ResponseEntity<ActivityResponseDto> createActivity(@PathVariable Long resumeId, @RequestBody CreatAvtivityRequestDto requestDto) {
        ActivityResponseDto activityResponseDto = activityService.createActivity(requestDto, resumeId);
        return new ResponseEntity<>(activityResponseDto, HttpStatus.CREATED);
    }

    @GetMapping("/resume/{resumeId}")
    public ResponseEntity<List<ActivityResponseDto>> getAllActivitiesByResumeId(@PathVariable Long resumeId) {
        List<ActivityResponseDto> activities = activityService.getAllActivitiesByResumeId(resumeId);
        return new ResponseEntity<>(activities, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponseDto> getActivityById(@PathVariable Long id) {
        ActivityResponseDto activity = activityService.getActivityById(id);
        if (activity == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(activity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        boolean deleted = activityService.deleteActivity(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
