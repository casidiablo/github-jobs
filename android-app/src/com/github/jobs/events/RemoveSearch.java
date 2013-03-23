package com.github.jobs.events;

import com.github.jobs.bean.SearchPack;

public class RemoveSearch {
  public final SearchPack searchPack;

  public RemoveSearch(SearchPack searchPack) {
    this.searchPack = searchPack;
  }
}
