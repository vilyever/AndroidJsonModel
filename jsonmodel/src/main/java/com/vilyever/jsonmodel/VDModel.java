package com.vilyever.jsonmodel;

import com.vilyever.reflectkit.VDReflectKit;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * VDModel
 * AndroidJsonModelConverter <com.vilyever.jsonmodelconverter>
 * Created by vilyever on 2015/8/18.
 * Feature:
 */
public class VDModel implements VDJsonModelDelegate {
    private final VDModel self = this;


    /* #Constructors */
    public VDModel() {
        // Required empty public constructor
    }

    /* #Overrides */
    @Override
    public String toString() {
        String description = super.toString();

        ArrayList<Field> fields = VDReflectKit.getFields(this.getClass(), VDModel.class, VDJson.ReflectExclusionDelegate);

        for(Field field : fields) {
            field.setAccessible(true);
            try {
                String jsonKey = self.jsonKeyBindingDictionary().get(field.getName());
                if (jsonKey == null
                        && null != field.getAnnotation(VDJsonKey.class)) {
                    jsonKey = field.getAnnotation(VDJsonKey.class).value();
                }

                if (jsonKey == null) {
                    jsonKey = "";
                }

                String value = "";
                Object jsonValue = VDJson.GetJsonValueFromField(field, this);
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

    /* #Accessors */

    /* #Delegates */
    // VDJsonModelDelegate
    @Override
    public HashMap<String, String> jsonKeyBindingDictionary() {
        return new HashMap<>();
    }

    @Override
    public SimpleDateFormat jsonDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public JSONObject toJson() {
        return VDJson.ModelToJson(self, false);
    }

    public JSONObject toJsonWithEmptyValue() {
        return VDJson.ModelToJson(self, true);
    }

    public String dateToString(Date date) {
        return self.jsonDateFormat().format(date);
    }

    public Date stringToDate(String dateString) {
        try {
            return self.jsonDateFormat().parse(dateString);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /* #Private Methods */

    /* #Public Methods */

    /* #Classes */

    /* #Interfaces */

    /* #Annotations @interface */

    /* #Enums */
}