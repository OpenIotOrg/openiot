/**
*    Copyright (c) 2011-2014, OpenIoT
*   
*    This file is part of OpenIoT.
*
*    OpenIoT is free software: you can redistribute it and/or modify
*    it under the terms of the GNU Lesser General Public License as published by
*    the Free Software Foundation, version 3 of the License.
*
*    OpenIoT is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU Lesser General Public License for more details.
*
*    You should have received a copy of the GNU Lesser General Public License
*    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
*
*     Contact: OpenIoT mailto: info@openiot.eu
*/

package org.openiot.gsn.processor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import groovy.lang.Binding;
import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.StreamElement;

import org.junit.Test;

import java.io.Serializable;
import java.util.TreeMap;

public class TestScriptletProcessor {

    private static final DataField[] dataFields1 = new DataField[]{
        new DataField("temperature","INTEGER",""),
        new DataField("speed","DOUBLE",""),
        new DataField("angle","INTEGER",""),
        new DataField("image","DOUBLE","")
    };

    private static final DataField[] dataFields2 = new DataField[]{
        new DataField("temperature","INTEGER",""),
        new DataField("speed","DOUBLE",""),
        new DataField("atm","DOUBLE","")

    };

    private static final Serializable[] data1 = new Serializable[] {
            23,
            2.34,
            -9,
            -4.5
    };

    private static final Serializable[] data2 = new Serializable[] {
            23,
            2.34,
            -4.5
    };

    @Test
    public void testCorrectProcessorParameters() {
        ScriptletProcessor processor = new ScriptletProcessor();
        TreeMap<String,String> parameters = new TreeMap<String,String>();
        boolean status = processor.initialize(dataFields1, parameters);
        assertFalse(status);
        //
        parameters = new TreeMap<String,String>();
        parameters.put("anyparam","println 'Hello World!';");
        status = processor.initialize(dataFields1, parameters);
        assertFalse(status);
        //
        parameters = new TreeMap<String,String>();
        parameters.put("scriptlet","println 'Hello World!';");
        status = processor.initialize(dataFields1, parameters);
        assertTrue(status);
        //
        parameters = new TreeMap<String,String>();
        parameters.put("scriplet-periodic","println 'Hello World!';");
        status = processor.initialize(dataFields1, parameters);
        assertFalse(status);
        //
        parameters = new TreeMap<String,String>();
        parameters.put("scriplet-periodic","println 'Hello World!';");
        parameters.put("period","2000");
        status = processor.initialize(dataFields1, parameters);
        assertTrue(status);
    }

    @Test
    public void testCorrectScriptExecution() {

        ScriptletProcessor processor = getProcessor(dataFields1, "msg = 'Hello ' + gsn; def msg1 = 'This is a script internal variable.'");
        StreamElement se = new StreamElement(dataFields1, data1);
        Binding context = processor.updateContext(se);
        context.setVariable("gsn", new String("Groovy GSN"));
        processor.evaluate(processor.scriptlet, se, true);
        assertNotNull(context.getVariable("msg"));
        assertEquals(context.getVariable("msg"), "Hello Groovy GSN");

        Object o = null;
        try {
            o = context.getVariable("msg1");
        }
        catch (Exception e) {}
        assertNull(o);
    }

    @Test
    public void testStatefullScriptlet() {
       ScriptletProcessor processor = getProcessor(dataFields1, "msg = (binding.getVariables().get('msg')==null) ? '' : msg; msg = 'Hello World ' + msg + ' ' + gsn + '!'; println msg; return gsn;");
        StreamElement se = new StreamElement(dataFields1, data1);
        Binding context = processor.updateContext(se);
        context.setVariable("gsn", new String("Groovy GSN"));
        processor.evaluate(processor.scriptlet, se, true);
        assertNotNull(context.getVariable("msg"));
        assertEquals(context.getVariable("msg"), "Hello World  Groovy GSN!");

        context.setVariable("msg", new String("Stateful"));
        processor.evaluate(processor.scriptlet, se, true);
        assertEquals(context.getVariable("msg"), "Hello World Stateful Groovy GSN!");
    }

    @Test
    public void testBindingOut() {
        ScriptletProcessor processor = getProcessor(dataFields2, "return;");
        StreamElement se = new StreamElement(dataFields1, data1);
        Binding context = processor.updateContext(se);
        processor.evaluate(processor.scriptlet, se, true);

        StreamElement seo = processor.formatOutputStreamElement(context);
        assertNotNull(seo.getData("temperature"));
        assertEquals(seo.getData("temperature"), data1[0]);
        assertNotNull(seo.getData("speed"));
        assertEquals(seo.getData("speed"), data1[1]);
        assertNull(seo.getData("atm"));
    }

    @Test
    public void testTimedField() {
        ScriptletProcessor processor = getProcessor(dataFields1, "return;");
        StreamElement se = new StreamElement(dataFields1, data1);
        Binding context = processor.updateContext(se);
        processor.evaluate(processor.scriptlet, se, true);

        StreamElement seo = processor.formatOutputStreamElement(context);
        assertNotSame(seo.getTimeStamp(), 123456L);

        se.setTimeStamp(123456L);
        context = processor.updateContext(se);
        processor.evaluate(processor.scriptlet, se, true);
        seo = processor.formatOutputStreamElement(context);
        assertEquals(123456L, seo.getTimeStamp());
    }

    @Test(expected = groovy.lang.MissingMethodException.class)
    public void testScriptletExecutionWithCompilationException() {
        ScriptletProcessor processor = getProcessor(dataFields1, "prinltn 'This Groovy code has a syntax error;'");
        StreamElement se = new StreamElement(dataFields1, data1);
        Binding context = processor.updateContext(se);
        processor.evaluate(processor.scriptlet, se, true);
    }

    @Test(expected = groovy.lang.MissingPropertyException.class)
    public void testScriptletExecutionWithUnsetVariableException() {
        ScriptletProcessor processor = getProcessor(dataFields1, "println 'This variable is not set ' + thevar");
        StreamElement se = new StreamElement(dataFields1, data1);
        Binding context = processor.updateContext(se);
        context.setVariable("gsn", new String("Groovy GSN"));
        processor.evaluate(processor.scriptlet, se, true);
    }

    @Test(expected = Exception.class)
    public void testScriptletExecutionWithSyntaxError() {
        ScriptletProcessor processor = getProcessor(dataFields1, "this code is not groovy");
        StreamElement se = new StreamElement(dataFields1, data1);
        Binding context = processor.updateContext(se);
        processor.evaluate(processor.scriptlet, se, true);
    }

    //

    private ScriptletProcessor getProcessor(DataField[] outputStructure, String scriptlet) {
        ScriptletProcessor processor = new ScriptletProcessor();
        TreeMap<String,String> parameters = new TreeMap<String,String>();
        parameters.put("scriptlet", scriptlet);
        processor.initialize(outputStructure, parameters);
        return processor;
    }

}
