package org.fireking.routerlibrary.model;

import org.fireking.routerlibrary.annotation.BindView;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class BindViewMeta {

    private VariableElement mFiledElement;
    private int mResId;


    public BindViewMeta(Element element){
        mFiledElement = (VariableElement) element;

        BindView annotation = mFiledElement.getAnnotation(BindView.class);
        mResId = annotation.value();
    }

    public VariableElement getmFiledElement() {
        return mFiledElement;
    }

    public int getmResId() {
        return mResId;
    }

    public TypeMirror getFieldType(){
        return mFiledElement.asType();
    }


    public Name getFieldName() {
        return mFiledElement.getSimpleName();
    }

}
