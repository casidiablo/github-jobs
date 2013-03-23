package com.github.jobs.events;

import com.github.jobs.bean.TemplateService;
import java.util.List;

public class DeleteServices {
  public final List<TemplateService> toDelete;

  public DeleteServices(List<TemplateService> toRemove) {
    this.toDelete = toRemove;
  }
}
