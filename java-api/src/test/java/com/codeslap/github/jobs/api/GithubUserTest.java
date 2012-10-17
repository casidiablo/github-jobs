package com.codeslap.github.jobs.api;

import com.github.api.GithubApi;
import com.github.bean.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author cristian
 * @version 1.0
 */
public class GithubUserTest {
    @Test
    public void testUser() {
        User user = new GithubApi().getUser("casidiablo");
        assertNotNull(user);
        assertNotNull(user.getUsername());
        assertEquals("casidiablo", user.getUsername());
        assertNotNull(user.getAvatarUrl());
        assertNotNull(user.getBio());
        assertNotNull(user.getBlog());
        assertNotNull(user.getCompany());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getFollowers());
        assertNotNull(user.getFollowing());
        assertNotNull(user.getHtmlUrl());
        assertNotNull(user.getPublicRepos());
        assertNotNull(user.getName());
    }
}
