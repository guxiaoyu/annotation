package org.fireking.processorlib.entity;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.fireking.routerlibrary.model.BindViewMeta;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import static org.fireking.processorlib.utils.Constans.WARNING_TIPS;

public class AnnotatedClass {
    public TypeElement mClassElement;

    public List<BindViewMeta> mField;

    public Elements mElementUtils;

    public AnnotatedClass(TypeElement mClassElement, Elements mElementUtils) {
        this.mClassElement = mClassElement;
        this.mElementUtils = mElementUtils;
        this.mField = new ArrayList<>();
    }

    public void addField(BindViewMeta meta){
        mField.add(meta);
    }


    //获取类名
    private static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }
    public String getPakageName(TypeElement element){
        return mElementUtils.getPackageOf(element).getQualifiedName().toString();
    }


    public JavaFile generateFile(){
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("inject")
//                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(mClassElement.asType()),"host",Modifier.FINAL)
                .addParameter(TypeName.OBJECT,"source")
                ;



        for (BindViewMeta meta:mField){
            methodBuilder.addStatement("host.$N = ($T)findView(source,$L)",
                    meta.getFieldName(),
                    ClassName.get(meta.getFieldType()),
                    meta.getmResId());
        }

        String packageName = getPakageName(mClassElement);

        String className = getClassName(mClassElement,packageName);
        ClassName bindClassName = ClassName.get(packageName,className );

        TypeSpec build = TypeSpec.classBuilder(bindClassName.simpleName() + "$$Injector")
                .addModifiers(Modifier.PUBLIC)
//                .addSuperinterface()
                .addJavadoc(WARNING_TIPS)
                .addMethod(methodBuilder.build())
                .build();

        return    JavaFile.builder(packageName,build)
                .build();

    }
}
