package org.dockit.dockitserver.controllers.utils;

/**
 * Utility class to validate given body parameters.
 */
public class ParameterValidator {


    /**
     * Check if there is any invalid given parameter
     *
     * @param parameters body parameters for a given request
     * @param <T> represents any type of given body parameter for a request
     * @return true if any null, false if all parameters are non-null
     */
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
