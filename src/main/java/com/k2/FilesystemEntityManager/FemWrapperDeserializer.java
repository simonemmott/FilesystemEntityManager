package com.k2.FilesystemEntityManager;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
/**
 * This class allows Gson to deserialize the instances of the generic class FemWrapper
 * 
 * This is achieved through the use of a ThreadLocal variable that holds the type of the object wrapped by the 
 * wrapper.  This local ThreadLocal variable is set before deserializing FemWrappers using Gson and unset afterwards.
 * 
 * @author simon
 *
 */
@SuppressWarnings("rawtypes")
public class FemWrapperDeserializer implements JsonDeserializer<FemWrapper> {
	
	LocalType localType;
	/**
	 * Create the wrapper deserializer setting the ThreadLocal variable to identify the type
	 * of the wrapped object.
	 * 
	 * @param localType	The ThreadLocal&lt;Type&gt; that provides the type of the wrapped object. The value of this ThreadLocal
	 * 					must be set prior to deserializing the wrqpper through Gson 
	 */
	public FemWrapperDeserializer(LocalType localType) {
		this.localType = localType;
	}

	/**
	 * Deserialize the object wrapper deserializing the wrapped object as the type identified by the ThreadLocal passed into 
	 * this instance when it was created.
	 */
    @SuppressWarnings("unchecked")
	@Override
    public FemWrapper deserialize(JsonElement je, Type respT,
                          JsonDeserializationContext jdc) throws JsonParseException {

    		// Get the type of the wrapped object from the ThreadLocal<Type> variable
        Type  t = localType.get();
        if (t == null) {
        		// If the ThreadLocal<Type> has not been set attempt to identify the type from type arguments of the class
        	    // passed to Gson and default to use Object.class
        		t = (respT instanceof ParameterizedType) ?
                ((ParameterizedType) respT).getActualTypeArguments()[0] :
                Object.class;
        }

        JsonObject jObject = (JsonObject) je;

        FemWrapper wrapper = new FemWrapper();

        // Extract the OCN from the wrapper
        wrapper.ocn = jObject.get("ocn").getAsInt();

        JsonElement objElement = jObject.get("obj");

        // If the wrapped object is populated deserialize it as the Type identified above
        if (objElement != null) {
            if(objElement.isJsonObject()) {
                wrapper.obj = (jdc.deserialize(objElement, t));
            }
        }
        return wrapper;
    }

}
