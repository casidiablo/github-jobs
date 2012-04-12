package com.github.jobs.ui;

import android.text.TextUtils;

import java.io.Serializable;

public class SearchPack implements Serializable {
    public String search;
    public String location;
    public boolean fullTime = true;
    public int page;

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
        int result = search != null ? search.hashCode() : 0;
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (fullTime ? 1 : 0);
        return result;
    }

    public boolean isDefault() {
        return search == null && location == null && fullTime;
    }
}
