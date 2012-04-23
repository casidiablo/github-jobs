package com.github.jobs.bean;

/**
 * @version 1.0
 * @author cristian
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
