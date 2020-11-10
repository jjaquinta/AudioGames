package jo.audio.companions.tools.gui.json;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.tree.DefaultMutableTreeNode;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;
import org.json.simple.parser.ParseException;

import jo.util.utils.obj.StringUtils;

/**
 * <p>Provides the model for translating JsonElement into JTree data nodes.  This class is not thread safe.</p>
 * 
 * @author Stephen Owens
 * 
 * <p>Provides the model for translating JsonElement into JTree data nodes.</p>
 *
 * <p>
 * Copyright 2011 Stephen P. Owens : steve@doitnext.com
 * </p>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * </p>
 * <p>
 *   http://www.apache.org/licenses/LICENSE-2.0
 * </p>
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */
public class JSONJTreeNode extends DefaultMutableTreeNode {
    /**
     * Using default serial id.
     */
    private static final long serialVersionUID = 1L;

    public enum DataType {ARRAY, OBJECT, VALUE};
    final DataType dataType;
    final int index;
    String fieldName;
    final String value;
    
    /**
     * @param fieldName - name of field if applicable or null
     * @param index - index of element in the array or -1 if not part of an array
     * @param jsonElement - element to represent
     */
    public JSONJTreeNode(String fieldName, int index, Object jsonElement) {
        this.index = index;
        this.fieldName = fieldName;
        if(jsonElement instanceof JSONArray) {
            this.dataType = DataType.ARRAY;
            this.value = jsonElement.toString();
            populateChildren(jsonElement);
        } else if(jsonElement instanceof JSONObject) {
            this.dataType = DataType.OBJECT;
            this.value = jsonElement.toString();
            populateChildren(jsonElement);
        } else if((jsonElement instanceof String) || (jsonElement instanceof Number) || (jsonElement instanceof Boolean)) {
            this.dataType = DataType.VALUE;
            this.value = jsonElement.toString();
        } else if(jsonElement == null) {
            this.dataType = DataType.VALUE;
            this.value = null;
        } else {
            throw new IllegalArgumentException("jsonElement is an unknown element type.");
        }
        
    }
    
    private void populateChildren(Object myJsonElement) {
        switch(dataType) {
        case ARRAY:
            int index = 0;
            @SuppressWarnings("unchecked")
            Iterator<Object> it = ((JSONArray)myJsonElement).iterator();
            while(it.hasNext()) {
                Object element = it.next();
                JSONJTreeNode childNode = new JSONJTreeNode(null, index, element);
                this.add(childNode);
                index++;
            }
            break;
        case OBJECT:
            for(Entry<String,Object> entry : ((JSONObject)myJsonElement).entrySet()) {
                JSONJTreeNode childNode = new JSONJTreeNode(entry.getKey(), -1, entry.getValue());
                this.add(childNode);
            }
            break;
        default:
            throw new IllegalStateException("Internal coding error this should never happen.");
        }
    }
    
    public Object asJsonElement() throws ParseException {
        StringBuilder sb = new StringBuilder();
        buildJsonString(sb);
        String json = sb.toString().trim();
        if(json.startsWith("{") || json.startsWith("["))        
            return JSONUtils.PARSER.parse(sb.toString());
        else {
            // Safety check the JSON, if it is of a named value object
            // We cheat a little if it is an orphan name value pair then
            // if we wrap it in {} chars it will parse if it isn't the parse
            // fails.           
            String testValue = "{" + json + "}";
            try {
                Object wrapperElt = JSONUtils.PARSER.parse(testValue);
                JSONObject obj = (JSONObject) wrapperElt;
                Iterator<Entry<String,Object>> it = obj.entrySet().iterator();
                Entry<String,Object> entry = it.next();
                return entry.getValue();
            } catch(ParseException jse) {
                Object rawElement = JSONUtils.PARSER.parse(json);
                return rawElement;
            }           
        }
    }
    
    private void buildJsonString(StringBuilder sb) throws ParseException {
        if(!StringUtils.isTrivial(this.fieldName)) {
            sb.append("\"" + this.fieldName + "\":");
        }
        Enumeration<?> children;
        switch(dataType) {
        case ARRAY:
            sb.append("[");
            children = this.children();
            while(children.hasMoreElements()) {
                JSONJTreeNode child = (JSONJTreeNode) children.nextElement();
                child.buildJsonString(sb);
                if(children.hasMoreElements())
                    sb.append(",");
            }
            sb.append("]");
            break;
        case OBJECT:
            sb.append("{");
            children = this.children();
            while(children.hasMoreElements()) {
                JSONJTreeNode child = (JSONJTreeNode) children.nextElement();
                child.buildJsonString(sb);
                if(children.hasMoreElements())
                    sb.append(",");
            }
            sb.append("}");
            break;          
        default: {
                // Use the JSON parser to parse the value for safety
                Object elt = JSONUtils.PARSER.parse(this.value);
                sb.append(elt.toString());
            }
        }
    }
    
    @Override
    public String toString() {
        switch(dataType) {
        case ARRAY:
        case OBJECT:
            if(index >= 0) {
                return String.format("[%d] (%s)", index, dataType.name());
            } else if(fieldName != null) {
                return String.format("%s (%s)", fieldName, dataType.name());
            } else {
                return String.format("(%s)", dataType.name());
            }
        default:
            if(index >= 0) {
                return String.format("[%d] %s", index, value);
            } else if(fieldName != null) {
                return String.format("%s: %s", fieldName, value);
            } else {
                return String.format("%s", value);
            }
            
        }
    }
}
