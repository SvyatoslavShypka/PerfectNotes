package com.goit.finalproject.access;

import java.util.Locale;

public enum Access {
    PUBLIC,
    PRIVATE;

    public static Access getAccess(String accessType) {
       return Access.valueOf(accessType.toUpperCase(Locale.ROOT));
    }
}
