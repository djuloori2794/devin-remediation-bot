package com.kd.devinbot.scheduler;

import com.kd.devinbot.devin.DevinClient;
import com.kd.devinbot.github.GitHubClient;
import com.kd.devinbot.github.GitHubIssue;
import com.kd.devinbot.task.TaskStore;
import java.time.Instant;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class IssueScanner {

  private final GitHubClient gitHubClient;
  private final DevinClient devinClient;
  private final TaskStore taskStore;
  private Instant lastScanTime;

  public IssueScanner(GitHubClient gitHubClient, DevinClient devinClient, TaskStore taskStore) {
    this.gitHubClient = gitHubClient;
    this.devinClient = devinClient;
    this.taskStore = taskStore;
  }

  /**
   * Runs automatically every 2 minutes.
   */
  @Scheduled(fixedDelayString = "${SCAN_INTERVAL_MS:120000}")
  public void scheduledScan() {
    scan();
  }

  /**
   * Core automation logic:
   * - Fetch GitHub issues with label
   * - Skip already processed issues
   * - Trigger Devin for new issues
   * - Store results
   */
  public void scan() {
    try {
      List<GitHubIssue> issues = gitHubClient.fetchOpenIssuesWithLabel();

      for (GitHubIssue issue : issues) {

        // Skip if already processed
        if (taskStore.alreadyProcessed(issue.number())) {
          continue;
        }

        try {
          String response = devinClient.createSession(issue);

          taskStore.saveSuccess(
              issue.number(),
              issue.htmlUrl(),
              issue.title(),
              response
          );

          System.out.println("SUCCESS: Started Devin session for issue #" + issue.number());

        } catch (Exception e) {

          taskStore.saveFailed(
              issue.number(),
              issue.htmlUrl(),
              issue.title(),
              e.getMessage()
          );

          System.err.println("FAILED: Issue #" + issue.number() + " → " + e.getMessage());
        }
      }

    } catch (Exception e) {
      System.err.println("Scan failed: " + e.getMessage());
    }
    lastScanTime = Instant.now();
  }

  public Instant getLastScanTime() {
    return lastScanTime;
  }
}