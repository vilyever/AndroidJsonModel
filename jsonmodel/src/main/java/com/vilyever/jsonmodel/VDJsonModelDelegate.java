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
 * VDJsonDelegate
 * AndroidJsonModelConverter <com.vilyever.jsonmodelconverter>
 * Created by vilyever on 2015/8/18.
 * Feature:
 */
public interface VDJsonModelDelegate {

    HashMap<String, String> jsonKeyBindingDictionary();

    SimpleDateFormat jsonDateFormat();

    JSONObject toJson();

    String dateToString(Date date);

    Date stringToDate(String dateString);

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface VDJsonKey {

        /**
         * @return json key
         */
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface VDJsonKeyIgnore {
    }

}