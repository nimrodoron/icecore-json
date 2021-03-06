/*
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
title      JSON Object Test                                +
project    icecore-json                                    +
version    0.8.0-frost.1                                   +
repository https://github.com/arcticicestudio/icecore-json +
author     Arctic Ice Studio                               +
email      development@arcticicestudio.com                 +
copyright  Copyright (C) 2016                              +
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
*/
package com.arcticicestudio.icecore.json;

import static com.arcticicestudio.icecore.json.TestUtil.assertException;
import static com.arcticicestudio.icecore.json.TestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.arcticicestudio.icecore.json.JsonObject.HashIndexTable;
import com.arcticicestudio.icecore.json.JsonObject.Member;
import org.mockito.InOrder;

/**
 * Tests the JSON object structure representation class {@link JsonObject}.
 *
 * @author Arctic Ice Studio &lt;development@arcticicestudio.com&gt;
 * @since 0.7.0
 */
public class JsonObjectTest {

  private JsonObject object;

  @Before
  public void setUp() {
    object = new JsonObject();
  }

  @Test
  public void copyConstructorFailsWithNull() {
    assertException(NullPointerException.class, "object is null", new Runnable() {
      public void run() {
        new JsonObject(null);
      }
    });
  }

  @Test
  public void copyConstructorHasSameValues() {
    object.add("yogurt", 92);
    JsonObject copy = new JsonObject(object);
    assertEquals(object.names(), copy.names());
    assertSame(object.get("yogurt"), copy.get("yogurt"));
  }

  @Test
  public void copyConstructorWorksOnSafeCopy() {
    JsonObject copy = new JsonObject(object);
    object.add("yogurt", 92);
    assertTrue(copy.isEmpty());
  }

  @Test
  public void unmodifiableObjectHasSameValues() {
    object.add("yogurt", 92);
    JsonObject unmodifiableObject = JsonObject.unmodifiableObject(object);
    assertEquals(object.names(), unmodifiableObject.names());
    assertSame(object.get("yogurt"), unmodifiableObject.get("yogurt"));
  }

