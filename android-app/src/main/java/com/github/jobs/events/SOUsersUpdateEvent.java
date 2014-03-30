package com.github.jobs.events;

import com.github.jobs.bean.SOUser;

import java.util.ArrayList;

public class SOUsersUpdateEvent {
  public ArrayList<SOUser> users;

  public SOUsersUpdateEvent(ArrayList<SOUser> users) {
    this.users = users;
  }
}
