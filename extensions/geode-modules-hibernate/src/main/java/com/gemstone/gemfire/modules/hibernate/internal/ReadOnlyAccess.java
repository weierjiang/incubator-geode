/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.gemstone.gemfire.modules.hibernate.internal;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.access.SoftLock;

public class ReadOnlyAccess extends Access {

  public ReadOnlyAccess(GemFireEntityRegion region) {
    super(region);
  }

  @Override
  public boolean insert(Object key, Object value, Object version)
      throws CacheException {
    throw new UnsupportedOperationException(
        "insert not supported on read only access");
  }

  @Override
  public boolean update(Object key, Object value, Object currentVersion,
      Object previousVersion) throws CacheException {
    throw new UnsupportedOperationException(
        "update not supported on read only access");
  }

  @Override
  public boolean afterInsert(Object key, Object value, Object version)
      throws CacheException {
    throw new UnsupportedOperationException(
        "insert not supported on read only access");
  }

  @Override
  public boolean afterUpdate(Object key, Object value, Object currentVersion,
      Object previousVersion, SoftLock lock) throws CacheException {
    throw new UnsupportedOperationException(
        "update not supported on read only access");
  }
}
