package com.kd.devinbot.task;

import java.time.Instant;

public record TaskRecord(String taskId, int issueNumber, String issueUrl, String title,
                         String devinResponse, TaskStatus status, Instant createdAt, String error) {}
