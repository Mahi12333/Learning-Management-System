package com.maven.neuto.controller;

import com.maven.neuto.payload.request.socket.ChatMessageResponseDTO;
import com.maven.neuto.payload.request.socket.ReportUserDTO;
import com.maven.neuto.payload.request.socket.SidebarUserDTO;
import com.maven.neuto.payload.response.PaginatedResponse;
import com.maven.neuto.service.MessageService;
import com.maven.neuto.utils.AppConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/message")
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/sidebar-users")
    public ResponseEntity<?> getSidebarUsers( @RequestParam(name = "name",  required = false) String name,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                             @RequestParam(name = "pageSize", defaultValue = AppConstants.LIMIT, required = false) Integer pageSize,
                                             @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder){
        PaginatedResponse<SidebarUserDTO> response = messageService.getSideBarUsers(name, pageNumber, pageSize, sortOrder);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/chats")
    public ResponseEntity<?> getChatsUsers(@RequestParam(name = "receiverId", required = true) Long receiverId,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                             @RequestParam(name = "pageSize", defaultValue = AppConstants.LIMIT, required = false) Integer pageSize,
                                             @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder){
        PaginatedResponse<ChatMessageResponseDTO> response = messageService.getChats(receiverId, pageNumber, pageSize, sortOrder);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/clear-chats")
    public ResponseEntity<?> clearChats(@Valid @RequestParam(name = "chatId", required = true) Long chatId){
        String response = messageService.clearChats(chatId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/block-user")
    public ResponseEntity<?> blockUser(@Valid @RequestParam(name = "blockedId", required = true) Long blockedId) {
        String response = messageService.blockUser(blockedId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/report-user")
    public ResponseEntity<?> reportUser(@RequestBody ReportUserDTO request) {
        String response = messageService.reportUser(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
