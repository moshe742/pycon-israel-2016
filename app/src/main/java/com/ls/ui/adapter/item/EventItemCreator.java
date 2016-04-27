package com.ls.ui.adapter.item;

import com.ls.pycon_israel.model.data.Event;

public interface EventItemCreator {

    EventListItem getItem(Event event);

}
