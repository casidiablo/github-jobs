package com.github.jobs.ui;

import android.text.TextUtils;

import java.io.Serializable;

public class SearchPack implements Serializable {
    private String search;
    private String location;
    private boolean fullTime = true;
    private int page;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isFullTime() {
        return fullTime;
    }

    public void setFullTime(boolean fullTime) {
        this.fullTime = fullTime;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchPack that = (SearchPack) o;

        if (fullTime != that.fullTime) return false;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        if (search != null ? !search.equals(that.search) : that.search != null) return false;

        return true;
    }

    @Override
    public String toString() {
        String toString = "";
        if (!TextUtils.isEmpty(search)) {
            toString = search;
        }
        if (!TextUtils.isEmpty(location)) {
            if (!TextUtils.isEmpty(this.search)) {
                toString += ", ";
            }
            toString += location;
        }
        if (!fullTime) {
            toString += " (part time)";
        }
        return toString;
    }

    @Override
    public int hashCode() {
        int result = search != null ? search.toLowerCase().trim().hashCode() : 0;
        result = 31 * result + (location != null ? location.toLowerCase().trim().hashCode() : 0);
        result = 31 * result + (fullTime ? 1 : 0);
        return result;
    }

    public boolean isDefault() {
        return search == null && location == null && fullTime;
    }
}
