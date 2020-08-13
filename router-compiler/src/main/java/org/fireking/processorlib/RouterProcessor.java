package org.fireking.processorlib;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.checkerframework.checker.units.qual.A;
import org.fireking.processorlib.entity.RouteDoc;
import org.fireking.processorlib.utils.Logger;
import org.fireking.processorlib.utils.StringUtils;
import org.fireking.processorlib.utils.TypeUtils;
import org.fireking.routerlibrary.annotation.Autowired;
import org.fireking.routerlibrary.annotation.Router;
import org.fireking.routerlibrary.enums.RouteType;
import org.fireking.routerlibrary.enums.TypeKind;
import org.fireking.routerlibrary.model.RouteMeta;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;


import static org.fireking.processorlib.utils.Constans.ACTIVITY;
import static org.fireking.processorlib.utils.Constans.ANNOTATION_TYPE_AUTOWIRED;
import static org.fireking.processorlib.utils.Constans.ANNOTATION_TYPE_ROUTE;
import static org.fireking.processorlib.utils.Constans.FRAGMENT;
import static org.fireking.processorlib.utils.Constans.FRAGMENT_V4;
import static org.fireking.processorlib.utils.Constans.IPROVIDER;
import static org.fireking.processorlib.utils.Constans.IPROVIDER_GROUP;
import static org.fireking.processorlib.utils.Constans.IROUTE_GROUP;
import static org.fireking.processorlib.utils.Constans.ITROUTE_ROOT;
import static org.fireking.processorlib.utils.Constans.KEY_GENERATE_DOC_NAME;
import static org.fireking.processorlib.utils.Constans.KEY_MODULE_NAME;
import static org.fireking.processorlib.utils.Constans.METHOD_LOAD_INTO;
import static org.fireking.processorlib.utils.Constans.NAME_OF_GROUP;
import static org.fireking.processorlib.utils.Constans.NAME_OF_PROVIDER;
import static org.fireking.processorlib.utils.Constans.NAME_OF_ROOT;
import static org.fireking.processorlib.utils.Constans.PACKAGE_OF_GENERATE_DOCS;
import static org.fireking.processorlib.utils.Constans.PACKAGE_OF_GENERATE_FILE;
import static org.fireking.processorlib.utils.Constans.SEPARATOR;
import static org.fireking.processorlib.utils.Constans.SERVICE;
import static org.fireking.processorlib.utils.Constans.VALUE_ENABLE;
import static org.fireking.processorlib.utils.Constans.WARNING_TIPS;

@AutoService(Processor.class)
@SupportedAnnotationTypes({ANNOTATION_TYPE_ROUTE, ANNOTATION_TYPE_AUTOWIRED})
public class RouterProcessor extends AbstractProcessor {
    Logger logger;

    private Types types;
    private Elements elementUtils;
    private Map<String,String> rootMap = new TreeMap<>();

    private Map<String, Set<RouteMeta>> groupMap = new HashMap<>();

    private Messager mMessager;//打印错误信息
//    private TypeMirror iProvider;
    private TypeUtils typeUtils ;
    private Filer mFiler;
    private String moduleName=null;
    private boolean generateDoc;
    private Writer docWriter;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        mMessager = processingEnvironment.getMessager();
        mMessager.printMessage(Diagnostic.Kind.WARNING,"==========init");
        logger = new Logger(mMessager);

        types = processingEnvironment.getTypeUtils();
        elementUtils = processingEnvironment.getElementUtils();

//        iProvider = elementUtils.getTypeElement(IPROVIDER).asType();
        typeUtils = new TypeUtils(types,elementUtils);
        mFiler = processingEnvironment.getFiler();

        Map<String, String> options = processingEnvironment.getOptions();
        if (MapUtils.isNotEmpty(options)){
            moduleName = options.get(KEY_MODULE_NAME);
            generateDoc = VALUE_ENABLE.equals(options.get(KEY_GENERATE_DOC_NAME));
        }

