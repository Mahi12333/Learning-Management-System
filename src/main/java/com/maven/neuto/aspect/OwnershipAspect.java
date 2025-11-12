package com.maven.neuto.aspect;

import com.maven.neuto.exception.APIException;
import com.maven.neuto.repository.CourseRepository;
import com.maven.neuto.repository.GroupRepository;
import com.maven.neuto.repository.UserRepository;
import com.maven.neuto.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OwnershipAspect {

     private final CourseRepository courseRepo;
     private final UserRepository userRepo;
     private final GroupRepository groupRepository;

    @Before("@annotation(ownerCheck)")
    public void verifyOwnership(JoinPoint jp, OwnerCheck ownerCheck) {
        String entityType = ownerCheck.entity();
        String idFieldName = ownerCheck.idParam();

        Long entityId = extractIdFromArguments(jp.getArgs(), idFieldName);

        Long currentUserId = ((UserDetailsImpl) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal()).getId();

        // Skip ownership check for super admin (example)
        if (currentUserId == 2L) {
            return;
        }

        boolean isOwner = false;

        switch (entityType) {
            case "Course":
                isOwner = courseRepo.findById(entityId)
                        .map(c -> c.getCourseCreator().getId().equals(currentUserId))
                        .orElse(false);
                break;

            case "Group":
                isOwner = groupRepository.findById(entityId)
                        .map(g -> g.getCreatedBy().getId().equals(currentUserId))
                        .orElse(false);
                break;

            case "User":
                isOwner = entityId.equals(currentUserId);
                break;

            default:
                throw new APIException("Unsupported entity for ownership check: " + entityType,
                        HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (!isOwner) {
            throw new APIException("You are not the owner of this " + entityType + "!", HttpStatus.FORBIDDEN);
        }
    }

    private Long extractIdFromArguments(Object[] args, String idFieldName) {
        for (Object arg : args) {
            if (arg instanceof Long) {
                return (Long) arg;
            }
            try {
                Field field = arg.getClass().getDeclaredField(idFieldName);
                field.setAccessible(true);
                Object value = field.get(arg);
                if (value instanceof Long) {
                    return (Long) value;
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        }
        throw new IllegalArgumentException("Could not find ID parameter: " + idFieldName);
    }

    // Optional - profile completion check
    @Before("@annotation(profileCompleteCheck)")
    public void ensureProfileComplete(JoinPoint jp) {
        Long currentUserId = ((UserDetailsImpl) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal()).getId();
        userRepo.findById(currentUserId)
                .filter(u -> Boolean.TRUE.equals(u.getProfileComplete()))
                .orElseThrow(() -> new APIException("Profile incomplete. Please complete it first.", HttpStatus.BAD_REQUEST));
    }
}
