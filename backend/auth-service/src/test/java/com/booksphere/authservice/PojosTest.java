package com.booksphere.authservice;

import com.booksphere.authservice.dto.*;
import com.booksphere.authservice.model.*;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PojosTest {

    @Test
    void testPojos() throws Exception {
        List<Class<?>> classes = Arrays.asList(
                User.class,
                Address.class,
                Otp.class,
                LoginResponse.class,
                UserDTO.class,
                AddressDTO.class,
                NotificationEvent.class
        );

        for (Class<?> clazz : classes) {
            Object instance1 = createFullInstance(clazz);
            Object instance2 = createFullInstance(clazz);
            Object instance3 = createFullInstance(clazz);
            modifyOneField(instance3);

            assertNotNull(instance1.toString());
            assertTrue(instance1.equals(instance1));
            assertEquals(instance1, instance2);
            assertNotEquals(instance1, instance3);
            assertNotEquals(instance1, null);
            assertEquals(instance1.hashCode(), instance2.hashCode());
            testBuilder(clazz);
        }
    }

    private Object createFullInstance(Class<?> clazz) throws Exception {
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                field.set(instance, getDummyValue(field.getType()));
            }
            return instance;
        } catch (Exception e) {
            // Fallback for classes without default constructor
            return null;
        }
    }

    private void modifyOneField(Object instance) throws Exception {
        if (instance == null) return;
        Field[] fields = instance.getClass().getDeclaredFields();
        if (fields.length > 0) {
            Field field = fields[0];
            field.setAccessible(true);
            field.set(instance, null); // Change to null to hit null-check branches
        }
    }

    private Object getDummyValue(Class<?> type) {
        if (type == String.class) return "test";
        if (type == Integer.class || type == int.class) return 1;
        if (type == Long.class || type == long.class) return 1L;
        if (type == Double.class || type == double.class) return 1.0;
        if (type == Boolean.class || type == boolean.class) return true;
        if (type == List.class) return new ArrayList<>();
        if (type == java.time.LocalDateTime.class) return java.time.LocalDateTime.now();
        if (type == Address.class) return new Address();
        if (type == AddressDTO.class) return new AddressDTO();
        return null;
    }

    private void testBuilder(Class<?> clazz) {
        try {
            Method builderMethod = clazz.getMethod("builder");
            Object builder = builderMethod.invoke(null);
            for (Field field : clazz.getDeclaredFields()) {
                try {
                    Method m = builder.getClass().getMethod(field.getName(), field.getType());
                    m.invoke(builder, getDummyValue(field.getType()));
                } catch (Exception ignored) {}
            }
            Object built = builder.getClass().getMethod("build").invoke(builder);
            assertNotNull(built);
        } catch (Exception ignored) {}
    }
}
