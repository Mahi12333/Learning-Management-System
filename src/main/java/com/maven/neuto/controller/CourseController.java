package com.maven.neuto.controller;

import com.maven.neuto.payload.request.course.*;
import com.maven.neuto.utils.AppConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/v1/course")
@RequiredArgsConstructor
public class CourseController {

   /* @PostMapping("/create-course")
    public ResponseEntity<?>createCourse(@Valid @RequestBody CourseCreateDTO request){

    }
    @GetMapping("/public-course")
    public ResponseEntity<?>PublicCourse(@Valid @RequestBody  ){

    }

    @GetMapping("/student-ongoing-course")
    public ResponseEntity<?>StudentOngoingCourse(@Valid @RequestBody  ){
    }

    @GetMapping("/student-recommended-course")
    public ResponseEntity<?>StudentRecommendedCourse(@Valid @RequestBody  ){
    }

    @GetMapping("/course-details-fetch")
    public ResponseEntity<?> CourseDetailsFetch(@Valid @RequestParam("slug") String slug){
    }

    @DeleteMapping("/delete-course")
    public ResponseEntity<?> DeleteCourse(@Valid @RequestParam("slug") String slug){

    }

    @GetMapping("/course-module-details-fetch")
    public ResponseEntity<?> CourseModuleDetailsFetch(@Valid @RequestParam("slug") String slug){
    }

    @GetMapping("/lesson-details-fetch")
    public ResponseEntity<?> LessonDetailsFetch(@Valid @RequestParam("slug") String slug){
    }

    @GetMapping("/project-actives")
    public ResponseEntity<?> ProjectActives(@Valid @RequestParam("slug") String slug, @RequestParam("search") String search, @RequestParam("location") String location){
    }

    @PostMapping("/create-module")
    public ResponseEntity<?>createModule(@Valid ){
    }

    @PatchMapping("/update-module")
    public ResponseEntity<?>updateModule(@Valid ){
    }

    @DeleteMapping("/delete-course")
    public ResponseEntity<?> DeleteModule(@Valid @RequestParam("slug") String slug){

    }

    @PostMapping("/create-lesson")
    public ResponseEntity<?>createLesson(@Valid @RequestBody ){
    }

    @PatchMapping("/update-lesson")
    public ResponseEntity<?>updateLesson(@Valid @RequestBody ){
    }

    @DeleteMapping("/delete-lesson")
    public ResponseEntity<?> deleteLesson(@Valid @RequestParam("slug") String slug){
    }

    @GetMapping("/student-recommended-lesson")
    public ResponseEntity<?> StudentRecommendedLesson(){
    }

    @PostMapping("/create-task-module")
    public ResponseEntity<?> CreateTaskModule(@Valid @RequestBody TaskModuleCreateDTO request){

    }

    @PatchMapping("/update-task-module")
    public ResponseEntity<?> UpdateTaskModule(@Valid @RequestBody TaskModuleUpdateDTO request){

    }

    @DeleteMapping("/delete-task-module")
    public ResponseEntity<?> deleteTaskModule(@Valid @RequestParam("taskId") String taskId){

    }

    @GetMapping("/all-task-module")
    public ResponseEntity<?> AllTaskModule(){

    }

    @PostMapping("/create-comment")
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentCreateDTO request){

    }

    @PatchMapping("/update-comment")
    public ResponseEntity<?> updateComment(@Valid @RequestBody CommentUpdateDTO request){

    }

    @DeleteMapping("/delete-comment")
    public ResponseEntity<?> deleteComment(@Valid @RequestParam("commentId") String commentId){

    }

    @PostMapping("/comment")
    public ResponseEntity<?> commment(@Valid @RequestBody CommentDTO request){

    }

    @PostMapping("/save-lesson-progress")
    public ResponseEntity<?> saveLessonProgress(@Valid @RequestBody LessonSaveProgress request){

    }

    @PostMapping("/like-lesson")
    public ResponseEntity<?> likeLesson(@Valid @RequestParam("lessonId") Long lessonId){

    }

    @GetMapping("/module-wise-progress")
    public ResponseEntity<?> moduleWiseProgress(@RequestParam(name = "courseSlug", required = true) String courseSlug, @RequestParam(name = "search", required = false) String search, @RequestParam(name = "location", required = false) String location){

    }

    @GetMapping("/all-tags")
    public ResponseEntity<?> allTags(@RequestParam(name = "search", required = false ) String search){

    }

    */






}
