package main.java.utils;

import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Utilitaire {

    public static Object conversionType(Parameter p, String value) {
        Class<?> clazz = p.getType();

        // Valeur null ou vide : on renvoie la valeur par défaut du type
        if (value == null || value.trim().isEmpty()) {
            return valeurParDefaut(clazz);
        }

        try {
            if (clazz == String.class || clazz == Object.class || clazz == CharSequence.class) {
                return value;
            } else if (clazz == int.class || clazz == Integer.class) {
                return Integer.parseInt(value.trim());
            } else if (clazz == long.class || clazz == Long.class) {
                return Long.parseLong(value.trim());
            } else if (clazz == double.class || clazz == Double.class) {
                return Double.parseDouble(value.trim());
            } else if (clazz == float.class || clazz == Float.class) {
                return Float.parseFloat(value.trim());
            } else if (clazz == short.class || clazz == Short.class) {
                return Short.parseShort(value.trim());
            } else if (clazz == byte.class || clazz == Byte.class) {
                return Byte.parseByte(value.trim());
            } else if (clazz == boolean.class || clazz == Boolean.class) {
                return Boolean.parseBoolean(value.trim());
            } else if (clazz == char.class || clazz == Character.class) {
                if (value.length() != 1) {
                    throw new IllegalArgumentException("Un seul caractère attendu : " + value);
                }
                return value.charAt(0);
            } else if (clazz == BigDecimal.class) {
                return new BigDecimal(value.trim());
            } else if (clazz == BigInteger.class) {
                return new BigInteger(value.trim());
            } else if (clazz == LocalDate.class) {
                return LocalDate.parse(value.trim());
            } else if (clazz == LocalDateTime.class) {
                return LocalDateTime.parse(value.trim());
            } else if (clazz.isEnum()) {
                @SuppressWarnings({"unchecked", "rawtypes"})
                Object e = Enum.valueOf((Class<Enum>) clazz, value.trim());
                return e;
            }
        } catch (NumberFormatException | java.time.format.DateTimeParseException ex) {
            throw new IllegalArgumentException(
                "Impossible de convertir \"" + value + "\" en " + clazz.getSimpleName(), ex);
        }

        throw new IllegalArgumentException("Type non supporté : " + clazz.getName());
    }

    public static Object valeurParDefaut(Class<?> clazz) {
        if (clazz == int.class || clazz == Integer.class)       return 0;
        if (clazz == long.class || clazz == Long.class)         return 0L;
        if (clazz == double.class || clazz == Double.class)     return 0.0;
        if (clazz == float.class || clazz == Float.class)       return 0.0f;
        if (clazz == short.class || clazz == Short.class)       return (short) 0;
        if (clazz == byte.class || clazz == Byte.class)         return (byte) 0;
        if (clazz == boolean.class || clazz == Boolean.class)   return false;
        if (clazz == char.class || clazz == Character.class)    return '\0';
        if (clazz == String.class )                             return "";  
        if (clazz == BigDecimal.class)                          return BigDecimal.ZERO;
        if (clazz == BigInteger.class)                          return BigInteger.ZERO;
        if (clazz == LocalDate.class)                           return LocalDate.now();
        if (clazz == LocalDateTime.class)                       return LocalDateTime.now();

        // String, dates, enums, objets personnalisés... : null
        return null;
    }
}
