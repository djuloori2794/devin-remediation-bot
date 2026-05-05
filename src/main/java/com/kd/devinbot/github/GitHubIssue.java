package com.kd.devinbot.github;

public record GitHubIssue(
    int number,
    String title,
    String body,
    String htmlUrl
) {}
