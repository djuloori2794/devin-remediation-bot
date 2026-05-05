package com.kd.devinbot.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kd.devinbot.config.AppConfig;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GitHubClient {

  private final AppConfig config;
  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;

  public GitHubClient(AppConfig config) {
    this.config = config;
    this.httpClient = HttpClient.newHttpClient();
    this.objectMapper = new ObjectMapper();
  }

  public List<GitHubIssue> fetchOpenIssuesWithLabel() throws Exception {
    String url = "https://api.github.com/repos/%s/%s/issues?state=open&labels=%s"
        .formatted(config.githubOwner, config.githubRepo, config.scanLabel);

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Authorization", "Bearer " + config.githubToken)
        .header("Accept", "application/vnd.github+json")
        .GET()
        .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() < 200 || response.statusCode() >= 300) {
      throw new RuntimeException("GitHub API failed: " + response.body());
    }

    JsonNode root = objectMapper.readTree(response.body());
    List<GitHubIssue> issues = new ArrayList<>();

    for (JsonNode item : root) {
      if (item.has("pull_request")) {
        continue;
      }

      issues.add(new GitHubIssue(
          item.get("number").asInt(),
          item.get("title").asText(),
          item.path("body").asText(""),
          item.get("html_url").asText()
      ));
    }

    return issues;
  }
}
