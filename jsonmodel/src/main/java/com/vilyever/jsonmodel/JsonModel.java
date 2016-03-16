package com.vilyever.jsonmodel;

import com.vilyever.reflectkit.ReflectKit;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * JsonModel
 * AndroidJsonModelConverter <com.vilyever.jsonmodelconverter>
 * Created by vilyever on 2015/8/18.
 * Feature:
 */
public class JsonModel implements JsonModelProtocol {
    private final JsonModel self = this;

    private final static SimpleDateFormat DefaultSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);


    /* Constructors */
    public JsonModel() {
        // Required empty public constructor
    }

    /* Overrides */
    @Override
    public String toString() {
        String description = super.toString();

        ArrayList<Field> fields = ReflectKit.getFields(this.getClass(), JsonModel.class, Json.ReflectExclusionDelegate);

        for(Field field : fields) {
            field.setAccessible(true);
            try {
                String jsonKey = getJsonKeyBindingDictionary().get(field.getName());
                if (jsonKey == null
                        && null != field.getAnnotation(JsonKey.class)) {
                    jsonKey = field.getAnnotation(JsonKey.class).value();
                }

                if (jsonKey == null) {
                    jsonKey = "";
                }

                String value = "";
                Object jsonValue = Json.GetJsonValueFromField(field, this);
                if (jsonValue != null) {
                    value = jsonValue.toString();
                }

                description += "\n" + field.getName() +
                        " (" + jsonKey + ")" +
                        " : " + value;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return description;
    }


    /* Delegates */
    /** {@link JsonModelProtocol} */
    @Override
    public HashMap<String, String> getJsonKeyBindingDictionary() {
        return new HashMap<>();
    }

    @Override
    public SimpleDateFormat getJsonDateFormat() {
        return DefaultSimpleDateFormat;
    }

    public JSONObject toJson() {
        return Json.ModelToJson(this);
    }

    public String dateToString(Date date) {
        return getJsonDateFormat().format(date);
    }

    public Date stringToDate(String dateString) {
        try {
            return getJsonDateFormat().parse(dateString);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    /** {@link JsonModelProtocol} */
}