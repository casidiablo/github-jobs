/*
 * Copyright 2012 CodeSlap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codeslap.github.jobs.api;

/**
 * @author cristian
 */
public class Search {
    //A search term, such as "ruby" or "java"
    private final String search;
    // A city name, zip code, or other location search term.
    private final String location;
    // A specific latitude. If used, you must also send long and must not send location.
    private final double latitude;
    // A specific longitude. If used, you must also send lat and must not send location.
    private final double longitude;
    // If you want to limit results to full time positions set this parameter to 'true'.
    private final boolean fullTime;
    // page number...
    private final int page;

    private Search(String search, String location, double latitude, double longitude, boolean fullTime, int page) {
        this.search = search;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fullTime = fullTime;
        this.page = page;
    }

    public String getSearch() {
        return search;
    }

    public String getLocation() {
        return location;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isFullTime() {
        return fullTime;
    }

    public int getPage() {
        return page;
    }

    public static class Builder {
        private String search;
        private String location;
        private double latitude;
        private double longitude;
        private boolean fullTime;
        private int page;

        public Builder setSearch(String search) {
            this.search = search;
            return this;
        }

        public Builder setLocation(String location) {
            this.location = location;
            return this;
        }

        public Builder setLatitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder setLongitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder setFullTime(boolean fullTime) {
            this.fullTime = fullTime;
            return this;
        }

        public Builder setPage(int page) {
            this.page = page;
            return this;
        }

        public Search createSearch() {
            if (location != null && (latitude != 0 || longitude != 0)) {
                throw new IllegalStateException("If you set the location, you cannot set latitude and longitude");
            }
            if (page < 0) {
                throw new IllegalStateException("Invalid page number. It must be a positive integer.");
            }
            return new Search(search, location, latitude, longitude, fullTime, page);
        }
    }
}
