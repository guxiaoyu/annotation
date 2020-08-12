package org.fireking.processorlib.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class RouteDoc {

    @JSONField(ordinal = 1)
    private String group;
    @JSONField(ordinal = 2)
    private String path;
    @JSONField(ordinal = 3)
    private String description;
    @JSONField(ordinal = 4)
    private String className;
    @JSONField(ordinal = 5)
    private String type;
    @JSONField(ordinal = 6)
    private List<Param> params;

    public List<Param> getParams() {
        return params;
    }

    public void setParams(List<Param> params) {
        this.params = params;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public static class Param{
        @JSONField(ordinal = 1)
        private String key;
        @JSONField(ordinal = 2)
        private String type;
        @JSONField(ordinal = 3)
        private String description;
        @JSONField(ordinal = 4)
        private boolean required;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }
    }
}
