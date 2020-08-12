package org.fireking.routerlibrary.enums;

public enum RouteType {
    ACTIVITY(0,"android.app.Activity"),
    SERVICE(1,"android.app.Service"),
    PROVIDER(2,""),
    CONTENT_PROVIDER(-1,"android.app.ContentProvider"),
    BOARDCAST(-1,""),
    METHOD(-1,""),
    FRAGMENT(-1,"android.app.Fragment"),
    UNKNOWN(-1,"Unknown route type");

    int id;
    String className;

    RouteType(int id, String className) {
        this.id = id;
        this.className = className;
    }

    public static RouteType parse(String name){
        for (RouteType routeType:RouteType.values()){
            if (routeType.getClassName().equals(name)){
                return routeType;
            }
        }
        return UNKNOWN;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
