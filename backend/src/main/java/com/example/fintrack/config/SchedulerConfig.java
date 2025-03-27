package com.example.fintrack.config;

import com.example.fintrack.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerConfig {

    @Autowired
    private NotificationService notificationService;

    @Scheduled(cron = "0 0 9 * * ?") // Runs at 9 AM every day
    public void sendReminders() {
        notificationService.checkDueDatesAndNotify();
        notificationService.checkBudgetLimits();
    }
}
