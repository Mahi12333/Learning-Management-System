package com.maven.neuto.serviceImplement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;

@Service
public class BulkBigDataUpload {
    @Autowired
    Executor fixedThreadPool;

    /*public void importStudents(List<Student> list) {
        for (Student s : list) {
            fixedThreadPool.execute(() -> saveToDB(s));
        }
    }*/

}
