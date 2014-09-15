/*
 * Copyright 2014 Some Dev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.jobs.bean;

/**
 * @author cristian
 * @version 1.0
 */
public class SearchesAndJobs {
  private long id;
  private int searchHashCode;
  private String jobId;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public int getSearchHashCode() {
    return searchHashCode;
  }

  public void setSearchHashCode(int searchHashCode) {
    this.searchHashCode = searchHashCode;
  }

  public String getJobId() {
    return jobId;
  }

  public void setJobId(String jobId) {
    this.jobId = jobId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SearchesAndJobs that = (SearchesAndJobs) o;

    if (id != that.id) return false;
    if (searchHashCode != that.searchHashCode) return false;
    if (jobId != null ? !jobId.equals(that.jobId) : that.jobId != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + searchHashCode;
    result = 31 * result + (jobId != null ? jobId.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "SearchesAndJobs{" +
        "id=" + id +
        ", searchHashCode=" + searchHashCode +
        ", jobId='" + jobId + '\'' +
        '}';
  }
}
