package org.dockit.dockitserver.controllers.utils;

public class ParameterValidator {
    @SafeVarargs
    public static <T> boolean invalid(T... parameters) {
        for(T parameter : parameters) {
            if (parameter == null) {
                return true;
            }
        }
        return false;
    }
}
