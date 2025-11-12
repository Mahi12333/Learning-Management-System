package com.maven.neuto.controller;


import com.maven.neuto.aspect.OwnerCheck;
import com.maven.neuto.payload.request.group.CreateGroupComment;
import com.maven.neuto.payload.request.group.GroupCreateDTO;
import com.maven.neuto.payload.request.group.GroupUpdateDTO;
import com.maven.neuto.payload.response.PaginatedResponse;
import com.maven.neuto.payload.response.group.CommentResponseDTO;
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
@RequestMapping("/api/v1/group")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @PostMapping("/create-group")
    public ResponseEntity<?> createGroup(@Valid @RequestBody GroupCreateDTO request){
        log.info("Create Group Request: {}", request);
        GroupResponseDTO response = groupService.createGroup(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @OwnerCheck(entity = "Group", idParam = "id")
    @PostMapping("/update-group")
    public ResponseEntity<?> updateGroup(@Valid @RequestBody GroupUpdateDTO request){
        GroupResponseDTO response = groupService.updatedGroup(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @OwnerCheck(entity = "Group", idParam = "id")
    @DeleteMapping("/delete-group")
    public ResponseEntity<?> deleteGroup(@Valid @RequestParam("groupId") Long groupId){
        groupService.deleteGroup(groupId);
        return ResponseEntity.ok("group.successfully.delete!");
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
    public ResponseEntity<PaginatedResponse<CommentResponseDTO>> AllGroup(
            @RequestParam(name = "groupId", required = true) Long groupId,
            @RequestParam(name = "parentId", required = false) Long parentId,
            @RequestParam(name = "replyToCommentId", required = false) Long replyToCommentId,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                          @RequestParam(name = "limit", defaultValue = AppConstants.LIMIT, required = false) Integer pageSize,
                                                                          @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder){
        PaginatedResponse<CommentResponseDTO> response = groupService.fetchGroupComment( groupId, parentId, replyToCommentId, type, pageNumber, pageSize, sortOrder);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/group-details-fetch")
    public ResponseEntity<?> groupDetailsFetch(@Valid @RequestParam("groupId") Long groupId){
        return null;
    }



}