        if (generateDoc){
            try {
                docWriter = mFiler.createResource(
                        StandardLocation.SOURCE_OUTPUT,
                        PACKAGE_OF_GENERATE_DOCS,
                        "router-map-of-"+ moduleName +".json"
                ).openWriter();
            }catch (IOException e){
                e.printStackTrace();
                logger.error("Create doc writer failed, because " + e.getMessage());

            }
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mMessager.printMessage(Diagnostic.Kind.WARNING,"==========process");
        if (set!=null && set.size()>0){
            Set<? extends Element> rootElements = roundEnvironment.getElementsAnnotatedWith(Router.class);
            try{
                parseRoutes(rootElements);
            }catch (Exception e){
                e.printStackTrace();
                mMessager.printMessage(Diagnostic.Kind.WARNING,"==========process:"+e.getMessage());
            }
            return true;
        }
        return false;
    }

    private void parseRoutes(Set<? extends Element> routeElements) throws IOException{
        if (routeElements!=null && routeElements.size()>0) {
            logger.info(">>> Found routes, size is " + routeElements.size() + " <<<");

            rootMap.clear();

            TypeMirror type_Activity = elementUtils.getTypeElement(ACTIVITY).asType();
            TypeMirror type_Service = elementUtils.getTypeElement(SERVICE).asType();
            TypeMirror type_FRAGMENT = elementUtils.getTypeElement(FRAGMENT).asType();
            TypeMirror type_FRAGMENT_V4 = elementUtils.getTypeElement(FRAGMENT_V4).asType();


//            TypeElement type_IRouterGroup = elementUtils.getTypeElement(IROUTE_GROUP);
            TypeElement type_IProviderGroup = elementUtils.getTypeElement(IPROVIDER_GROUP);

            ClassName routeMetaCn = ClassName.get(RouteMeta.class);
            ClassName routeTypeCn = ClassName.get(RouteType.class);

//            ParameterizedTypeName inputMapTypeOfRoot = ParameterizedTypeName.get(
//                    ClassName.get(Map.class),
//                    ClassName.get(String.class),
//                    ParameterizedTypeName.get(
//                            ClassName.get(Class.class),
//                            WildcardTypeName.subtypeOf(ClassName.get(type_IRouterGroup))
//                    )
//            );


            ParameterizedTypeName inputMapTypeOfGroup = ParameterizedTypeName.get(
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouteMeta.class)
            );

            for (Element element:routeElements){
                TypeMirror tm = element.asType();
                Router router = element.getAnnotation(Router.class);
                RouteMeta routeMeta = null;

                if (types.isSubtype(tm,type_Activity) || types.isSubtype(tm,type_FRAGMENT)
                    ||types.isSubtype(tm,type_FRAGMENT_V4)){
                    Map<String,Integer> paramsType = new HashMap<>();
                    Map<String, Autowired> injectConfig = new HashMap<>();
                    for (Element field:element.getEnclosedElements()){
                        if (field.getKind().isField() && field.getAnnotation(Autowired.class)!=null /*&& !types.isSubtype(field.asType(),iProvider)*/){
                            Autowired paramConfig = field.getAnnotation(Autowired.class);
                            String injectName = StringUtils.isEmpty(paramConfig.name()) ? field.getSimpleName().toString() :paramConfig.name();
                            paramsType.put(injectName,typeUtils.typeExchange(field));
                            injectConfig.put(injectName,paramConfig);
                        }
                    }

                    if (types.isSubtype(tm,type_Activity)){
                        routeMeta = new RouteMeta(router,element, RouteType.ACTIVITY,paramsType);
                    }else {
                        routeMeta = new RouteMeta(router,element, RouteType.parse(FRAGMENT),paramsType);

                    }

                    routeMeta.setInjectConfig(injectConfig);
                }/*else if (types.isSubtype(tm,iProvider)){
                    routeMeta = new RouteMeta(router,element,RouteType.PROVIDER,null);
                }else if (types.isSubtype(tm,type_Service)){
                    routeMeta = new RouteMeta(router, element, RouteType.parse(SERVICE), null);
                }*/



                categories(routeMeta);
            }
//            ParameterSpec rootParamSpec = ParameterSpec.builder(inputMapTypeOfRoot,"routes").build();

            ParameterSpec groupParamSpec = ParameterSpec.builder(inputMapTypeOfGroup,"atlas").build();
            ParameterSpec providerParamSpec = ParameterSpec.builder(inputMapTypeOfGroup, "providers").build();  // Ps. its param type same as groupParamSpec!

//
//            MethodSpec.Builder loadIntoMethodOfRootBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
//                    .addAnnotation(Override.class)
//                    .addModifiers(Modifier.PUBLIC)
//                    .addParameter(rootParamSpec);
//
//            MethodSpec.Builder loadIntoMethodOfProviderBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
//                    .addAnnotation(Override.class)
//                    .addModifiers(Modifier.PUBLIC)
//                    .addParameter(providerParamSpec);

            Map<String, List<RouteDoc>> docSource = new HashMap<>();

            for (Map.Entry<String,Set<RouteMeta>> entry:groupMap.entrySet()){
                logger.info(">>> Found routes, groupMap size is " + groupMap.size() + " <<<");

                String groupName = entry.getKey();

                MethodSpec.Builder loadIntoMethodOfGroupBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
//                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(groupParamSpec);
                List<RouteDoc> routeDocList = new ArrayList<>();
                Set<RouteMeta> groupData = entry.getValue();

                for (RouteMeta routeMeta:groupData){
                    logger.error("================"+groupData.size()+"==="+routeMeta.toString());
                    RouteDoc routeDoc = extractDocInfo(routeMeta);
                    ClassName className = ClassName.get((TypeElement) routeMeta.getRawType());
                    switch (routeMeta.getType()){
                        case PROVIDER:
                            List<? extends TypeMirror> interfaces = ((TypeElement) routeMeta.getRawType()).getInterfaces();

//                            for (TypeMirror tm:interfaces){
//                                if (types.isSameType(tm,iProvider)){
//
//                                    loadIntoMethodOfProviderBuilder.addStatement(
//                                            "providers.put($S,$T.build($T."+routeMeta.getType() +",$T.class,$S,$S,null))",
//                                            (routeMeta.getRawType()).toString(),
//                                            routeMetaCn,
//                                            routeTypeCn,
//                                            className,
//                                            routeMeta.getPath(),
//                                            routeMeta.getGroup()
//                                    );
//                                }else if (types.isSubtype(tm,iProvider)){
//                                    loadIntoMethodOfProviderBuilder.addStatement(
//                                            "providers.put($S,$T.build($T."+routeMeta.getType() +",$T.class,$S,$S,null))",
//                                            tm.toString(),
//                                            routeMetaCn,
//                                            routeTypeCn,
//                                            className,
//                                            routeMeta.getPath(),
//                                            routeMeta.getGroup()
//                                    );
//                                }
//                            }
                                break;
                            default:
                                break;

                    }

                    StringBuilder mapBodyBuilder = new StringBuilder();
                    Map<String,Integer> paramsType = routeMeta.getParamsType();
                    Map<String,Autowired> injectConfigs = routeMeta.getInjectConfig();
                    if (MapUtils.isNotEmpty(paramsType)){
                        List<RouteDoc.Param> paramList = new ArrayList<>();
                        for (Map.Entry<String,Integer> types : paramsType.entrySet()){
                            mapBodyBuilder.append("put(\"")
                                    .append(types.getKey())
                                    .append("\", ")
                                    .append(types.getValue())
                                    .append("); ");
                            RouteDoc.Param param = new RouteDoc.Param();
                            Autowired injectConfig = injectConfigs.get(types.getKey());
                            param.setKey(types.getKey());
                            param.setType(TypeKind.values()[types.getValue()].name().toLowerCase());
                            param.setDescription(injectConfig.desc());
                            param.setRequired(injectConfig.required());

                            paramList.add(param);
                        }

                        routeDoc.setParams(paramList);

                    }

                    String mapBody = mapBodyBuilder.toString();
                    loadIntoMethodOfGroupBuilder.addStatement(
                            "atlas.put($S,$T.build($T." + routeMeta.getType()+ ",$T.class,$S,$S, " +(StringUtils.isEmpty(mapBody) ?null:("new java.util.HashMap<String,Integer>(){{" + mapBodyBuilder.toString()+"}}))")),
                            routeMeta.getPath(),
                            routeMetaCn,
                            routeTypeCn,
                            className,
                            routeMeta.getPath().toLowerCase(),
                            routeMeta.getGroup().toLowerCase()
                    );

                    routeDoc.setClassName(className.toString());
                    routeDocList.add(routeDoc);
//
                }

                String groupFileName = NAME_OF_GROUP + groupName;
                JavaFile.builder(
                        PACKAGE_OF_GENERATE_FILE,
                        TypeSpec.classBuilder(groupFileName)
                                .addJavadoc(WARNING_TIPS)
//                                .addSuperinterface(ClassName.get(type_IRouterGroup))
                                .addModifiers(Modifier.PUBLIC)
                                .addMethod(loadIntoMethodOfGroupBuilder.build())
                                .build()

                ).build().writeTo(mFiler);

                rootMap.put(groupName,groupFileName);
                docSource.put(groupName,routeDocList);
            }

//            if (MapUtils.isNotEmpty(rootMap)){
//                for (Map.Entry<String,String> entry:rootMap.entrySet()){
//                    loadIntoMethodOfRootBuilder.addStatement("routes.put($S,$T.class)",entry.getKey(),ClassName.get(PACKAGE_OF_GENERATE_FILE,entry.getValue()));
//                }
//            }

            if (generateDoc){
                docWriter.append(JSON.toJSONString(docSource, SerializerFeature.PrettyFormat));
                docWriter.flush();
                docWriter.close();
            }

//            String providerMapFileNAme = NAME_OF_PROVIDER + SEPARATOR + moduleName;
//            JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
//                    TypeSpec.classBuilder(providerMapFileNAme)
//                            .addJavadoc(WARNING_TIPS)
//                            .addSuperinterface(ClassName.get(type_IProviderGroup))
//                            .addModifiers(Modifier.PUBLIC)
//                            .addMethod(loadIntoMethodOfProviderBuilder.build())
//                            .build()
//            ).build().writeTo(mFiler);
//
//            String rootFileName = NAME_OF_ROOT + SEPARATOR +moduleName;
//            JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
//                    TypeSpec.classBuilder(rootFileName)
//                            .addJavadoc(WARNING_TIPS)
////                            .addSuperinterface(ClassName.get(elementUtils.getTypeElement(ITROUTE_ROOT)))
//                            .build()
//            ).build().writeTo(mFiler);

        }

    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedOptions() {
        return new HashSet<String>(){{
            this.add(KEY_MODULE_NAME);
            this.add(KEY_GENERATE_DOC_NAME);
        }};
    }

