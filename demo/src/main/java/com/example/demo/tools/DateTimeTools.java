package com.example.demo.tools;

import org.springframework.ai.tool.annotation.Tool;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author yanglin
 */
public class DateTimeTools {

    @Tool(name = "getCurrentDateTime", description = "Get the current date and time in the user's timezone")
    public String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Tool(name = "setAlarm", description = "Set a user alarm for the given time, provided in yyyy-MM-dd HH:mm:ss format")
    public void setAlarm(String time) {
        LocalDateTime alarmTime = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("Alarm set for " + alarmTime);
    }
}
