package com.maven.neuto.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class OwnershipAspect {
    /*
    @Autowired private CourseRepository courseRepo;
    @Autowired private UserRepository userRepo;

    @Before("@annotation(ownerCheck)")
    public void checkOwnership(JoinPoint jp, OwnerCheck ownerCheck) {
        Long id = (Long) Arrays.stream(jp.getArgs())
                      .filter(arg -> arg instanceof Long)
                      .findFirst()
                      .orElseThrow();

        Long currentUserId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().getId();
        boolean isOwner = courseRepo.findById(id)
                          .map(c -> c.getCreatedBy().getId().equals(currentUserId))
                          .orElse(false);

        if (!isOwner) throw new AccessDeniedException("Not owner!");
    }

     @Before("@annotation(profileCompleteCheck)")
     public void checkOwnership(JoinPoint jp){
         Long currentUserId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().getId();
         // will be check profileComplete field this is complete or not.. if not than error will be through that your profile still incomplete
     }
     */


}
