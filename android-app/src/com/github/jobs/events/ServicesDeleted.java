package com.github.jobs.events;

public class ServicesDeleted {
  public final boolean noServicesRemaining;

  public ServicesDeleted(boolean noServicesRemaining) {
    this.noServicesRemaining = noServicesRemaining;
  }
}
