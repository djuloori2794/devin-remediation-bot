package com.kd.devinbot.task;

public enum TaskStatus {

  /**
   * Indicates that the Devin session was successfully created.
   * This means the issue has been successfully delegated to Devin.
   * (For this demo, we treat session creation as successful execution.)
   */
  SUCCESS,

  /**
   * Indicates that the Devin session could not be created due to an error
   * (e.g., API failure, network issue, invalid request).
   */
  FAILED
}
