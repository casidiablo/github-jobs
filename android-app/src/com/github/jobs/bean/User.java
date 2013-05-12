/*
 * Copyright 2012 CodeSlap
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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author cristian
 * @version 1.0
 */
public class User {
  @JsonProperty("created_at")
  private String createdAt;
  private String blog;
  private String email;
  private int following;
  @JsonProperty("public_repos")
  private int publicRepos;
  private String location;
  private String bio;
  @JsonProperty("html_url")
  private String htmlUrl;
  private String name;
  @JsonProperty("avatar_url")
  private String avatarUrl;
  private int followers;
  private long id;
  @JsonProperty("login")
  private String username;
  private String company;

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getBlog() {
    return blog;
  }

  public void setBlog(String blog) {
    this.blog = blog;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public int getFollowing() {
    return following;
  }

  public void setFollowing(int following) {
    this.following = following;
  }

  public int getPublicRepos() {
    return publicRepos;
  }

  public void setPublicRepos(int publicRepos) {
    this.publicRepos = publicRepos;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public String getHtmlUrl() {
    return htmlUrl;
  }

  public void setHtmlUrl(String htmlUrl) {
    this.htmlUrl = htmlUrl;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public int getFollowers() {
    return followers;
  }

  public void setFollowers(int followers) {
    this.followers = followers;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    User user = (User) o;

    if (followers != user.followers) return false;
    if (following != user.following) return false;
    if (id != user.id) return false;
    if (publicRepos != user.publicRepos) return false;
    if (avatarUrl != null ? !avatarUrl.equals(user.avatarUrl) : user.avatarUrl != null) {
      return false;
    }
    if (bio != null ? !bio.equals(user.bio) : user.bio != null) return false;
    if (blog != null ? !blog.equals(user.blog) : user.blog != null) return false;
    if (company != null ? !company.equals(user.company) : user.company != null) return false;
    if (createdAt != null ? !createdAt.equals(user.createdAt) : user.createdAt != null) {
      return false;
    }
    if (email != null ? !email.equals(user.email) : user.email != null) return false;
    if (htmlUrl != null ? !htmlUrl.equals(user.htmlUrl) : user.htmlUrl != null) return false;
    if (location != null ? !location.equals(user.location) : user.location != null) return false;
    if (name != null ? !name.equals(user.name) : user.name != null) return false;
    if (username != null ? !username.equals(user.username) : user.username != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = createdAt != null ? createdAt.hashCode() : 0;
    result = 31 * result + (blog != null ? blog.hashCode() : 0);
    result = 31 * result + (email != null ? email.hashCode() : 0);
    result = 31 * result + following;
    result = 31 * result + publicRepos;
    result = 31 * result + (location != null ? location.hashCode() : 0);
    result = 31 * result + (bio != null ? bio.hashCode() : 0);
    result = 31 * result + (htmlUrl != null ? htmlUrl.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (avatarUrl != null ? avatarUrl.hashCode() : 0);
    result = 31 * result + followers;
    result = 31 * result + (int) (id ^ (id >>> 32));
    result = 31 * result + (username != null ? username.hashCode() : 0);
    result = 31 * result + (company != null ? company.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "User{" +
        "createdAt='" + createdAt + '\'' +
        ", blog='" + blog + '\'' +
        ", email='" + email + '\'' +
        ", following=" + following +
        ", publicRepos=" + publicRepos +
        ", location='" + location + '\'' +
        ", bio='" + bio + '\'' +
        ", htmlUrl='" + htmlUrl + '\'' +
        ", name='" + name + '\'' +
        ", avatarUrl='" + avatarUrl + '\'' +
        ", followers=" + followers +
        ", id=" + id +
        ", username='" + username + '\'' +
        ", company='" + company + '\'' +
        '}';
  }
}
