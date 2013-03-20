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

package com.github.jobs.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author cristian
 */
public class JsonMapper {
  private static final ObjectMapper mapper = new ObjectMapper();
  static {
    mapper.configure(MapperFeature.AUTO_DETECT_FIELDS, true);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
  }

  public static <T> List<T> getList(InputStream from, TypeReference<List<T>> reference) {
    try {
      return mapper.readValue(from, reference);
    } catch (IOException e) {
      return null;
    }
  }

  public static <T> T getObject(InputStream from, Class<T> type) {
    try {
      return mapper.readValue(from, type);
    } catch (IOException e) {
      return null;
    }
  }
}
