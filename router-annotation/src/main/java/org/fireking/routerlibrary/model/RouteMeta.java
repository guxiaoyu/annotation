package org.fireking.routerlibrary.model;

import org.fireking.routerlibrary.annotation.Autowired;
import org.fireking.routerlibrary.annotation.Router;
import org.fireking.routerlibrary.enums.RouteType;

import java.util.Map;

import javax.lang.model.element.Element;

public class RouteMeta {
    private RouteType type;
    private Element rawType;
    private Class<?> destination;
    private String path;
    private String group;
    private Map<String,Integer> paramsType;
    private String name;

    private Map<String, Autowired> injectConfig;


    public static RouteMeta build(RouteType type,Class<?> destination,String path,String group){
        return new RouteMeta(type,null,destination,path,group,null,null);
    }
    public static RouteMeta build(RouteType type,Class<?> destination,String path,String group,Map<String,Integer> paramsType){
        return new RouteMeta(type,null,destination,path,group,paramsType,null);
    }

    public RouteMeta(Router router,Class<?> destination,RouteType routeType){
        this(routeType,null,destination,router.path(),router.group(),null,router.name());
    }

    public RouteMeta(Router router,Element rawType,RouteType routeType,Map<String,Integer> paramsType){
        this(routeType,rawType,null,router.path(),router.group(),paramsType,router.name());
    }

    public RouteMeta(RouteType type, Element rawType, Class<?> destination, String path, String group, Map<String, Integer> paramsType, String name) {
        this.type = type;
        this.rawType = rawType;
        this.destination = destination;
        this.path = path;
        this.group = group;
        this.paramsType = paramsType;
        this.name = name;
    }

    public RouteType getType() {
        return type;
    }

    public void setType(RouteType type) {
        this.type = type;
    }

    public Element getRawType() {
        return rawType;
    }

    public void setRawType(Element rawType) {
        this.rawType = rawType;
    }

    public Class<?> getDestination() {
        return destination;
    }

    public void setDestination(Class<?> destination) {
        this.destination = destination;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Map<String, Integer> getParamsType() {
        return paramsType;
    }

    public void setParamsType(Map<String, Integer> paramsType) {
        this.paramsType = paramsType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Autowired> getInjectConfig() {
        return injectConfig;
    }

    public void setInjectConfig(Map<String, Autowired> injectConfig) {
        this.injectConfig = injectConfig;
    }

    @Override
    public String toString() {
        return "RouteMeta{" +
                "type=" + type +
                ", rawType=" + rawType +
                ", destination=" + destination +
                ", path='" + path + '\'' +
                ", group='" + group + '\'' +
                ", paramsType=" + paramsType +
                ", name='" + name + '\'' +
                ", injectConfig=" + injectConfig +
                '}';
    }
}
