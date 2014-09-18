package org.openiot.cupus.mobile.entity.mobilebroker;

import java.util.Set;

/**
 * Created by Kristijan on 27.01.14..
 */
public interface AnnouncementListener {
    void announcement(Set<String> subscriptionAttributes, boolean unsubscribe);
}
