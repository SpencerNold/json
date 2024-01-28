# JSON Library for the Java Programming Language
Reads and writes Java object files to the JSON format, supporting POJO classes, limited Java data structures, as well as programmable JsonComponent classes.

## How to build
The project is a very generic gradle project, to build it, use the gradle wrapper.
```
./gradlew - UNIX gradle wrapper command
gradlew   - Windows gradle wrapper command

gradlew eclipse - Set up the project for Eclipse
gradlew idea    - Set up the project for IntelliJ Idea
gradlew build   - Compile the project
```

## Basic Programming with the Library
There is a simple static class for fast, easy use, `me.spencernold.json.Json` which includes some simple object->json and json->object functions.
```java
public class Test {
     private int a = 4;
     private String b = "Hello world!";
     private List<String> list = Arrays.asList("1", "2", 3);

     public static void main(String[] args) {
          Test test = new Test();
          String jsonText = Json.toJson(test); // Json string of what our Test object is
          test = Json.fromJson(jsonTest, Test.class); // Reads the Json string into the Test Object
     }
}
```
## Reading Complex Structures
Using our `Json.fromJson` function with the Class argument has the downside of not allowing List or Map objects to be read. Using the `TypeDef<T>` class, as a parameter instead, we can now read those data structures.
```java
     // Using the same Test class as before
     public static void main(String[] args) {
          List<String> list = Arrays.asList(new Test(), new Test(), new Test());
          String jsonText = Json.toJson(list);
          list = Json.fromJson(jsonText, TypeDef.ofList(List.class, Test.class));
     }
```

## Skipping Fields
Any fields in a class that you don't want written to the Json format can be ignored with the `@JsonIgnore` annotation, and the JsonWriter will leave them completely untouched.
## Components
For each data type in the Json format, there are implementations of the `JsonComponent` class, including `JsonObject`, `JsonArray`, and `JsonPrimitive`. These classes work as Java representations of Json object members, and the `toString()` function has been overridden to return the Json text representation of them.
