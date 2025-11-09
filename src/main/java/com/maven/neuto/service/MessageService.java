package com.maven.neuto.service;

import com.maven.neuto.payload.request.socket.ChatMessageResponseDTO;
import com.maven.neuto.payload.request.socket.ReportUserDTO;
import com.maven.neuto.payload.request.socket.SidebarUserDTO;
import com.maven.neuto.payload.response.PaginatedResponse;
import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.Query;

public interface MessageService {
    PaginatedResponse<SidebarUserDTO> getSideBarUsers(String name, Integer pageNumber, Integer pageSize, String sortOrder);

    PaginatedResponse<ChatMessageResponseDTO> getChats(Long receiverId, Integer pageNumber, Integer pageSize, String sortOrder);

    String clearChats(Long chatId);
    
    String blockUser(Long blockedId);

    String reportUser(ReportUserDTO request);
}
