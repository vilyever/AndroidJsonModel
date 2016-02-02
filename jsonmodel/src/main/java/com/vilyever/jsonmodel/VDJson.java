package com.vilyever.jsonmodel;

import android.support.annotation.Nullable;

import com.vilyever.reflectkit.VDReflectKit;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * VDJson
 * AndroidJsonModelConverter <com.vilyever.jsonmodelconverter>
 * Created by vilyever on 2015/8/18.
 * Feature:
 */
public class VDJson<T extends VDJsonModelProtocol> {
    private final VDJson self = this;

    private static final String ClassNameJsonKey = "VD_CN";

    public static final VDReflectKit.FieldsExclusionDelegate ReflectExclusionDelegate = new VDReflectKit.FieldsExclusionDelegate() {
        @Override
        public boolean shouldExclude(Field field) {
            return (field.getModifiers() & Modifier.STATIC) != 0 || (field.getModifiers() & Modifier.FINAL) != 0 || (field.getModifiers() & Modifier.SYNCHRONIZED) != 0 || (field.getModifiers() & Modifier.VOLATILE) != 0 || (field.getModifiers() & Modifier.TRANSIENT) != 0 || (field.getModifiers() & Modifier.NATIVE) != 0 || (field.getModifiers() & Modifier.INTERFACE) != 0 || (field.getModifiers() & Modifier.STRICT) != 0 || (field.getModifiers() & Modifier.ABSTRACT) != 0 || null != field.getAnnotation(VDJsonModelProtocol.VDJsonKeyIgnore.class);
        }
    };

    final Class<T> modelClazz;

    /* #Constructors */
    public VDJson(Class<T> modelClazz) {
        this.modelClazz = modelClazz;
        if (!VDJsonModelProtocol.class.isAssignableFrom(modelClazz)) {
            throw new IllegalStateException("The Model to create must implements VDJsonModelDelegate");
        }
    }

