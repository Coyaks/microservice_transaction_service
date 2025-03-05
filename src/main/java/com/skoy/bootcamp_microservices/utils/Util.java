package com.skoy.bootcamp_microservices.utils;

import java.math.BigDecimal;

public class Util {

    public static BigDecimal convertObjectToBigDecimal(Object obj) {
        if (obj instanceof BigDecimal) {
            return (BigDecimal) obj;
        } else if (obj instanceof String) {
            return new BigDecimal((String) obj);
        } else if (obj instanceof Number) {
            return BigDecimal.valueOf(((Number) obj).doubleValue());
        } else {
            throw new IllegalArgumentException("Cannot convert object to BigDecimal");
        }
    }

}
