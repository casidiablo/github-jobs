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

import org.json.JSONException;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author cristian
 */
public class GithubJobsApiTest {

//    @Test
    public void test() throws JSONException {
        Search search = new Search.Builder().setSearch("ruby").createSearch();
        List<Job> jobs = GithubJobsApi.search(search);
        assertNotNull(jobs);
        assertTrue(jobs.size() > 0);
        for (Job job : jobs) {
            Job found = GithubJobsApi.getJob(job.getId(), false);
            assertEquals(job, found);
        }
    }
}
