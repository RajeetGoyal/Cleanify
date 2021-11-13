package com.example.cleanify.data;

import java.util.ArrayList;
import java.util.Random;

public class FactCardUrls {
    /***
     *
     * @return A string value that represents a random url from the available ones.
     */
    public static String getFactCardUrl() {
        ArrayList<String> urls = new ArrayList<>();
        urls.add("https://i.imgur.com/yhUBzaU.jpg");
        urls.add("https://i.imgur.com/prWG1Zw.jpg");
        urls.add("https://i.imgur.com/i5Vj3Tn.jpg");
        urls.add("https://i.imgur.com/6kazxKu.jpg");
        urls.add("https://i.imgur.com/ibJPSeV.jpg");

        Random random = new Random();
        return urls.get(random.nextInt(5));
    }
}