    /* Public Methods */
    /**
     * 转换json为model
     * @param json 原json
     * @return 转换的model
     */
    @Nullable
    public T modelFromJson(JSONObject json) {
        if (json == null) {
            return null;
        }

        Class<?> typeClazz = self.modelClazz;
        try {
            if (json.has(ClassNameJsonKey)) {
                typeClazz = Class.forName(json.getString(ClassNameJsonKey));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        T model;
        try {
            // alloc a new model instance
            model = (T) typeClazz.newInstance();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // get the jsonKey binding dic, ("propertyName" : "jsonKey")
        HashMap<String, String> jsonKeyBindingDictionary = model.getJsonKeyBindingDictionary();
        if (jsonKeyBindingDictionary == null) {
            jsonKeyBindingDictionary = new HashMap<>();
        }

        // get all property fields from the model class up to Model_VDKit.class
        List<Field> fields = VDReflectKit.getFields(typeClazz, VDJsonModelProtocol.class, ReflectExclusionDelegate);

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                String fieldName = field.getName();

                if (jsonKeyBindingDictionary.containsKey(fieldName) ) {
                    // if property'Name Reflect the Json Key in getJsonKeyBindingDictionary, set it
                    String jsonKey = jsonKeyBindingDictionary.get(fieldName);
                    PutJsonValueToField(field, model, json, jsonKey);
                }
                else if (field.getAnnotation(VDJsonModelProtocol.VDJsonKey.class) != null) {
                    // if the json value  no setted by binding keys, check if annotationJsonKey exist
                    String jsonKey = field.getAnnotation(VDJsonModelProtocol.VDJsonKey.class).value();
                    PutJsonValueToField(field, model, json, jsonKey);
                }
                else {
                    // if the json value no setted by binding keys and annotationJsonKey, set as property'name
                    String jsonKey = fieldName;
                    PutJsonValueToField(field, model, json, jsonKey);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return model;
    }

    /**
     * 转换json字符串为model
     * @param jsonString json字符串
     * @return model
     */
    @Nullable
    public T modelFromJsonString(String jsonString) {
        if (jsonString == null) {
            return null;
        }

        try {
            return (T) self.modelFromJson(new JSONObject(jsonString));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 转换json数组为model的ArrayList
     * @param jsonArray json数组
     * @return model集合
     */
    public ArrayList<T> modelsFromJsonArray(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }

        ArrayList<T> list = new ArrayList<>();
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            try {
                list.add(this.modelFromJson(jsonArray.getJSONObject(i)));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    /**
     * 转换json字符串为model的ArrayList
     * @param jsonString json字符串
     * @return model集合
     */
    public ArrayList<T> modelsFromJsonString(String jsonString) {
        if (jsonString == null) {
            return null;
        }

        try {
            return this.modelsFromJsonArray(new JSONArray(jsonString));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * model转换为json
     * @param model model
     * @return 转换的json
     */
    public static JSONObject ModelToJson(VDJsonModelProtocol model) {
        JSONObject jsonObject = new JSONObject();

        if (model == null) {
            return jsonObject;
        }

        // get the jsonKey binding dic, ("propertyName" : "jsonKey")
        HashMap<String, String> jsonKeyBindingDictionary = model.getJsonKeyBindingDictionary();
        if (jsonKeyBindingDictionary == null) {
            jsonKeyBindingDictionary = new HashMap<>();
        }

        // get all property fields from the model class up to Model_VDKit.class
        List<Field> fields = VDReflectKit.getFields(model.getClass(), VDJsonModelProtocol.class, ReflectExclusionDelegate);

        // set all property to json object
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                String fieldName = field.getName();
                String jsonKey = null;

                if (jsonKeyBindingDictionary.containsKey(fieldName) ) {
                    // if property'Name Reflect the Json Key in getJsonKeyBindingDictionary, set it
                    jsonKey = jsonKeyBindingDictionary.get(fieldName);
                }
                else if (null != field.getAnnotation(VDJsonModelProtocol.VDJsonKey.class) ) {
                    // if the json value no setted by binding keys, check if annotationJsonKey exist
                    jsonKey = field.getAnnotation(VDJsonModelProtocol.VDJsonKey.class).value();
                }
                else {
                    // if the json value no setted by binding keys and annotationJsonKey, set as property'name
                    jsonKey = fieldName;
                }

                Object value = GetJsonValueFromField(field, model);
                if (value != null) {
                    jsonObject.put(jsonKey, value);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return jsonObject;
    }

    /**
     * model集合转换为JSONArray
     * @param models model集合
     * @return 转换的JSONArray
     */
    public static JSONArray ModelsToJson(List<VDJsonModelProtocol> models, boolean containEmptyValue) {
        JSONArray jsonArray = new JSONArray();

        if (models == null) {
            return jsonArray;
        }

        int length = models.size();
        for (int i = 0; i < length; i++) {
            jsonArray.put(ModelToJson(models.get(i)));
        }

        return jsonArray;
    }

    /* Private Methods */
    /**
     * 设置json值到对应的变量
     * @param field 变量反射的field
     * @param model 持有变量的model
     * @param json json
     * @param jsonKey 当前变量对应的json键
     */
    static void PutJsonValueToField(Field field, VDJsonModelProtocol model, JSONObject json, String jsonKey) {
        field.setAccessible(true);
        try {
            if (!json.has(jsonKey)) {
                return;
            }

            Class<?> fieldType = field.getType();

            if (fieldType.isPrimitive()) {
                Object value = json.get(jsonKey);
                if (fieldType.equals(float.class)) {
                    field.set(model, Float.valueOf(value.toString()));
                }
                else if (fieldType.equals(double.class)) {
                    field.set(model, Double.valueOf(value.toString()));
                }
                else if (fieldType.equals(boolean.class)) {
                    field.set(model, Boolean.valueOf(value.toString()));
                }
                else {
                    field.set(model, value);
                }
            }
            else if (fieldType.equals(String.class)) {
                field.set(model, json.getString(jsonKey));
            }
            else if (fieldType.equals(Number.class)) {
                field.set(model, json.get(jsonKey));
            }
            else if (fieldType.equals(Date.class)) {
                Date date = model.getJsonDateFormat().parse(json.getString(jsonKey));
                field.set(model, date);
            }
            else if (fieldType.isEnum()) {
                int ordinal = (int) json.get(jsonKey);
                Enum[] enums = (Enum[]) fieldType.getDeclaredMethod("values").invoke(null);
                if (enums.length > ordinal) {
                    field.set(model, enums[ordinal]);
                }
            }
            else if (VDJsonModelProtocol.class.isAssignableFrom(fieldType)) {
                Class<? extends VDJsonModelProtocol> typeClazz = (Class<? extends VDJsonModelProtocol>) fieldType;
                if (json.getJSONObject(jsonKey).has(ClassNameJsonKey)) {
                    typeClazz = (Class<? extends VDJsonModelProtocol>) Class.forName(json.getJSONObject(jsonKey).getString(ClassNameJsonKey));
                }
                field.set(model, new VDJson<>(typeClazz).modelFromJsonString(json.getString(jsonKey)));
            }
            else if (List.class.isAssignableFrom(fieldType)) {
                Type type = field.getGenericType();
                if (type instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType)type;
                    Class<?> typeClazz = (Class<?>) parameterizedType.getActualTypeArguments()[0];

                    if (VDJsonModelProtocol.class.isAssignableFrom(typeClazz)) {
                        field.set(model, new VDJson<>((Class<? extends VDJsonModelProtocol>) typeClazz).modelsFromJsonString(json.getString(jsonKey)));
                    }
                    else {
                        JSONArray jsonArray = json.getJSONArray(jsonKey);
                        int length = jsonArray.length();
                        List<Object> array = new ArrayList<>();
                        for(int i = 0; i < length; i++) {
                            array.add(jsonArray.get(i));
                        }
                        field.set(model, array);
                    }
                }
            }
            else if (fieldType.isArray()) {
                Class<?> typeClazz = fieldType.getComponentType();

                if (VDJsonModelProtocol.class.isAssignableFrom(typeClazz)) {
                    List<? extends VDJsonModelProtocol> members = new VDJson<>((Class<? extends VDJsonModelProtocol>) typeClazz).modelsFromJsonString(json.getString(jsonKey));
                    int size = members.size();
                    Object array = Array.newInstance(typeClazz, size);
                    for(int i = 0; i < size; i++) {
                        Array.set(array, i, members.get(i));
                    }
                    field.set(model, array);
                }
                else {
                    JSONArray jsonArray = json.getJSONArray(jsonKey);
                    int length = jsonArray.length();
                    Object array = Array.newInstance(typeClazz, length);
                    for(int i = 0; i < length; i++) {
                        Array.set(array, i, jsonArray.get(i));
                    }
                    field.set(model, array);
                }
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取变量的值
     * @param field 指定变量反射的field
     * @param model 持有变量的model
     * @return 变量对应的值
     */
    @Nullable
    static Object GetJsonValueFromField(Field field, VDJsonModelProtocol model) {
        field.setAccessible(true);
        try {
            if (field.get(model) == null) {
                return null;
            }

            Class<?> fieldType = field.getType();

            if (fieldType.isPrimitive()) {
                return field.get(model);
            }
            else if (fieldType.equals(String.class)) {
                return field.get(model);
            }
            else if (fieldType.equals(Number.class)) {
                return field.get(model);
            }
            else if (fieldType.equals(Date.class)) {
                Date date = (Date) field.get(model);
                return model.getJsonDateFormat().format(date);
            }
            else if (fieldType.isEnum()) {
                Enum em = (Enum) field.get(model);
                return em.ordinal();
            }
            else if (VDJsonModelProtocol.class.isAssignableFrom(fieldType)) {
                VDJsonModelProtocol member = (VDJsonModelProtocol)field.get(model);
                JSONObject jsonObject = member.toJson();
                if (!fieldType.equals(member.getClass())) {
                    jsonObject.put(ClassNameJsonKey, member.getClass().getName());
                }
                return jsonObject;
            }
            else if (List.class.isAssignableFrom(fieldType)) {
                Type type = field.getGenericType();
                if (type instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType)type;
                    Class<?> typeClazz = (Class<?>) parameterizedType.getActualTypeArguments()[0];

                    if (VDJsonModelProtocol.class.isAssignableFrom(typeClazz)) {
                        List<VDJsonModelProtocol> members = (List<VDJsonModelProtocol>) field.get(model);
                        int size = members.size();
                        JSONArray jsonArray = new JSONArray();
                        for(int i = 0; i < size; i++) {
                            JSONObject jsonObject = members.get(i).toJson();
                            if (!typeClazz.equals(members.get(i).getClass())) {
                                jsonObject.put(ClassNameJsonKey, members.get(i).getClass().getName());
                            }
                            jsonArray.put(jsonObject);
                        }
                        return jsonArray;
                    }
                    else {
                        List<Object> members = (List<Object>) field.get(model);
                        int size = members.size();
                        JSONArray jsonArray = new JSONArray();
                        for(int i = 0; i < size; i++) {
                            jsonArray.put(members.get(i));
                        }
                        return jsonArray;
                    }
                }
            }
            else if (fieldType.isArray()) {
                Class<?> typeClazz = fieldType.getComponentType();

                if (VDJsonModelProtocol.class.isAssignableFrom(typeClazz)) {
                    Object members = field.get(model);
                    int length = Array.getLength(members);
                    JSONArray jsonArray = new JSONArray();
                    for(int i = 0; i < length; i++) {
                        VDJsonModelProtocol member = (VDJsonModelProtocol) Array.get(members, i);
                        JSONObject jsonObject = member.toJson();
                        if (!typeClazz.equals(member.getClass())) {
                            jsonObject.put(ClassNameJsonKey, member.getClass().getName());
                        }
                        jsonArray.put(jsonObject);
                    }
                    return jsonArray;
                }
                else {
                    Object members = field.get(model);
                    int length = Array.getLength(members);
                    JSONArray jsonArray = new JSONArray();
                    for(int i = 0; i < length; i++) {
                        jsonArray.put(Array.get(members, i));
                    }
                    return jsonArray;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}