package com.github.jobs.events;

import android.os.Bundle;
import com.github.jobs.bean.SearchPack;

public class SearchFinished extends SearchEvent {
  public final Bundle resultData;

  public SearchFinished(SearchPack searchPack, Bundle resultData) {
    super(searchPack);
    this.resultData = resultData;
  }
}