  @Test
  public void unmodifiableObjectReflectsChanges() {
    JsonObject unmodifiableObject = JsonObject.unmodifiableObject(object);
    object.add("yogurt", 92);
    assertEquals(object.names(), unmodifiableObject.names());
    assertSame(object.get("yogurt"), unmodifiableObject.get("yogurt"));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void unmodifiableObjectPreventsModification() {
    JsonObject unmodifiableObject = JsonObject.unmodifiableObject(object);
    unmodifiableObject.add("yogurt", 92);
  }

  @Test
  public void isEmptyTrueAfterCreation() {
    assertTrue(object.isEmpty());
  }

  @Test
  public void isEmptyFalseAfterAdd() {
    object.add("yogurt", true);
    assertFalse(object.isEmpty());
  }

  @Test
  public void sizeZeroAfterCreation() {
    assertEquals(0, object.size());
  }

  @Test
  public void sizeOneAfterAdd() {
    object.add("a", true);
    assertEquals(1, object.size());
  }

  /**
   * @since 0.8.0
   */
  @Test
  public void keyRepetitionAllowsMultipleEntries() {
    object.add("a", true);
    object.add("a", "value");
    assertEquals(2, object.size());
  }

  /**
   * @since 0.8.0
   */
  @Test
  public void keyRepetitionGetsLastEntry() {
    object.add("a", true);
    object.add("a", "value");
    assertEquals("value", object.getString("a", "missing"));
  }

  /**
   * @since 0.8.0
   */
  @Test
  public void keyRepetitionEqualityConsidersRepetitions() {
    object.add("a", true);
    object.add("a", "value");

    JsonObject onlyFirstProperty = new JsonObject();
    onlyFirstProperty.add("a", true);
    assertNotEquals(onlyFirstProperty, object);

    JsonObject bothProperties = new JsonObject();
    bothProperties.add("a", true);
    bothProperties.add("a", "value");
    assertEquals(bothProperties, object);
  }

  @Test
  public void namesEmptyAfterCreation() {
    assertTrue(object.names().isEmpty());
  }

  @Test
  public void namesContainsNameAfterAdd() {
    object.add("yogurt", true);
    List<String> names = object.names();
    assertEquals(1, names.size());
    assertEquals("yogurt", names.get(0));
  }

  @Test
  public void namesReflectsChanges() {
    List<String> names = object.names();
    object.add("yogurt", true);
    assertEquals(1, names.size());
    assertEquals("yogurt", names.get(0));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void namesPreventsModification() {
    List<String> names = object.names();
    names.add("yogurt");
  }

  @Test
  public void iteratorIsEmptyAfterCreation() {
    assertFalse(object.iterator().hasNext());
  }

  @Test
  public void iteratorHasNextAfterAdd() {
    object.add("a", true);
    Iterator<Member> iterator = object.iterator();
    assertTrue(iterator.hasNext());
  }

  @Test
  public void iteratorNextReturnsActualValue() {
    object.add("a", true);
    Iterator<Member> iterator = object.iterator();
    assertEquals(new Member("a", Json.TRUE), iterator.next());
  }

  @Test
  public void iteratorNextProgressesToNextValue() {
    object.add("a", true);
    object.add("b", false);
    Iterator<Member> iterator = object.iterator();
    iterator.next();
    assertTrue(iterator.hasNext());
    assertEquals(new Member("b", Json.FALSE), iterator.next());
  }

  @Test(expected = NoSuchElementException.class)
  public void iteratorNextFailsAtEnd() {
    Iterator<Member> iterator = object.iterator();
    iterator.next();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void iteratorDoesNotAllowModification() {
    object.add("a", 92);
    Iterator<Member> iterator = object.iterator();
    iterator.next();
    iterator.remove();
  }

  @Test(expected = ConcurrentModificationException.class)
  public void iteratorDetectsConcurrentModification() {
    Iterator<Member> iterator = object.iterator();
    object.add("a", 92);
    iterator.next();
  }

  @Test
  public void getFailsWithNullName() {
    assertException(NullPointerException.class, "name is null", new Runnable() {
      public void run() {
        object.get(null);
      }
    });
  }

  @Test
  public void getReturnsNullForNonExistingMember() {
    assertNull(object.get("yogurt"));
  }

  @Test
  public void getReturnsValueForName() {
    object.add("yogurt", true);
    assertEquals(Json.TRUE, object.get("yogurt"));
  }

  @Test
  public void getReturnsLastValueForName() {
    object.add("yogurt", false).add("yogurt", true);
    assertEquals(Json.TRUE, object.get("yogurt"));
  }

  @Test
  public void getIntReturnsValueFromMember() {
    object.add("yogurt", 92);
    assertEquals(92, object.getInt("yogurt", 42));
  }

  @Test
  public void getIntReturnsDefaultForMissingMember() {
    assertEquals(92, object.getInt("yogurt", 92));
  }

  @Test
  public void getLongReturnsValueFromMember() {
    object.add("yogurt", 92l);
    assertEquals(92l, object.getLong("yogurt", 42l));
  }

  @Test
  public void getLongReturnsDefaultForMissingMember() {
    assertEquals(92l, object.getLong("yogurt", 92l));
  }

  @Test
  public void getFloatReturnsValueFromMember() {
    object.add("yogurt", 3.14f);
    assertEquals(3.14f, object.getFloat("yogurt", 1.41f), 0);
  }

  @Test
  public void getFloatReturnsDefaultForMissingMember() {
    assertEquals(3.14f, object.getFloat("yogurt", 3.14f), 0);
  }

  @Test
  public void getDoubleReturnsValueFromMember() {
    object.add("yogurt", 3.14);
    assertEquals(3.14, object.getDouble("yogurt", 1.41), 0);
  }

  @Test
  public void getDoubleReturnsDefaultForMissingMember() {
    assertEquals(3.14, object.getDouble("yogurt", 3.14), 0);
  }

  @Test
  public void getBooleanReturnsValueFromMember() {
    object.add("yogurt", true);
    assertTrue(object.getBoolean("yogurt", false));
  }

  @Test
  public void getBooleanReturnsDefaultForMissingMember() {
    assertFalse(object.getBoolean("yogurt", false));
  }

  @Test
  public void getStringReturnsValueFromMember() {
    object.add("yogurt", "coconut");
    assertEquals("coconut", object.getString("yogurt", "chocolate"));
  }

  @Test
  public void getStringReturnsDefaultForMissingMember() {
    assertEquals("chocolate", object.getString("yogurt", "chocolate"));
  }

  @Test
  public void addFailsWithNullName() {
    assertException(NullPointerException.class, "name is null", new Runnable() {
      public void run() {
        object.add(null, 92);
      }
    });
  }

  @Test
  public void addInt() {
    object.add("a", 92);
    assertEquals("{\"a\":92}", object.toString());
  }

  @Test
  public void addIntEnablesChaining() {
    assertSame(object, object.add("a", 92));
  }

  @Test
  public void addLong() {
    object.add("a", 92l);
    assertEquals("{\"a\":92}", object.toString());
  }

  @Test
  public void addLongEnablesChaining() {
    assertSame(object, object.add("a", 92l));
  }

  @Test
  public void addFloat() {
    object.add("a", 3.14f);
    assertEquals("{\"a\":3.14}", object.toString());
  }

  @Test
  public void addFloatEnablesChaining() {
    assertSame(object, object.add("a", 3.14f));
  }

  @Test
  public void addDouble() {
    object.add("a", 3.14d);
    assertEquals("{\"a\":3.14}", object.toString());
  }

  @Test
  public void addDoubleEnablesChaining() {
    assertSame(object, object.add("a", 3.14d));
  }

  @Test
  public void addBoolean() {
    object.add("a", true);
    assertEquals("{\"a\":true}", object.toString());
  }

  @Test
  public void addBooleanEnablesChaining() {
    assertSame(object, object.add("a", true));
  }

  @Test
  public void addString() {
    object.add("a", "yogurt");
    assertEquals("{\"a\":\"yogurt\"}", object.toString());
  }

  @Test
  public void addStringToleratesNull() {
    object.add("a", (String)null);
    assertEquals("{\"a\":null}", object.toString());
  }

  @Test
  public void addStringEnablesChaining() {
    assertSame(object, object.add("a", "yogurt"));
  }

  @Test
  public void addJsonNull() {
    object.add("a", Json.NULL);
    assertEquals("{\"a\":null}", object.toString());
  }

  @Test
  public void addJsonArray() {
    object.add("a", new JsonArray());
    assertEquals("{\"a\":[]}", object.toString());
  }

  @Test
  public void addJsonObject() {
    object.add("a", new JsonObject());
    assertEquals("{\"a\":{}}", object.toString());
  }

  @Test
  public void addJsonEnablesChaining() {
    assertSame(object, object.add("a", Json.NULL));
  }

  @Test
  public void addJsonFailsWithNull() {
    assertException(NullPointerException.class, "value is null", new Runnable() {
      public void run() {
        object.add("a", (JsonValue)null);
      }
    });
  }

  @Test
  public void addJsonNestedArray() {
    JsonArray innerArray = new JsonArray();
    innerArray.add(92);
    object.add("a", innerArray);
    assertEquals("{\"a\":[92]}", object.toString());
  }

  @Test
  public void addJsonNestedArrayModifiedAfterAdd() {
    JsonArray innerArray = new JsonArray();
    object.add("a", innerArray);
    innerArray.add(92);
    assertEquals("{\"a\":[92]}", object.toString());
  }

  @Test
  public void addJsonNestedObject() {
    JsonObject innerObject = new JsonObject();
    innerObject.add("a", 92);
    object.add("a", innerObject);
    assertEquals("{\"a\":{\"a\":92}}", object.toString());
  }

  @Test
  public void addJsonNestedObjectModifiedAfterAdd() {
    JsonObject innerObject = new JsonObject();
    object.add("a", innerObject);
    innerObject.add("a", 92);
    assertEquals("{\"a\":{\"a\":92}}", object.toString());
  }

  @Test
  public void setInt() {
    object.set("a", 92);
    assertEquals("{\"a\":92}", object.toString());
  }

  @Test
  public void setIntEnablesChaining() {
    assertSame(object, object.set("a", 92));
  }

  @Test
  public void setLong() {
    object.set("a", 92l);
    assertEquals("{\"a\":92}", object.toString());
  }

  @Test
  public void setLongEnablesChaining() {
    assertSame(object, object.set("a", 92l));
  }

  @Test
  public void setFloat() {
    object.set("a", 3.14f);
    assertEquals("{\"a\":3.14}", object.toString());
  }

  @Test
  public void setFloatEnablesChaining() {
    assertSame(object, object.set("a", 3.14f));
  }

  @Test
  public void setDouble() {
    object.set("a", 3.14d);
    assertEquals("{\"a\":3.14}", object.toString());
  }

  @Test
  public void setDoubleEnablesChaining() {
    assertSame(object, object.set("a", 3.14d));
  }

  @Test
  public void setBoolean() {
    object.set("a", true);
    assertEquals("{\"a\":true}", object.toString());
  }

  @Test
  public void setBooleanEnablesChaining() {
    assertSame(object, object.set("a", true));
  }

  @Test
  public void setString() {
    object.set("a", "yogurt");
    assertEquals("{\"a\":\"yogurt\"}", object.toString());
  }

  @Test
  public void setStringEnablesChaining() {
    assertSame(object, object.set("a", "yogurt"));
  }

  @Test
  public void setJsonNull() {
    object.set("a", Json.NULL);
    assertEquals("{\"a\":null}", object.toString());
  }

  @Test
  public void setJsonArray() {
    object.set("a", new JsonArray());
    assertEquals("{\"a\":[]}", object.toString());
  }

  @Test
  public void setJsonObject() {
    object.set("a", new JsonObject());
    assertEquals("{\"a\":{}}", object.toString());
  }

  @Test
  public void setJsonEnablesChaining() {
    assertSame(object, object.set("a", Json.NULL));
  }

  @Test
  public void setAddsElementIfMissing() {
    object.set("a", Json.TRUE);
    assertEquals("{\"a\":true}", object.toString());
  }

  @Test
  public void setModifiesElementIfExisting() {
    object.add("a", Json.TRUE);
    object.set("a", Json.FALSE);
    assertEquals("{\"a\":false}", object.toString());
  }

  @Test
  public void setModifiesLastElementIfMultipleExisting() {
    object.add("a", 1);
    object.add("a", 2);
    object.set("a", Json.TRUE);
    assertEquals("{\"a\":1,\"a\":true}", object.toString());
  }

  @Test
  public void removeFailsWithNullName() {
    assertException(NullPointerException.class, "name is null", new Runnable() {
      public void run() {
        object.remove(null);
      }
    });
  }

  @Test
  public void removeRemovesMatchingMember() {
    object.add("a", 92);
    object.remove("a");
    assertEquals("{}", object.toString());
  }

  @Test
  public void removeRemovesOnlyMatchingMember() {
    object.add("a", 92);
    object.add("b", 42);
    object.add("c", true);
    object.remove("b");
    assertEquals("{\"a\":92,\"c\":true}", object.toString());
  }

  @Test
  public void removeRemovesOnlyLastMatchingMember() {
    object.add("a", 92);
    object.add("a", 42);
    object.remove("a");
    assertEquals("{\"a\":92}", object.toString());
  }

  @Test
  public void removeRemovesOnlyLastMatchingMemberAfterRemove() {
    object.add("a", 92);
    object.remove("a");
    object.add("a", 42);
    object.add("a", 47);
    object.remove("a");
    assertEquals("{\"a\":42}", object.toString());
  }

  @Test
  public void removeDoesNotModifyObjectWithoutMatchingMember() {
    object.add("a", 92);
    object.remove("b");
    assertEquals("{\"a\":92}", object.toString());
  }

  @Test
  public void mergeFailsWithNull() {
    assertException(NullPointerException.class, "object is null", new Runnable() {
      public void run() {
        object.merge(null);
      }
    });
  }

  @Test
  public void mergeAppendsMembers() {
    object.add("a", 1).add("b", 1);
    object.merge(Json.object().add("c", 2).add("d", 2));
    assertEquals(Json.object().add("a", 1).add("b", 1).add("c", 2).add("d", 2), object);
  }

  @Test
  public void mergeReplacesMembers() {
    object.add("a", 1).add("b", 1).add("c", 1);
    object.merge(Json.object().add("b", 2).add("d", 2));
    assertEquals(Json.object().add("a", 1).add("b", 2).add("c", 1).add("d", 2), object);
  }

  @Test
  public void writeEmpty() throws IOException {
    JsonWriter writer = mock(JsonWriter.class);
    object.write(writer);
    InOrder inOrder = inOrder(writer);
    inOrder.verify(writer).writeObjectOpen();
    inOrder.verify(writer).writeObjectClose();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void writeWithSingleValue() throws IOException {
    JsonWriter writer = mock(JsonWriter.class);
    object.add("a", 92);
    object.write(writer);
    InOrder inOrder = inOrder(writer);
    inOrder.verify(writer).writeObjectOpen();
    inOrder.verify(writer).writeMemberName("a");
    inOrder.verify(writer).writeMemberSeparator();
    inOrder.verify(writer).writeNumber("92");
    inOrder.verify(writer).writeObjectClose();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void writeWithMultipleValues() throws IOException {
    JsonWriter writer = mock(JsonWriter.class);
    object.add("a", 92);
    object.add("b", 3.14f);
    object.add("c", "yogurt");
    object.add("d", true);
    object.add("e", (String)null);
    object.write(writer);
    InOrder inOrder = inOrder(writer);
    inOrder.verify(writer).writeObjectOpen();
    inOrder.verify(writer).writeMemberName("a");
    inOrder.verify(writer).writeMemberSeparator();
    inOrder.verify(writer).writeNumber("92");
    inOrder.verify(writer).writeObjectSeparator();
    inOrder.verify(writer).writeMemberName("b");
    inOrder.verify(writer).writeMemberSeparator();
    inOrder.verify(writer).writeNumber("3.14");
    inOrder.verify(writer).writeObjectSeparator();
    inOrder.verify(writer).writeMemberName("c");
    inOrder.verify(writer).writeMemberSeparator();
    inOrder.verify(writer).writeString("yogurt");
    inOrder.verify(writer).writeObjectSeparator();
    inOrder.verify(writer).writeMemberName("d");
    inOrder.verify(writer).writeMemberSeparator();
    inOrder.verify(writer).writeLiteral("true");
    inOrder.verify(writer).writeObjectSeparator();
    inOrder.verify(writer).writeMemberName("e");
    inOrder.verify(writer).writeMemberSeparator();
    inOrder.verify(writer).writeLiteral("null");
    inOrder.verify(writer).writeObjectClose();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void isObject() {
    assertTrue(object.isObject());
  }

  @Test
  public void asObject() {
    assertSame(object, object.asObject());
  }

  @Test
  public void equalsTrueForSameInstance() {
    assertTrue(object.equals(object));
  }

  @Test
  public void equalsTrueForEqualObjects() {
    assertTrue(object().equals(object()));
    assertTrue(object("a", "1", "b", "2").equals(object("a", "1", "b", "2")));
  }

  @Test
  public void equalsFalseForDifferentObjects() {
    assertFalse(object("a", "1").equals(object("a", "2")));
    assertFalse(object("a", "1").equals(object("b", "1")));
    assertFalse(object("a", "1", "b", "2").equals(object("b", "2", "a", "1")));
  }

  @Test
  public void equalsFalseForNull() {
    assertFalse(new JsonObject().equals(null));
  }

  @Test
  public void equalsFalseForSubclass() {
    JsonObject jsonObject = new JsonObject();
    assertFalse(jsonObject.equals(new JsonObject(jsonObject) {}));
  }

  @Test
  public void hashCodeEqualsForEqualObjects() {
    assertTrue(object().hashCode() == object().hashCode());
    assertTrue(object("a", "1").hashCode() == object("a", "1").hashCode());
  }

  @Test
  public void hashCodeDiffersForDifferentObjects() {
    assertFalse(object().hashCode() == object("a", "1").hashCode());
    assertFalse(object("a", "1").hashCode() == object("a", "2").hashCode());
    assertFalse(object("a", "1").hashCode() == object("b", "1").hashCode());
  }

  @Test
  public void indexOfReturnsNoIndexIfEmpty() {
    assertEquals(-1, object.indexOf("a"));
  }

  @Test
  public void indexOfReturnsIndexOfMember() {
    object.add("a", true);
    assertEquals(0, object.indexOf("a"));
  }

  @Test
  public void indexOfReturnsIndexOfLastMember() {
    object.add("a", true);
    object.add("a", true);
    assertEquals(1, object.indexOf("a"));
  }

  @Test
  public void indexOfReturnsIndexOfLastMemberAfterRemove() {
    object.add("a", true);
    object.add("a", true);
    object.remove("a");
    assertEquals(0, object.indexOf("a"));
  }

  @Test
  public void indexOfReturnsUpdatedIndexAfterRemove() {
    object.add("a", true);
    object.add("b", true);
    object.remove("a");
    assertEquals(0, object.indexOf("b"));
  }

  @Test
  public void indexOfReturnsIndexOfLastMemberForBigObject() {
    object.add("a", true);
    /* The hash index table does not return a value for indexes above 255 */
    for (int i = 0; i < 256; i++) {
      object.add("x-" + i, 0);
    }
    object.add("a", true);
    assertEquals(257, object.indexOf("a"));
  }

  @Test
  public void hashIndexTableCopyConstructor() {
    HashIndexTable original = new HashIndexTable();
    original.add("name", 92);
    HashIndexTable copy = new HashIndexTable(original);
    assertEquals(92, copy.get("name"));
  }

  @Test
  public void hashIndexTableAdd() {
    HashIndexTable indexTable = new HashIndexTable();
    indexTable.add("name-0", 0);
    indexTable.add("name-1", 1);
    indexTable.add("name-fe", 0xfe);
    indexTable.add("name-ff", 0xff);
    assertEquals(0, indexTable.get("name-0"));
    assertEquals(1, indexTable.get("name-1"));
    assertEquals(0xfe, indexTable.get("name-fe"));
    assertEquals(-1, indexTable.get("name-ff"));
  }

  @Test
  public void hashIndexTableAddOverwritesPreviousValue() {
    HashIndexTable indexTable = new HashIndexTable();
    indexTable.add("name", 92);
    indexTable.add("name", 42);
    assertEquals(42, indexTable.get("name"));
  }

  @Test
  public void hashIndexTableAddClearsPreviousValueIfIndexExceeds0xff() {
    HashIndexTable indexTable = new HashIndexTable();
    indexTable.add("name", 92);
    indexTable.add("name", 300);
    assertEquals(-1, indexTable.get("name"));
  }

  @Test
  public void hashIndexTableRemove() {
    HashIndexTable indexTable = new HashIndexTable();
    indexTable.add("name", 92);
    indexTable.remove(92);
    assertEquals(-1, indexTable.get("name"));
  }

  @Test
  public void hashIndexTableRemoveUpdatesSubsequentElements() {
    HashIndexTable indexTable = new HashIndexTable();
    indexTable.add("yogurt", 92);
    indexTable.add("coconut", 42);
    indexTable.remove(92);
    assertEquals(42, indexTable.get("coconut"));
  }

  @Test
  public void hashIndexTableRemoveDoesNotChangePrecedingElements() {
    HashIndexTable indexTable = new HashIndexTable();
    indexTable.add("yogurt", 23);
    indexTable.add("coconut", 42);
    indexTable.remove(42);
    assertEquals(23, indexTable.get("yogurt"));
  }

  @Test
  public void canBeSerializedAndDeserialized() throws Exception {
    object.add("yogurt", 92).add("coconut", new JsonObject().add("a", 3.14d).add("b", true));
    assertEquals(object, serializeAndDeserialize(object));
  }

  @Test
  public void deserializedObjectCanBeAccessed() throws Exception {
    object.add("yogurt", 92);
    JsonObject deserializedObject = serializeAndDeserialize(object);
    assertEquals(92, deserializedObject.get("yogurt").asInt());
  }

  @Test
  public void memberReturnsNameAndValue() {
    Member member = new Member("a", Json.TRUE);
    assertEquals("a", member.getName());
    assertEquals(Json.TRUE, member.getValue());
  }

  @Test
  public void memberEqualsTrueForSameInstance() {
    Member member = new Member("a", Json.TRUE);
    assertTrue(member.equals(member));
  }

  @Test
  public void memberEqualsTrueForEqualObjects() {
    Member member = new Member("a", Json.TRUE);
    assertTrue(member.equals(new Member("a", Json.TRUE)));
  }

  @Test
  public void memberEqualsFalseForDifferingObjects() {
    Member member = new Member("a", Json.TRUE);
    assertFalse(member.equals(new Member("b", Json.TRUE)));
    assertFalse(member.equals(new Member("a", Json.FALSE)));
  }

  @Test
  public void memberEqualsFalseForNull() {
    Member member = new Member("a", Json.TRUE);
    assertFalse(member.equals(null));
  }

  @Test
  public void memberEqualsFalseForSubclass() {
    Member member = new Member("a", Json.TRUE);
    assertFalse(member.equals(new Member("a", Json.TRUE) {}));
  }

  @Test
  public void memberHashCodeEqualsForEqualObjects() {
    Member member = new Member("a", Json.TRUE);
    assertTrue(member.hashCode() == new Member("a", Json.TRUE).hashCode());
  }

  @Test
  public void memberHashCodeDiffersForDifferingObjects() {
    Member member = new Member("a", Json.TRUE);
    assertFalse(member.hashCode() == new Member("b", Json.TRUE).hashCode());
    assertFalse(member.hashCode() == new Member("a", Json.FALSE).hashCode());
  }

  private static JsonObject object(String... namesAndValues) {
    JsonObject object = new JsonObject();
    for (int i = 0; i < namesAndValues.length; i += 2) {
      object.add(namesAndValues[i], namesAndValues[i + 1]);
    }
    return object;
  }
}
