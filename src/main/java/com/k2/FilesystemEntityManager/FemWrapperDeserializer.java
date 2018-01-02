package com.k2.FilesystemEntityManager;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

@SuppressWarnings("rawtypes")
public class FemWrapperDeserializer implements JsonDeserializer<FemWrapper> {
	
	LocalType localType;
	
	public FemWrapperDeserializer(LocalType localType) {
		this.localType = localType;
	}

    @SuppressWarnings("unchecked")
	@Override
    public FemWrapper deserialize(JsonElement je, Type respT,
                          JsonDeserializationContext jdc) throws JsonParseException {

    	
        Type  t = localType.get();
        if (t == null) {
        		t = (respT instanceof ParameterizedType) ?
                ((ParameterizedType) respT).getActualTypeArguments()[0] :
                Object.class;
        }

        JsonObject jObject = (JsonObject) je;

        FemWrapper wrapper = new FemWrapper();

        //can add validation and null value check here
        wrapper.ocn = jObject.get("ocn").getAsInt();

        JsonElement objElement = jObject.get("obj");

        if (objElement != null) {
            if(objElement.isJsonObject()) {
                wrapper.obj = (jdc.deserialize(objElement, t));
            }
        }
        return wrapper;
    }

}
