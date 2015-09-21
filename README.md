# AndroidJsonModel
Json model 转换，支持修改父类json变量的key值

## Import
[JitPack](https://jitpack.io/)

Add it in your project's build.gradle at the end of repositories:

```gradle
repositories {
  // ...
  maven { url "https://jitpack.io" }
}
```

Step 2. Add the dependency in the form

```gradle
dependencies {
  compile 'com.github.vilyever:AndroidJsonModel:1.0.1'
}
```

## Usage
```java

void main() {
  // create a sample model to generate json string
  Apple apple = new Apple();
  apple.name = "apple";
  apple.color = "red";
  
  // get sample json string
  String jsonString = apple.toJson().toString();
  // generate model from json string
  apple = new VDJson<>(Apple.class).modelFromJsonString(jsonString);
}

public static class Fruit extends VDModel {
    @VDJsonModelDelegate.VDJsonKey("nam")
    public String name;
}
public static class Apple extends Fruit {
    @VDJsonModelDelegate.VDJsonKey("col")
    public String color;

    @Override
    public HashMap<String, String> jsonKeyBindingDictionary() {
        HashMap dictionary =  super.jsonKeyBindingDictionary();
        // change the super json key
        // left is the var name, right is the json key
        // 建议使用 AndroidKeyPath 中的方法输入变量名，避免输入的变量名拼写错误
        dictionary.put("name", "aname");
        return dictionary;
    }
}
```

## License
[Apache License Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt)

