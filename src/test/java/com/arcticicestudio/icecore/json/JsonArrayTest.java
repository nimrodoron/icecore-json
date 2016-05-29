/*
+++++++++++++++++++++++++++++++++++++++++++
title     JSON Array Test                 +
project   icecore-json                    +
file      JsonArrayTest.java              +
version                                   +
author    Arctic Ice Studio               +
email     development@arcticicestudio.com +
website   http://arcticicestudio.com      +
copyright Copyright (C) 2016              +
created   2016-05-29 10:00 UTC+0200       +
modified  2016-05-29 10:01 UTC+0200       +
+++++++++++++++++++++++++++++++++++++++++++

[Description]
Tests the JSON array structure representation class "JsonArray".

[Copyright]
Copyright (C) 2016 Arctic Ice Studio <development@arcticicestudio.com>

[References]
JSON
  (http://json.org)
ECMA-404 1st Edition (October 2013)
  (http://www.ecma-international.org/publications/files/ECMA-ST/ECMA-404.pdf)
Java 8 API Documentation
  (https://docs.oracle.com/javase/8/docs/api/)
JUnit
  (http://junit.org)
Arctic Versioning Specification (ArcVer)
  (http://specs.arcticicestudio.com/arcver)
*/
package com.arcticicestudio.icecore.json;

import static com.arcticicestudio.icecore.json.TestUtil.assertException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the JSON array structure representation class {@link JsonArray}.
 *
 * @author Arctic Ice Studio &lt;development@arcticicestudio.com&gt;
 * @since 0.7.0
 */
public class JsonArrayTest {

  private JsonArray array;

  @Before
  public void setUp() {
    array = new JsonArray();
  }

  @Test
  public void copyConstructorFailsWithNull() {
    assertException(NullPointerException.class, "array is null", new Runnable() {
      public void run() {
        new JsonArray(null);
      }
    });
  }

  @Test
  public void copyConstructorHasSameValues() {
    array.add(92);
    JsonArray copy = new JsonArray(array);
    assertEquals(array.values(), copy.values());
  }

  @Test
  public void copyConstructorWorksOnSafeCopy() {
    JsonArray copy = new JsonArray(array);
    array.add(92);
    assertTrue(copy.isEmpty());
  }

  @Test
  public void unmodifiableArrayHasSameValues() {
    array.add(92);
    JsonArray unmodifiableArray = JsonArray.unmodifiableArray(array);
    assertEquals(array.values(), unmodifiableArray.values());
  }

  @Test
  public void unmodifiableArrayReflectsChanges() {
    JsonArray unmodifiableArray = JsonArray.unmodifiableArray(array);
    array.add(92);
    assertEquals(array.values(), unmodifiableArray.values());
  }
}
