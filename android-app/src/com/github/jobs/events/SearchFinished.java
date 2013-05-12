package com.github.jobs.events;

import android.os.Parcelable;
import com.github.jobs.bean.SearchPack;
import java.util.ArrayList;

public class SearchFinished extends SearchEvent {
  public final ArrayList<Parcelable> payload;

  public SearchFinished(SearchPack searchPack, ArrayList<Parcelable> payload) {
    super(searchPack);
    this.payload = payload;
  }
}
