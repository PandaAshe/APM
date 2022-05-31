package com.jiace.apm.until;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: yw
 * @date: 2021-06-02
 * @description: 实现Date和Gson的正确转换
 */
class UtilDateGSON implements JsonSerializer, JsonDeserializer{


    @Override
    public Date deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws JsonParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(arg0.getAsJsonPrimitive().getAsString());
        } catch (ParseException e) {
             Log.e("UtilDateGSON", "UtilDateGSON deserialize error." + e.toString());
        }
        return date;
    }

    @Override
    public JsonElement serialize(Object o, Type type, JsonSerializationContext jsonSerializationContext)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return new JsonPrimitive(sdf.format(o));
    }
}