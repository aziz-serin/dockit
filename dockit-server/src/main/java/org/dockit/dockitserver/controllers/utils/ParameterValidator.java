package org.dockit.dockitserver.controllers.utils;

public class ParameterValidator {
    @SafeVarargs
    public static <T> boolean valid(T... parameters) {
        for(T parameter : parameters) {
            if (parameter == null) {
                return false;
            }
        }
        return true;
    }
}
