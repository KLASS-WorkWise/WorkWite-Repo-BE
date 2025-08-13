package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.Activity.ActivityResponseDto;
import com.example.WorkWite_Repo_BE.dtos.Activity.CreatAvtivityRequestDto;
import com.example.WorkWite_Repo_BE.entities.Activity;
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
    public ResponseEntity<ActivityResponseDto> creatActivity (@PathVariable Long resumeId, @RequestBody CreatAvtivityRequestDto creatAvtivityRequestDto){
        ActivityResponseDto activityResponseDto = activityService.createActivity(creatAvtivityRequestDto,resumeId);
        return new ResponseEntity<>(activityResponseDto, HttpStatus.OK);
    }

//    @GetMapping("/{resumeId}")
//    public ResponseEntity<List<ActivityResponseDto>> getResumeById (@PathVariable Long resumeId){
//        List<ActivityResponseDto> getActivity = activityService.getActivityById(resumeId);
//        return new ResponseEntity<>(getActivity, HttpStatus.OK);
//    }

}
