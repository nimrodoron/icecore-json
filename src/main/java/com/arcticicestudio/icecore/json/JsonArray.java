/*
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
title      JSON Array                                      +
project    icecore-json                                    +
version    0.8.0-frost.1                                   +
repository https://github.com/arcticicestudio/icecore-json +
author     Arctic Ice Studio                               +
email      development@arcticicestudio.com                 +
copyright  Copyright (C) 2016                              +
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
*/
package com.arcticicestudio.icecore.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a JSON array, an ordered collection of JSON values.
 * <p>
 *   Elements can be added using the {@code add(...)} methods which accept instances of {@link JsonValue}, strings,
 *   primitive numbers and boolean values.
 *   To replace an element of an array, use the {@code set(int, ...)} methods.
 * </p>
 * <p>
 *   Elements can be accessed by their index using {@link #get(int)}.
 *   This class also supports iterating over the elements in document order using an {@link #iterator()} or an
 *   enhanced for loop:
 * <pre>
 * for (JsonValue value : jsonArray) {
 *   ...
 * }
 * </pre>
 * <p>
 *   An equivalent {@link List} can be obtained from the method {@link #values()}.
 * </p>
 * <p>
 *   Note that this class is <strong>not thread-safe!</strong><br>
 * </p>
 * <p>
 *   If multiple threads access a {@link JsonArray} instance concurrently, while at least one of these threads modifies
 *   the contents of this array, access to the instance must be synchronized externally.
 *   Failure to do so may lead to an inconsistent state.
 * </p>
 * <p>
 *   This class is <strong>not supposed to be extended</strong> by clients.
 * </p>
 *
 * @author Arctic Ice Studio &lt;development@arcticicestudio.com&gt;
 * @since 0.2.0
 */
public class JsonArray extends JsonValue implements Iterable<JsonValue> {

  private final List<JsonValue> values;

  /**
   * Creates a new empty JsonArray.
   */
  public JsonArray() {
    values = new ArrayList<JsonValue>();
  }

  /**
   * Creates a new JsonArray with the contents of the specified JSON array.
   *
   * @param array the JsonArray to get the initial contents from which <strong>MUST NOT</strong> be {@code null}.
   */
  public JsonArray(JsonArray array) {
    this(array, false);
  }

  /**
   * Initializes the contents of the specified JSON array with a modifiability state based on the boolean value of the
   * {@code unmodifiable} parameter.
   *
   * @param array the JsonArray to get the initial contents from which <strong>MUST NOT</strong> be {@code null}.
   * @param unmodifiable the state of the modifiability
   */
  private JsonArray(JsonArray array, boolean unmodifiable) {
    if (array == null) {
      throw new NullPointerException("array is null");
    }
    if (unmodifiable) {
      values = Collections.unmodifiableList(array.values);
    } else {
      values = new ArrayList<JsonValue>(array.values);
    }
  }

  /**
   * Returns an unmodifiable wrapper for the specified JsonArray.
   * <p>
   *   This method allows to provide read-only access to a JsonArray.
   * </p>
   * <p>
   *   The returned JsonArray is backed by the given array and reflects subsequent changes.
   *   Attempts to modify the returned JsonArray result in an {@link UnsupportedOperationException}.
   * </p>
   *
   * @param array the JsonArray for which an unmodifiable JsonArray is to be returned
   * @return an unmodifiable view of the specified JsonArray
   */
  public static JsonArray unmodifiableArray(JsonArray array) {
    return new JsonArray(array, true);
  }

  /**
   * Appends the JSON representation of the specified {@code int} value to the end of this array.
   *
   * @param value the value to add to the array
   * @return the array itself, to enable method chaining
   */
  public JsonArray add(int value) {
    values.add(Json.value(value));
    return this;
  }

  /**
   * Appends the JSON representation of the specified {@code long} value to the end of this array.
   *
   * @param value the value to add to the array
   * @return the array itself, to enable method chaining
   */
  public JsonArray add(long value) {
    values.add(Json.value(value));
    return this;
  }

  /**
   * Appends the JSON representation of the specified {@code float} value to the end of this array.
   *
   * @param value the value to add to the array
   * @return the array itself, to enable method chaining
   */
  public JsonArray add(float value) {
    values.add(Json.value(value));
    return this;
  }

  /**
   * Appends the JSON representation of the specified {@code double} value to the end of this array.
   *
   * @param value the value to add to the array
   * @return the array itself, to enable method chaining
   */
  public JsonArray add(double value) {
    values.add(Json.value(value));
    return this;
  }

  /**
   * Appends the JSON representation of the specified {@code boolean} value to the end of this array.
   *
   * @param value the value to add to the array
   * @return the array itself, to enable method chaining
   */
  public JsonArray add(boolean value) {
    values.add(Json.value(value));
    return this;
  }

  /**
   * Appends the JSON representation of the specified string to the end of this array.
   *
   * @param value the string to add to the array
   * @return the array itself, to enable method chaining
   */
  public JsonArray add(String value) {
    values.add(Json.value(value));
    return this;
  }

  /**
   * Appends the specified JSON value to the end of this array.
   *
   * @param value the JsonValue to add to the array which <strong>MUST NOT</strong> be {@code null}
   * @return the array itself, to enable method chaining
   */
  public JsonArray add(JsonValue value) {
    if (value == null) {
      throw new NullPointerException("value is null");
    }
    values.add(value);
    return this;
  }

  /**
   * Replaces the element at the specified position in this array with the JSON representation of the specified
   * {@code int} value.
   *
   * @param index the index of the array element to replace
   * @param value the value to be stored at the specified array position
   * @return the array itself, to enable method chaining
   * @throws IndexOutOfBoundsException if the index is out of range ({@code index &lt; 0} or {@code index &gt;= size})
   */
  public JsonArray set(int index, int value) {
    values.set(index, Json.value(value));
    return this;
  }

  /**
   * Replaces the element at the specified position in this array with the JSON representation of the specified
   * {@code long} value.
   *
   * @param index the index of the array element to replace
   * @param value the value to be stored at the specified array position
   * @return the array itself, to enable method chaining
   * @throws IndexOutOfBoundsException if the index is out of range ({@code index &lt; 0} or {@code index &gt;= size})
   */
  public JsonArray set(int index, long value) {
    values.set(index, Json.value(value));
    return this;
  }

  /**
   * Replaces the element at the specified position in this array with the JSON representation of the specified
   * {@code float} value.
   *
   * @param index the index of the array element to replace
   * @param value the value to be stored at the specified array position
   * @return the array itself, to enable method chaining
   * @throws IndexOutOfBoundsException if the index is out of range ({@code index &lt; 0} or {@code index &gt;= size})
   */
  public JsonArray set(int index, float value) {
    values.set(index, Json.value(value));
    return this;
  }

  /**
   * Replaces the element at the specified position in this array with the JSON representation of the specified
   * {@code double} value.
   *
   * @param index the index of the array element to replace
   * @param value the value to be stored at the specified array position
   * @return the array itself, to enable method chaining
   * @throws IndexOutOfBoundsException if the index is out of range ({@code index &lt; 0} or {@code index &gt;= size})
   */
  public JsonArray set(int index, double value) {
    values.set(index, Json.value(value));
    return this;
  }

  /**
   * Replaces the element at the specified position in this array with the JSON representation of the specified
   * {@code boolean} value.
   *
   * @param index the index of the array element to replace
   * @param value the value to be stored at the specified array position
   * @return the array itself, to enable method chaining
   * @throws IndexOutOfBoundsException if the index is out of range ({@code index &lt; 0} or {@code index &gt;= size})
   */
  public JsonArray set(int index, boolean value) {
    values.set(index, Json.value(value));
    return this;
  }

  /**
   * Replaces the element at the specified position in this array with the JSON representation of the specified string.
   *
   * @param index the index of the array element to replace
   * @param value the string to be stored at the specified array position
   * @return the array itself, to enable method chaining
   * @throws IndexOutOfBoundsException if the index is out of range ({@code index &lt; 0} or {@code index &gt;= size})
   */
  public JsonArray set(int index, String value) {
    values.set(index, Json.value(value));
    return this;
  }

  /**
   * Replaces the element at the specified position in this array with the specified JSON value.
   *
   * @param index the index of the array element to replace
   * @param value the value to be stored at the specified array position, must not be {@code null}
   * @return the array itself, to enable method chaining
   * @throws IndexOutOfBoundsException if the index is out of range ({@code index &lt; 0} or {@code index &gt;= size})
   */
  public JsonArray set(int index, JsonValue value) {
    if (value == null) {
      throw new NullPointerException("value is null");
    }
    values.set(index, value);
    return this;
  }

  /**
   * Removes the element at the specified index from this array.
   *
   * @param index the index of the element to remove
   * @return the array itself, to enable method chaining
   * @throws IndexOutOfBoundsException if the index is out of range ({@code index &lt; 0} or {@code index &gt;= size})
   */
  public JsonArray remove(int index) {
    values.remove(index);
    return this;
  }

  /**
   * Returns the number of elements in this array.
   *
   * @return the number of elements in this array
   */
  public int size() {
    return values.size();
  }

  /**
   * Returns {@code true} if this array contains no elements.
   *
   * @return {@code true} if this array contains no elements
   */
  public boolean isEmpty() {
    return values.isEmpty();
  }

  /**
   * Returns the value of the element at the specified position in this array.
   *
   * @param index the index of the array element to return
   * @return the value of the element at the specified position
   * @throws IndexOutOfBoundsException if the index is out of range ({@code index &lt; 0} or {@code index &gt;= size})
   */
  public JsonValue get(int index) {
    return values.get(index);
  }

  /**
   * Returns a list of the values in this array in document order.
   * <p>
   *   The returned list is backed by this array and will reflect subsequent changes.
   *   <strong>It cannot be used to modify this array!</strong>
   *   Attempts to modify the returned list will result in an exception.
   * </p>
   *
   * @return a list of the values in this array
   */
  public List<JsonValue> values() {
    return Collections.unmodifiableList(values);
  }

  /**
   * Returns an iterator over the values of this array in document order.
   * <p>
   *   <strong>The returned iterator cannot be used to modify this array!</strong>
   * </p>
   *
   * @return an iterator over the values of this array
   */
  public Iterator<JsonValue> iterator() {
    final Iterator<JsonValue> iterator = values.iterator();
    return new Iterator<JsonValue>() {

      public boolean hasNext() {
        return iterator.hasNext();
      }

      public JsonValue next() {
        return iterator.next();
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  /**
   * {@inheritDoc}
   *
   * @since 0.5.0
   */
  @Override
  void write(JsonWriter writer) throws IOException {
    writer.writeArrayOpen();
    Iterator<JsonValue> iterator = iterator();
    if (iterator.hasNext()) {
      iterator.next().write(writer);
      while (iterator.hasNext()) {
        writer.writeArraySeparator();
        iterator.next().write(writer);
      }
    }
    writer.writeArrayClose();
  }

  @Override
  public boolean isArray() {
    return true;
  }

  @Override
  public JsonArray asArray() {
    return this;
  }

  @Override
  public int hashCode() {
    return values.hashCode();
  }

  /**
   * Indicates whether a given object is "equal to" this JsonArray.
   * <p>
   *   An object is considered equal if it is also a {@code JsonArray} and both arrays contain the same list of values.
   * </p>
   * <p>
   *   If two JsonArrays are equal, they will also produce the same JSON output.
   * </p>
   *
   * @param object the object to be compared with this JsonArray
   * @return {@code true} if the specified object is equal to this JsonArray, {@code false} otherwise
   */
  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null) {
      return false;
    }
    if (getClass() != object.getClass()) {
      return false;
    }
    JsonArray other = (JsonArray)object;
    return values.equals(other.values);
  }
}