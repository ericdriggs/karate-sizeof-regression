//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.intuit.karate.core;

import com.intuit.karate.FileUtils;
import com.intuit.karate.Json;
import com.intuit.karate.JsonUtils;
import com.intuit.karate.XmlUtils;
import com.intuit.karate.core.Feature;

import com.intuit.karate.graal.JsValue;

import java.util.*;
import java.util.function.Function;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

public class Variable {
    private static final Logger logger = LoggerFactory.getLogger(Variable.class);
    public static final Variable NULL = new Variable((Object)null);
    public static final Variable NOT_PRESENT = new Variable("#notpresent");
    public final Type type;
    private final Object value;

    public Variable(Object o) {
        if (o instanceof Value) {
            o = (new JsValue((Value)o)).getValue();
        } else if (o instanceof JsValue) {
            o = ((JsValue)o).getValue();
        }

        if (o == null) {
            this.type = Variable.Type.NULL;
        } else if (o instanceof Value) {
            Value v = (Value)o;
            if (v.canExecute()) {
                this.type = Variable.Type.JS_FUNCTION;
            } else {
                this.type = Variable.Type.OTHER;
            }
        } else if (o instanceof Function) {
            this.type = Variable.Type.JAVA_FUNCTION;
        } else if (o instanceof Node) {
            this.type = Variable.Type.XML;
        } else if (o.getClass().isArray()) {
            this.type = Variable.Type.ARRAY;
        } else if (o instanceof List) {
            this.type = Variable.Type.LIST;
        } else if (o instanceof Set) {
            this.type = Variable.Type.SET;
        }else if (o instanceof Map) {
            this.type = Variable.Type.MAP;
        } else if (o instanceof String) {
            this.type = Variable.Type.STRING;
        } else if (Number.class.isAssignableFrom(o.getClass())) {
            this.type = Variable.Type.NUMBER;
        } else if (Boolean.class.equals(o.getClass())) {
            this.type = Variable.Type.BOOLEAN;
        } else if (o instanceof byte[]) {
            this.type = Variable.Type.BYTES;
        } else if (o instanceof Feature) {
            this.type = Variable.Type.FEATURE;
        } else {
            this.type = Variable.Type.OTHER;
        }

        this.value = o;
    }

    public <T> T getValue() {
        return (T)this.value;
    }

    public boolean isJsOrJavaFunction() {
        return this.type == Variable.Type.JS_FUNCTION || this.type == Variable.Type.JAVA_FUNCTION;
    }

    public boolean isJavaFunction() {
        return this.type == Variable.Type.JAVA_FUNCTION;
    }

    public boolean isJsFunction() {
        return this.type == Variable.Type.JS_FUNCTION;
    }


    public boolean isBytes() {
        return this.type == Variable.Type.BYTES;
    }

    public boolean isString() {
        return this.type == Variable.Type.STRING;
    }

    public boolean isArray() {
        return this.type == Variable.Type.ARRAY;
    }

    public boolean isList() {
        return this.type == Variable.Type.LIST;
    }

    public boolean isMap() {
        return this.type == Variable.Type.MAP;
    }

    public boolean isSet() {
        return this.type == Variable.Type.SET;
    }

    public boolean isMapOrList() {
        return this.type == Variable.Type.MAP || this.type == Variable.Type.LIST;
    }

    public boolean isXml() {
        return this.type == Variable.Type.XML;
    }

    public boolean isNumber() {
        return this.type == Variable.Type.NUMBER;
    }

    public boolean isNull() {
        return this.type == Variable.Type.NULL;
    }

    public boolean isOther() {
        return this.type == Variable.Type.OTHER;
    }

    public boolean isFeature() {
        return this.type == Variable.Type.FEATURE;
    }

    public boolean isBoolean() {
        return this.type == Variable.Type.BOOLEAN;
    }

    public boolean isTrue() {
        return this.type == Variable.Type.BOOLEAN && (Boolean)this.value;
    }

