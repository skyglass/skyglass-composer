package io.eventuate.tram.examples.todolist.common;


import java.util.UUID;

public class Utils {
  public static String generateUniqueString() {
    return UUID.randomUUID().toString();
  }
}
