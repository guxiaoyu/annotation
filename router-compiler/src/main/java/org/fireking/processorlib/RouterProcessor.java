package org.fireking.processorlib;

import com.google.auto.service.AutoService;

import org.fireking.routerlibrary.Router;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
@AutoService(Processor.class)
public class RouterProcessor extends AbstractProcessor {

    private Map<String,String> rootMap = new TreeMap<>();

//    private Map<String, List<>>

    private Messager mMessager;//打印错误信息

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        mMessager = processingEnvironment.getMessager();
        mMessager.printMessage(Diagnostic.Kind.WARNING,"==========init");

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mMessager.printMessage(Diagnostic.Kind.WARNING,"==========process");
        if (set!=null && set.size()>0){
            Set<? extends Element> rootElements = roundEnvironment.getElementsAnnotatedWith(Router.class);
            try{
                parseRoutes(rootElements);
            }catch (Exception e){
                mMessager.printMessage(Diagnostic.Kind.WARNING,"==========process:"+e.getMessage());
            }
            return true;
        }
        return false;
    }

    private void parseRoutes(Set<? extends Element> routeElements) throws IOException{
        if (routeElements!=null && routeElements.size()>0) {
            rootMap.clear();

        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return super.getSupportedAnnotationTypes();
    }
}
