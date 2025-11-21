package com.maven.neuto.serviceImplement;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ReportSchdulerService {

    @Scheduled(cron = "0 0 1 * * *") // every day 1 AM
    public void generateDailyAttendance() {
        //TODO
    }
}
