package com.example.eticaretapp.eventbus;

import org.greenrobot.eventbus.Subscribe;

public class EventSubscriber {

    @Subscribe
    public void onDataEvent(DataEvent event) {
        // Olay tetiklendiğinde bu metod çalışacak ve veriyi alacak
        System.out.println("Gelen veri: " + event.data);
    }
}
