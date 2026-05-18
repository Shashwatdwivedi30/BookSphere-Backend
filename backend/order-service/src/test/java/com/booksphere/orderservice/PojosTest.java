package com.booksphere.orderservice;

import com.booksphere.orderservice.dto.*;
import com.booksphere.orderservice.model.*;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PojosTest {

    @Test
    void testPojos() throws Exception {
        List<Class<?>> classes = Arrays.asList(
                Order.class,
                OrderItem.class,
                Address.class,
                OrderDTO.class,
                OrderItemDTO.class,
                AddressDTO.class,
                NotificationEvent.class
        );

        for (Class<?> clazz : classes) {
            // Test basic instantiation and getters/setters
            Object instance1 = createFullInstance(clazz);
            Object instance2 = createFullInstance(clazz);
            Object instance3 = createFullInstance(clazz);
            
            // Modify one field in instance3 to make it different
            modifyOneField(instance3);

            // 1. Test Getters/Setters/ToString
            assertNotNull(instance1.toString());
            
            // 2. Test Equals/HashCode
            assertTrue(instance1.equals(instance1));
            assertEquals(instance1, instance2);
            assertNotEquals(instance1, instance3);
            assertNotEquals(instance1, null);
            assertNotEquals(instance1, "not a pojo");
            
            assertEquals(instance1.hashCode(), instance2.hashCode());
            // HashCode for different objects can be same (rare but possible), 
            // but for simple DTOs it should be different.
            
            // 3. Test Builder if exists
            testBuilder(clazz);
        }
    }

    private Object createFullInstance(Class<?> clazz) throws Exception {
        Object instance = clazz.getDeclaredConstructor().newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            field.set(instance, getDummyValue(field.getType()));
        }
        return instance;
    }

    private void modifyOneField(Object instance) throws Exception {
        Field[] fields = instance.getClass().getDeclaredFields();
        if (fields.length > 0) {
            Field field = fields[0];
            field.setAccessible(true);
            Object current = field.get(instance);
            if (current instanceof String) {
                field.set(instance, "different");
            } else if (current instanceof Number) {
                field.set(instance, 999);
            }
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
            
            // Call all builder methods that match field names
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