    private RouteDoc extractDocInfo(RouteMeta routeMeta){
        RouteDoc routeDoc = new RouteDoc();
        routeDoc.setGroup(routeMeta.getGroup());
        routeDoc.setPath(routeMeta.getPath());
        routeDoc.setDescription(routeMeta.getName());
        routeDoc.setType(routeMeta.getType().name().toLowerCase());

        return routeDoc;
    }


    private void categories(RouteMeta routeMeta){
        if (routeVerify(routeMeta)){

            Set<RouteMeta> routeMetas = groupMap.get(routeMeta.getGroup());
            if (CollectionUtils.isNotEmpty(routeMetas))
                logger.error("=========="+routeMetas.toString());
            if (CollectionUtils.isEmpty(routeMetas)){
                Set<RouteMeta> routeMetaSet = new TreeSet<>(new Comparator<RouteMeta>() {
                    @Override
                    public int compare(RouteMeta routeMeta, RouteMeta t1) {
                        try {
                            return routeMeta.getPath().compareTo(t1.getPath());
                        }catch (Exception e) {
                            e.printStackTrace();
                            logger.error(e.getMessage());

                            return 0;
                        }
                    }
                });

                routeMetaSet.add(routeMeta);
                groupMap.put(routeMeta.getGroup(),routeMetaSet);
            }else {
                routeMetas.add(routeMeta);
            }
        }else {
            logger.warning(">>> Route meta verify error, group is " + routeMeta.getGroup() + " <<<");
        }
    }


    private boolean routeVerify(RouteMeta meta){
        String path = meta.getPath();

        if (StringUtils.isEmpty(path) || !path.startsWith("/")){
            return false;
        }

        if (StringUtils.isEmpty(meta.getGroup())){
            try{
                String defaultGroup = path.substring(1,path.indexOf("/",1));
                if (StringUtils.isEmpty(defaultGroup)){
                    return false;
                }
                meta.setGroup(defaultGroup);
                return true;
            }catch (Exception e){
                e.printStackTrace();
                logger.error("Failed to extract default group! " + e.getMessage());

                return false;
            }

        }

        return true;
    }

}
