package com.kd.devinbot.controller;

import com.kd.devinbot.scheduler.IssueScanner;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScanController {

  private final IssueScanner issueScanner;

  public ScanController(IssueScanner issueScanner) {
    this.issueScanner = issueScanner;
  }

  @PostMapping("/scan-now")
  public String scanNow() {
    issueScanner.scan();
    return "Scan completed";
  }
}