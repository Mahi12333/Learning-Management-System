package com.maven.neuto.controller;


import com.maven.neuto.payload.request.group.CreateGroupComment;
import com.maven.neuto.payload.request.group.GroupCreateDTO;
import com.maven.neuto.payload.request.group.GroupUpdateDTO;
import com.maven.neuto.payload.response.group.GroupCommentResponseDTO;
import com.maven.neuto.payload.response.group.GroupResponseDTO;
import com.maven.neuto.service.GroupService;
import com.maven.neuto.utils.AppConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/course")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @PostMapping("/create-group")
    public ResponseEntity<?> createGroup(@Valid @RequestBody GroupCreateDTO request){
        GroupResponseDTO response = groupService.createGroup(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/update-group")
    public ResponseEntity<?> updateGroup(@Valid @RequestBody GroupUpdateDTO request){
        GroupResponseDTO response = groupService.updatedGroup(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-group")
    public ResponseEntity<?> deleteGroup(@Valid @RequestParam("groupId") Long groupId){
        groupService.deleteGroup(groupId);
        return ResponseEntity.ok("Group successfully delete!");
    }

    @PostMapping("/join-leave-group")
    public ResponseEntity<?> joinLeaveGroup(@Valid @RequestParam("groupId") Long groupId){
         String response = groupService.joinLeaveGroup(groupId);
         return ResponseEntity.ok(response);
    }

    @PostMapping("/create-group-comment")
    public ResponseEntity<?> createGroupComment(@Valid @RequestBody CreateGroupComment request){
        GroupCommentResponseDTO response = groupService.createGroupComment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-group-comment")
    public ResponseEntity<?> deleteGroupComment(@Valid @RequestParam("groupId") Long groupId){
     return null;
    }

    @GetMapping("/fetch-group-comment")
    public ResponseEntity<?> fetchGroupComment(@Valid @RequestParam("groupId") Long groupId){
        return null;
    }

    @GetMapping("/all-group")
    public ResponseEntity<?> AllGroup( @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                       @RequestParam(name = "limit", defaultValue = AppConstants.LIMIT, required = false) Integer pageSize,
                                       @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder){
        return null;
    }

    @PostMapping("/group-details-fetch")
    public ResponseEntity<?> groupDetailsFetch(@Valid @RequestParam("groupId") Long groupId){
        return null;
    }



}
