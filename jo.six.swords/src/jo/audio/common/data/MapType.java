package jo.audio.common.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

@SuppressWarnings("rawtypes")
public @interface MapType {
    public Class keyType();
    public Class valueType();
}
