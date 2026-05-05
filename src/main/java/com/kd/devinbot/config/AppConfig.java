package com.kd.devinbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Value("${GITHUB_TOKEN}")
  public String githubToken;

  @Value("${GITHUB_OWNER}")
  public String githubOwner;

  @Value("${GITHUB_REPO}")
  public String githubRepo;

  @Value("${DEVIN_API_KEY}")
  public String devinApiKey;

  @Value("${SCAN_LABEL:devin-remediate}")
  public String scanLabel;

}
