package com.kd.devinbot.task;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class TaskStore {

  private final Map<Integer, TaskRecord> tasksByIssueNumber = new ConcurrentHashMap<>();

  public boolean alreadyProcessed(int issueNumber) {
    return tasksByIssueNumber.containsKey(issueNumber);
  }

  public void saveSuccess(int issueNumber, String issueUrl, String title, String devinResponse) {
    tasksByIssueNumber.put(issueNumber, new TaskRecord(
        UUID.randomUUID().toString(),
        issueNumber,
        issueUrl,
        title,
        devinResponse,
        TaskStatus.SUCCESS,
        Instant.now(),
        null
    ));
  }

  public void saveFailed(int issueNumber, String issueUrl, String title, String error) {
    tasksByIssueNumber.put(issueNumber, new TaskRecord(
        UUID.randomUUID().toString(),
        issueNumber,
        issueUrl,
        title,
        null,
        TaskStatus.FAILED,
        Instant.now(),
        error
    ));
  }

  public List<Integer> processedIssueNumbers() {
    return tasksByIssueNumber.keySet().stream().sorted().toList();
  }

  public Collection<TaskRecord> allTasks() {
    return tasksByIssueNumber.values();
  }
}