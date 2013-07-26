/*******************************************************************************
 * Copyright (c) 2011-2014, OpenIoT
 *  
 *  This library is free software; you can redistribute it and/or
 *  modify it either under the terms of the GNU Lesser General Public
 *  License version 2.1 as published by the Free Software Foundation
 *  (the "LGPL"). If you do not alter this
 *  notice, a recipient may use your version of this file under the LGPL.
 *  
 *  You should have received a copy of the LGPL along with this library
 *  in the file COPYING-LGPL-2.1; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *  
 *  This software is distributed on an "AS IS" basis, WITHOUT WARRANTY
 *  OF ANY KIND, either express or implied. See the LGPL  for
 *  the specific language governing rights and limitations.
 *  
 *  Contact: OpenIoT mailto: info@openiot.eu
 ******************************************************************************/
package org.openiot.ui.request.commons.models;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import org.openiot.ui.request.commons.util.StringUtils;

/**
 * Decorate a map with a modified put method that sets a flag when the map is
 * modified
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class ObservableMap<K, V> extends Observable implements Map<K, V>, Serializable {
	private static final long serialVersionUID = 1L;

    private Map<K, V> wrappedMap;

    public ObservableMap(Map<K, V> inputMap) {
        this.wrappedMap = inputMap;
    }

    public int size() {
        return wrappedMap.size();
    }

    public boolean isEmpty() {
        return wrappedMap.isEmpty();
    }

    public boolean containsKey(Object key) {
        return wrappedMap.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return wrappedMap.containsValue(value);
    }

    public V get(Object key) {
        return wrappedMap.get(key);
    }

    public V remove(Object key) {
        V val = wrappedMap.remove(key);
        setChanged();
        try{
        	notifyObservers();
        }catch(Throwable ex){
        	ex.printStackTrace();
        }

        return val;
    }

    public void clear() {
        wrappedMap.clear();
        setChanged();
        try{
        	notifyObservers();
        }catch(Throwable ex){
        	ex.printStackTrace();
        }
    }

    public Set<K> keySet() {
        return wrappedMap.keySet();
    }

    public Collection<V> values() {
        return wrappedMap.values();
    }

    public Set<Entry<K, V>> entrySet() {
        return wrappedMap.entrySet();
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        boolean modified = false;
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            // Get original value and check for modifications.
            @SuppressWarnings("unchecked")
			V originalValue = (V) StringUtils.nullIfEmpty(wrappedMap.get(entry.getKey()));
            @SuppressWarnings("unchecked")
			V trimmedValue = (V) StringUtils.nullIfEmpty(entry.getValue());
            if ((originalValue == null && trimmedValue != null)
                    || (originalValue != null && trimmedValue == null)
                    || (originalValue != null && trimmedValue != null && !originalValue.toString().equals(trimmedValue.toString()))) {
            } else {
                // Nothing to modify
                continue;
            }

            // Update map value and notify observers
            wrappedMap.put(entry.getKey(), entry.getValue());
            modified = true;
        }

        try{
        	notifyObservers();
        }catch(Throwable ex){
        	ex.printStackTrace();
        }
    }

    public V put(K key, V valueIn) {
        // Get original value and check for modifications.
        @SuppressWarnings("unchecked")
		V originalValue = (V) StringUtils.nullIfEmpty(wrappedMap.get(key));
        @SuppressWarnings("unchecked")
		V trimmedValue = (V) StringUtils.nullIfEmpty(valueIn);
        if ((originalValue == null && trimmedValue != null)
                || (originalValue != null && trimmedValue == null)
                || (originalValue != null && trimmedValue != null && !originalValue.toString().equals(trimmedValue.toString()))) {
        } else {
            // Nothing to modify
            return valueIn;
        }

        // Update map value and notify observers
        wrappedMap.put(key, valueIn);
        setChanged();
        try{
        	notifyObservers(key);
        }catch(Throwable ex){
        	ex.printStackTrace();
        }

        return valueIn;
    }

    public Map<K, V> getWrappedMap() {
        return wrappedMap;
    }
    
    public String toString(){
        return wrappedMap.toString();
    }
}