    public String getTypeString() {
        return this.type.name().toLowerCase();
    }

    public Node getAsXml() {
        switch (this.type) {
            case XML:
                return (Node)this.getValue();
            case MAP:
                return XmlUtils.fromMap((Map)this.getValue());
            case STRING:
            case BYTES:
                String xml = this.getAsString();
                return XmlUtils.toXmlDoc(xml);
            case OTHER:
                return XmlUtils.fromJavaObject(this.value);
            default:
                throw new RuntimeException("cannot convert to xml:" + this);
        }
    }

    public Object getValueAndConvertIfXmlToMap() {
        return this.isXml() ? XmlUtils.toObject((Node)this.getValue()) : this.value;
    }

    public Object getValueAndForceParsingAsJson() {
        switch (this.type) {
            case XML:
                return XmlUtils.toObject((Node)this.getValue());
            case MAP:
            case LIST:
                return this.value;
            case STRING:
            case BYTES:
                return JsonUtils.fromJson(this.getAsString());
            case OTHER:
                return Json.of(this.value).value();
            default:
                throw new RuntimeException("cannot convert to json: " + this);
        }
    }

    public byte[] getAsByteArray() {
        return this.type == Variable.Type.BYTES ? (byte[])this.getValue() : FileUtils.toBytes(this.getAsString());
    }

    public String getAsString() {
        switch (this.type) {
            case XML:
                return XmlUtils.toString((Node)this.getValue());
            case MAP:
            case LIST:
                try {
                    return JsonUtils.toJson(this.value);
                } catch (Throwable var2) {
                    logger.warn("conversion to json string failed, will attempt to use fall-back approach: {}", var2.getMessage());
                    return JsonUtils.toJsonSafe(this.value, false);
                }
            case STRING:
            case OTHER:
            default:
                return this.value.toString();
            case BYTES:
                return FileUtils.toString((byte[])((byte[])this.value));
            case NULL:
                return null;
        }
    }

    public String getAsPrettyString() {
        switch (this.type) {
            case XML:
                return this.getAsPrettyXmlString();
            case MAP:
            case LIST:
                return JsonUtils.toJsonSafe(this.value, true);
            default:
                return this.getAsString();
        }
    }

    public String getAsPrettyXmlString() {
        return XmlUtils.toString(this.getAsXml(), true);
    }

    public int getAsInt() {
        return this.isNumber() ? ((Number)this.value).intValue() : Integer.valueOf(this.getAsString());
    }

    public Variable copy(boolean deep) {
        switch (this.type) {
            case XML:
                return new Variable(XmlUtils.toXmlDoc(this.getAsString()));
            case MAP:
                return deep ? new Variable(JsonUtils.deepCopy(this.value)) : new Variable(new LinkedHashMap((Map)this.value));
            case LIST:
                return deep ? new Variable(JsonUtils.deepCopy(this.value)) : new Variable(new ArrayList((List)this.value));
            default:
                return this;
        }
    }

    public Variable toLowerCase() {
        switch (this.type) {
            case XML:
                String xml = this.getAsString().toLowerCase();
                return new Variable(XmlUtils.toXmlDoc(xml));
            case MAP:
            case LIST:
                String json = this.getAsString().toLowerCase();
                return new Variable(JsonUtils.fromJson(json));
            case STRING:
                return new Variable(this.getAsString().toLowerCase());
            case BYTES:
            case OTHER:
            default:
                return this;
        }
    }

    public boolean isNotPresent() {
        return "#notpresent".equals(this.value);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[type: ").append(this.type);
        sb.append(", value: ").append(this.value);
        sb.append("]");
        return sb.toString();
    }

    public static enum Type {
        NULL,
        BOOLEAN,
        NUMBER,
        STRING,
        BYTES,
        ARRAY,
        LIST,
        SET,
        MAP,
        XML,
        JS_FUNCTION,
        JAVA_FUNCTION,
        FEATURE,
        OTHER;

        private Type() {
        }
    }
}
