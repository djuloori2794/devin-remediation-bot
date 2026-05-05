package com.kd.devinbot.controller;

import com.kd.devinbot.scheduler.IssueScanner;
import com.kd.devinbot.task.TaskStatus;
import com.kd.devinbot.task.TaskStore;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetricsController {

  private final TaskStore taskStore;
  private final IssueScanner issueScanner;

  public MetricsController(TaskStore taskStore, IssueScanner issueScanner) {
    this.taskStore = taskStore;
    this.issueScanner = issueScanner;
  }

  @GetMapping("/metrics")
  public Map<String, Object> metrics() {
    var tasks = taskStore.allTasks();

    long total = tasks.size();
    long successful = tasks.stream()
        .filter(task -> task.status() == TaskStatus.SUCCESS)
        .count();

    long failed = tasks.stream()
        .filter(task -> task.status() == TaskStatus.FAILED)
        .count();

    double successRate = total == 0 ? 0.0 : (successful * 100.0) / total;

    return Map.of(
        "total_tasks", total,
        "successful_devin_sessions", successful,
        "failed_devin_sessions", failed,
        "success_rate_percent", successRate,
        "automation_trigger", "scheduled GitHub issue scanner",
        "tracked_signal", "Devin session creation",
        "processed_issue_numbers", taskStore.processedIssueNumbers(),
        "last_scan_time", issueScanner.getLastScanTime()
    );
  }
}
