package com.k2.FilesystemEntityManager;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.k2.Util.Version.Version;

@SuppressWarnings("rawtypes")
public class FemWrapperAndVersionDeserializer implements JsonDeserializer<FemWrapperAndVersion> {

	LocalType localType;
	
	public FemWrapperAndVersionDeserializer(LocalType localType) {
		this.localType = localType;
	}


    @SuppressWarnings("unchecked")
	@Override
    public FemWrapperAndVersion deserialize(JsonElement je, Type respT,
                          JsonDeserializationContext jdc) throws JsonParseException {

    	
        Type  t = localType.get();
        if (t == null) {
        		t = (respT instanceof ParameterizedType) ?
                ((ParameterizedType) respT).getActualTypeArguments()[0] :
                Object.class;
        }

        JsonObject jObject = (JsonObject) je;

        FemWrapperAndVersion wrapper = new FemWrapperAndVersion();

        //can add validation and null value check here
        wrapper.ocn = jObject.get("ocn").getAsInt();

        JsonElement versionElement = jObject.get("version");

        if (versionElement != null) {
            if(versionElement.isJsonObject()) {
                wrapper.obj = (jdc.deserialize(versionElement, Version.class));
            }
        }
        JsonElement objElement = jObject.get("obj");

        if (objElement != null) {
            if(objElement.isJsonObject()) {
                wrapper.obj = (jdc.deserialize(objElement, t));
            }
        }
        return wrapper;
    }

}
