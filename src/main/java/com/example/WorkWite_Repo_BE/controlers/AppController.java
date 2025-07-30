package com.example.WorkWite_Repo_BE.controlers;


import com.example.WorkWite_Repo_BE.dtos.ApplicationsDto.AppResponseDto;
import com.example.WorkWite_Repo_BE.dtos.ApplicationsDto.CreateApplicationRequestDto;
import com.example.WorkWite_Repo_BE.dtos.ApplicationsDto.PaginatedAppResponseDto;
import com.example.WorkWite_Repo_BE.dtos.ApplicationsDto.UpdateAppRequestDto;
import com.example.WorkWite_Repo_BE.services.AppService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/apps")
public class AppController {
    private final AppService appService;

    public AppController(AppService appService) {
        this.appService = appService;
    }
    //    @GetMapping("")
//    public List<AppResponseDto> getAllApps() {
//        return appService.getAllApps();
//    }
    @GetMapping("")
    public PaginatedAppResponseDto getAllAppsByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        System.out.println("page: " + page);
        System.out.println("size: " + size);
        return this.appService.getAllAppsByPage(page, size);
    }

    @PostMapping()
    public AppResponseDto createApplication(@RequestBody @Valid CreateApplicationRequestDto createApplicationRequestDto) {
        return this.appService.createApplication(createApplicationRequestDto);
    }

    @GetMapping("/{id}")
    public AppResponseDto getAppById(@PathVariable("id") Integer id) {
        return this.appService.getAppById(id);
    }
    @PatchMapping("/{id}")
    public AppResponseDto updateStudent(@PathVariable("id") Integer id, @RequestBody UpdateAppRequestDto application) {
        return this.appService.updateApplication(id, application);
    }

    @DeleteMapping("/{id}")
    public void deleteApplication(@PathVariable("id") Integer id) {
        this.appService.deleteApplication(id);
    }

}
