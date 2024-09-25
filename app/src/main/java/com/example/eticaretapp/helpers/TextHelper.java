package com.example.eticaretapp.helpers;

public class TextHelper {


    public static boolean containSpace(String... texts) {
        for (String text : texts) {
            if (text.contains(" "))
                return true;
        }
        return false;

    }

    public static boolean isNullOrEmpty(String... texts) {
        for (String text : texts) {
            if (text == null)
                return true;
            else if (text.trim().isEmpty())
                return true;
        }
        return false;
    }

}
