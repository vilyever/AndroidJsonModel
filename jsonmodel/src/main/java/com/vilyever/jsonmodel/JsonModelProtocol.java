package com.vilyever.jsonmodel;

import org.json.JSONObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * JsonModelProtocol
 * AndroidJsonModelConverter <com.vilyever.jsonmodelconverter>
 * Created by vilyever on 2015/8/18.
 * Feature:
 */
public interface JsonModelProtocol {

    /**
     * 属性变量名对应json键值，("propertyName" : "jsonKey")
     * @return 绑定map
     */
    HashMap<String, String> getJsonKeyBindingDictionary();

    /**
     * Date格式化
     * @return SimpleDateFormat
     */
    SimpleDateFormat getJsonDateFormat();

    /**
     * 转换为json
     * @return json
     */
    JSONObject toJson();

    /**
     * date转换string
     * @param date date
     * @return 转换的string
     */
    String dateToString(Date date);

    /**
     * string转换date
     * @param dateString string
     * @return 转换的date
     */
    Date stringToDate(String dateString);

    /**
     * 标记变量对应的json key
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface JsonKey {

        /**
         * @return json key
         */
        String value();
    }

    /**
     * 标记此变量不参与json转换
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface JsonKeyIgnore {
    }

}