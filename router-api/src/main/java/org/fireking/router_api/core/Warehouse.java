package org.fireking.router_api.core;

import org.fireking.router_api.facade.IProvider;
import org.fireking.router_api.facade.IRouterGroup;
import org.fireking.routerlibrary.model.RouteMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Warehouse {
    // Cache route and metas
    static Map<String, Class<? extends IRouterGroup>> groupsIndex = new HashMap<>();
    static Map<String, RouteMeta> routes = new HashMap<>();

    // Cache provider
    static Map<Class, IProvider> providers = new HashMap<>();
    static Map<String, RouteMeta> providersIndex = new HashMap<>();


    static void clear() {
        routes.clear();
        groupsIndex.clear();
        providers.clear();
        providersIndex.clear();
    }
}
