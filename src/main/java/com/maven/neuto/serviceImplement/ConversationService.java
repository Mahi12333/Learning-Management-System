package com.maven.neuto.serviceImplement;


import com.maven.neuto.model.Conversation;
import com.maven.neuto.model.ConversationParticipant;
import com.maven.neuto.model.User;
import com.maven.neuto.repository.ConversationParticipantRepository;
import com.maven.neuto.repository.ConversationRepository;
import com.maven.neuto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final UserRepository userRepository;

    @Transactional
    public Conversation findOrCreate(Long user1Id, Long user2Id) {
        // 🧠 Step 1: Try to find an existing conversation having both participants
        Optional<Conversation> existingConversation = conversationRepository
                .findByParticipants(user1Id, user2Id);
        if (existingConversation.isPresent()) {
            return existingConversation.get();
        }

        // 🧱 Step 2: Create new conversation
        Conversation newConversation = new Conversation();
        conversationRepository.save(newConversation);

        // 🧍‍♂️ Step 3: Create participants
        User user1 = userRepository.getReferenceById(user1Id);
        User user2 = userRepository.getReferenceById(user2Id);

        ConversationParticipant p1 = ConversationParticipant.builder()
                .conversation(newConversation)
                .conversationParticipantUser(user1)
                .build();


        ConversationParticipant p2 = ConversationParticipant.builder()
                .conversation(newConversation)
                .conversationParticipantUser(user2)
                .build();


        participantRepository.saveAll(List.of(p1, p2));

        // ✅ Step 4: Return the saved conversation
        return newConversation;
    }
}
