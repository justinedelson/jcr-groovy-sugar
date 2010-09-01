package com.helemus.jcr.groovy.sugar

import javax.jcr.Node
import javax.jcr.Value
import javax.jcr.Property
import javax.jcr.PropertyType

class Sweetener {
    
    public static convertNodeToJavaObject(Node node, Value value) {
        if (value.getType() == PropertyType.REFERENCE) {
            String nodeUuid = value.getString()
            return node.getSession().getNodeByUUID(nodeUuid)
        } else {
            switch (value.getType()) {
                case PropertyType.BINARY:
                    // TODO - add lazyinputstream
                    return value;
                case PropertyType.BOOLEAN:
                    return value.getBoolean();
                case PropertyType.DATE:
                    return value.getDate();
                case PropertyType.DOUBLE:
                    return value.getDouble();
                case PropertyType.LONG:
                    return value.getLong();
                case PropertyType.NAME: // fall through
                case PropertyType.PATH: // fall through
                case PropertyType.WEAKREFERENCE: // fall through
                case PropertyType.REFERENCE: // fall through
                case PropertyType.STRING: // fall through
                case PropertyType.UNDEFINED: // not actually expected
                default: // not actually expected
                    return value.getString();
            }
        }
    }
    
    public static void modifyNodeMetaClass() {
        def mc = Node.metaClass
        mc.getProperty = getNodeProperty
    }
    
    private static getNodeProperty = { String name ->
        def metaProperty = Node.metaClass.getMetaProperty(name)
        
        def result
        if (metaProperty) {
            result = metaProperty.getProperty(delegate)
        } else {
            result = []
            
            def it = delegate.getNodes(name);
            while (it.hasNext()) {
                result << it.nextNode()
            }
            
            boolean isMulti = false;
            
            it = delegate.getProperties(name);
            while (it.hasNext()) {
                Property prop = it.nextProperty();
                if (prop.getDefinition().isMultiple()) {
                    isMulti = true;
                    Value[] values = prop.getValues();
                    for (int i = 0; i < values.length; i++) {
                        result << Sweetener.convertNodeToJavaObject(delegate, values[i])
                    }
                } else {
                    result << Sweetener.convertNodeToJavaObject(delegate, prop.getValue())
                }
            }
        }
        
        if (result.size() == 0) {
            return null
        } else if (result.size() == 1) {
            return result[0]
        } else {
            return result
        }
    }
}
