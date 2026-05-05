package com.kd.devinbot.devin;

import com.kd.devinbot.config.AppConfig;
import com.kd.devinbot.github.GitHubIssue;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.stereotype.Component;

@Component
public class DevinClient {

  private final AppConfig config;
  private final HttpClient httpClient;

  public DevinClient(AppConfig config) {
    this.config = config;
    this.httpClient = HttpClient.newHttpClient();
  }

  public String createSession(GitHubIssue issue) throws Exception {
    String repoUrl = "https://github.com/%s/%s".formatted(config.githubOwner, config.githubRepo);

    String prompt = """
        You are working on my fork of Apache Superset.

        Repository:
        %s

        Issue:
        %s

        Issue title:
        %s

        Issue body:
        %s

        Task:
        Remediate the GitHub issue with the smallest safe code or documentation change.

        Requirements:
        1. Create a new branch.
        2. Make the smallest safe change that addresses the issue.
        3. Run a lightweight relevant validation if possible.
        4. Open a pull request against %s/%s.
        5. In the PR description, include:
           - What changed
           - How it was validated
           - Any remaining risks
        6. Comment on the issue with the PR link.

        Keep the change focused. Do not refactor broadly.
        """.formatted(
        repoUrl,
        issue.htmlUrl(),
        issue.title(),
        issue.body(),
        config.githubOwner,
        config.githubRepo
    );

    String body = """
        {
          "prompt": %s,
          "repo_url": "%s"
        }
        """.formatted(toJsonString(prompt), repoUrl);

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.devin.ai/v1/sessions"))
        .header("Authorization", "Bearer " + config.devinApiKey)
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() < 200 || response.statusCode() >= 300) {
      throw new RuntimeException("Devin API failed: " + response.body());
    }

    return response.body();
  }

  private String toJsonString(String value) {
    return "\"" + value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
        + "\"";
  }

}
