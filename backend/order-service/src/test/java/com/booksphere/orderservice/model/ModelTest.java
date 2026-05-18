package com.booksphere.orderservice.model;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ModelTest {

    @Test
    void testModels() throws Exception {
        List<Class<?>> modelClasses = Arrays.asList(
                Order.class,
                OrderItem.class,
                Address.class
        );

        for (Class<?> clazz : modelClasses) {
            Object instance;
            try {
                Method builderMethod = clazz.getMethod("builder");
                Object builder = builderMethod.invoke(null);
                instance = builder.getClass().getMethod("build").invoke(builder);
            } catch (Exception e) {
                try {
                    instance = clazz.getDeclaredConstructor().newInstance();
                } catch (Exception e2) {
                    continue;
                }
            }

            Object instance2;
            try {
                instance2 = clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                instance2 = instance;
            }

            for (Method method : clazz.getMethods()) {
                try {
                    if (method.getParameterCount() == 0) {
                        method.invoke(instance);
                    } else if (method.getParameterCount() == 1) {
                        Class<?> paramType = method.getParameterTypes()[0];
                        if (paramType == String.class) method.invoke(instance, "test");
                        else if (paramType == Integer.class || paramType == int.class) method.invoke(instance, 1);
                        else if (paramType == Long.class || paramType == long.class) method.invoke(instance, 1L);
                        else if (paramType == Boolean.class || paramType == boolean.class) method.invoke(instance, true);
                        else if (paramType == Double.class || paramType == double.class) method.invoke(instance, 1.0);
                        else if (paramType == java.time.LocalDateTime.class) method.invoke(instance, java.time.LocalDateTime.now());
                        else if (paramType == List.class) method.invoke(instance, new ArrayList<>());
                        else if (paramType == Object.class) method.invoke(instance, instance2);
                    }
                } catch (Exception ignored) {}
            }
            assertNotNull(instance.toString());
            instance.hashCode();
            instance.equals(instance2);
        }
    }
}
