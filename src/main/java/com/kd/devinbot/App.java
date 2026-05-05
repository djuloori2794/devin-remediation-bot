package com.kd.devinbot;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class App {

  private static final String ISSUE_URL = "https://github.com/djuloori2794/superset/issues/1";
  private static final String REPO_URL = "https://github.com/djuloori2794/superset";

  public static void main(String[] args) throws Exception {
    String apiKey = "apk_user_dXNlci1kNmIyNzE3ZDVlMTk0ZWFlYjZiMDk5MmZhYzRjNDI0Y19vcmctNzQ3YWM2NTBiMDc0NGNmMmE5YzMwM2MxNzgxODkwZjY6ZGUzZGQ4OTYzNDU1NGNiZWJhYTEwYzQ1YzlmY2EyMWI=";

    if (apiKey == null || apiKey.isBlank()) {
      throw new IllegalStateException("Missing DEVIN_API_KEY");
    }

    String prompt = """
        You are working on my fork of Apache Superset.

        Repository:
        %s

        Issue:
        %s

        Task:
        Fix the issue with the smallest safe documentation change.

        Requirements:
        1. Create a new branch.
        2. Find one typo, grammar issue, or unclear sentence in docs/.
        3. Make a minimal documentation-only fix.
        4. Open a pull request against djuloori2794/superset.
        5. In the PR description, include:
           - What changed
           - How it was validated
           - Any remaining risks
        6. Comment on the issue with the PR link.

        Keep the change very small.
        """.formatted(REPO_URL, ISSUE_URL);

    String body = """
        {
          "prompt": %s,
          "repo_url": "%s"
        }
        """.formatted(toJsonString(prompt), REPO_URL);

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.devin.ai/v1/sessions"))
        .header("Authorization", "Bearer " + apiKey)
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build();

    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(request, HttpResponse.BodyHandlers.ofString());

    System.out.println("Status: " + response.statusCode());
    System.out.println(response.body());

    if (response.statusCode() < 200 || response.statusCode() >= 300) {
      throw new RuntimeException("Failed to create Devin session");
    }
  }

  private static String toJsonString(String value) {
    return "\"" + value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
        + "\"";
  }
}