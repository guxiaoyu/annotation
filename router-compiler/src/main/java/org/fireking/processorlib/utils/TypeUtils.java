package org.fireking.processorlib.utils;

import org.fireking.routerlibrary.enums.TypeKind;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static org.fireking.processorlib.utils.Constans.BOOLEAN;
import static org.fireking.processorlib.utils.Constans.BYTE;
import static org.fireking.processorlib.utils.Constans.CHAR;
import static org.fireking.processorlib.utils.Constans.DOUBEL;
import static org.fireking.processorlib.utils.Constans.FLOAT;
import static org.fireking.processorlib.utils.Constans.INTEGER;
import static org.fireking.processorlib.utils.Constans.LONG;
import static org.fireking.processorlib.utils.Constans.PARCELABLE;
import static org.fireking.processorlib.utils.Constans.SERIALIZABLE;
import static org.fireking.processorlib.utils.Constans.SHORT;
import static org.fireking.processorlib.utils.Constans.STRING;

public class TypeUtils {

    private Types types;
    private TypeMirror parcelableType;
    private TypeMirror serializableType;

    public TypeUtils(Types types, Elements elements) {
        this.types = types;
        parcelableType = elements.getTypeElement(PARCELABLE).asType();
        serializableType = elements.getTypeElement(SERIALIZABLE).asType();
    }

    public int typeExchange(Element element){
        TypeMirror typeMirror = element.asType();
        if (typeMirror.getKind().isPrimitive()){
            return element.asType().getKind().ordinal();
        }

        switch (typeMirror.toString()){
            case BYTE :
                return TypeKind.BYTE.ordinal();
            case SHORT:
                return TypeKind.SHORT.ordinal();
            case INTEGER:
                return TypeKind.INT.ordinal();
            case LONG:
                return TypeKind.LONG.ordinal();
            case FLOAT:
                return TypeKind.FLOAT.ordinal();
            case DOUBEL:
                return TypeKind.DOUBLE.ordinal();
            case BOOLEAN:
                return TypeKind.BOOLEAN.ordinal();
            case CHAR:
                return TypeKind.CHAR.ordinal();
            case STRING:
                return TypeKind.STRING.ordinal();
            default:
                if (types.isSubtype(typeMirror, parcelableType)) {
                    // PARCELABLE
                    return TypeKind.PARCELABLE.ordinal();
                } else if (types.isSubtype(typeMirror, serializableType)) {
                    // SERIALIZABLE
                    return TypeKind.SERIALIZABLE.ordinal();
                } else {
                    return TypeKind.OBJECT.ordinal();
                }
        }
    }
}
