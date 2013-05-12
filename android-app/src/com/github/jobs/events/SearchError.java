package com.github.jobs.events;

import com.github.jobs.bean.SearchPack;

public class SearchError extends SearchEvent {
  public SearchError(SearchPack searchPack) {
    super(searchPack);
  }
}
