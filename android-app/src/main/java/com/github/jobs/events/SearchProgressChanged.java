package com.github.jobs.events;

import com.github.jobs.bean.SearchPack;

public class SearchProgressChanged extends SearchEvent {
  public final boolean syncing;

  public SearchProgressChanged(SearchPack searchPack, boolean syncing) {
    super(searchPack);
    this.syncing = syncing;
  }
}
