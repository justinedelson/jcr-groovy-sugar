package com.helemus.jcr.groovy.sugar;

import javax.jcr.Node;

import static org.junit.Assert.*;

import org.apache.sling.commons.testing.jcr.RepositoryTestBase;
import org.junit.Test;

/**
 * @author justin
 *
 */
class SweetenerTest extends RepositoryTestBase {
    
    @Override
    protected void setUp() throws Exception {
        ExpandoMetaClass.enableGlobally()
        super.setUp();
    }
   
    public void testModifyNodeMetaClass() {
        Sweetener.modifyNodeMetaClass();
        
        Node n = getTestRootNode();
        n.setProperty("testprop", "value");
        
        assertEquals "value", n.testprop
        
    }

}
