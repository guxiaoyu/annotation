package org.fireking.processorlib;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;
import org.fireking.processorlib.entity.AnnotatedClass;
import org.fireking.processorlib.utils.Logger;
import org.fireking.processorlib.utils.StringUtils;
import org.fireking.routerlibrary.annotation.BindView;
import org.fireking.routerlibrary.model.BindViewMeta;

import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static org.fireking.processorlib.utils.Constans.WARNING_TIPS;
//https://www.cnblogs.com/huansky/p/9544640.html
@AutoService(Processor.class)
public class BindViewProcessor extends AbstractProcessor {

    Elements elementUtils;
    Filer mFiler;
    Types typeUtils;
    Logger logger;
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        mFiler = processingEnvironment.getFiler();
        typeUtils = processingEnvironment.getTypeUtils();
        logger = new Logger(processingEnvironment.getMessager());
        logger.info("====================BindViewProcessor----init");

    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>(){{this.add(BindView.class.getCanonicalName());}};
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        logger.info("======================BindViewProcessor----process");
        if (CollectionUtils.isNotEmpty(set)){
            try {
                parseElement(roundEnvironment);
            }catch (Exception e){
                logger.info("=========================error:"+e.getMessage());
                return false;
            }


            try {
                for (AnnotatedClass annotatedClass: map.values()){
                    annotatedClass.generateFile().writeTo(mFiler);
                }
            }catch (Exception e){
                e.printStackTrace();
                logger.info("================generateFile=========error:"+e.getMessage());

            }
        }
        return false;
    }

    private void parseElement(RoundEnvironment roundEnv) {
            for (Element element:roundEnv.getElementsAnnotatedWith(BindView.class)){

//                if (element.getKind() == ElementKind.FIELD){
                    AnnotatedClass annotatedClass = getAnnotatedClass(element);
                    BindViewMeta bindViewMeta = new BindViewMeta(element);
                    annotatedClass.addField(bindViewMeta);
//                }

            }
    }


    private Map<String,AnnotatedClass> map = new HashMap<>();

    private AnnotatedClass getAnnotatedClass(Element element){
        TypeElement enclosedElements = (TypeElement) element.getEnclosingElement();
        String fullClassName = enclosedElements.getQualifiedName().toString();
        AnnotatedClass annotatedClass = map.get(fullClassName);
        if (annotatedClass == null){
            annotatedClass = new AnnotatedClass(enclosedElements,elementUtils);
            map.put(fullClassName,annotatedClass);
        }

        return annotatedClass;
    }


}
