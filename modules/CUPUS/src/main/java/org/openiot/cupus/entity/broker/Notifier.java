/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openiot.cupus.entity.broker;

import java.util.UUID;
import org.openiot.cupus.artefact.Publication;

/**
 *
 * @author Aleksandar
 */
public abstract class Notifier {
    
    abstract public void send(Publication pub, UUID subscriber);
}
