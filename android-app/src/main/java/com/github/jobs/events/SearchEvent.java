package com.github.jobs.events;

import com.github.jobs.bean.SearchPack;

public class SearchEvent {
  public final SearchPack searchPack;

  public SearchEvent(SearchPack searchPack) {
    this.searchPack = searchPack;
  }
}
