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
package com.gemstone.gemfire.internal.cache.versions;

import org.junit.experimental.categories.Category;

import com.gemstone.gemfire.test.junit.categories.UnitTest;

import junit.framework.TestCase;

@Category(UnitTest.class)
public class RVVExceptionJUnitTest extends TestCase {

  public RVVExceptionJUnitTest() {
  }
  
  public void testRVVExceptionB() {
    RVVExceptionB ex = new RVVExceptionB(5, 10);
    ex.add(8);
    ex.add(6);
    assertEquals(8, ex.getHighestReceivedVersion());
    ex.add(5);
    assertEquals(8, ex.getHighestReceivedVersion());
    
  }

  public void testRVVExceptionT() {
    RVVExceptionT ex = new RVVExceptionT(5, 10);
    ex.add(8);
    ex.add(6);
    assertEquals(8, ex.getHighestReceivedVersion());

  }
}
