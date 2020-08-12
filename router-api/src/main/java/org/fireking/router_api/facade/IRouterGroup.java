package org.fireking.router_api.facade;

import org.fireking.routerlibrary.model.RouteMeta;

import java.util.Map;

public interface IRouterGroup {

    void loadInto(Map<String, RouteMeta> atlas);
}
