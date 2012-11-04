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
