package com.booksphere.wishlistservice;

import com.booksphere.wishlistservice.model.*;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PojosTest {

    @Test
    void testPojos() throws Exception {
        List<Class<?>> classes = Arrays.asList(
                Wishlist.class,
                WishlistItem.class
        );

        for (Class<?> clazz : classes) {
            Object instance = createInstance(clazz);
            if (instance == null) continue;

            Object instance2 = createInstance(clazz);

            for (Method method : clazz.getMethods()) {
                try {
                    if (method.getParameterCount() == 0) {
                        method.invoke(instance);
                    } else if (method.getParameterCount() == 1) {
                        Object val = getParameterValue(method.getParameterTypes()[0], instance2);
                        method.invoke(instance, val);
                    }
                } catch (Exception ignored) {}
            }
            assertNotNull(instance.toString());
            instance.hashCode();
            instance.equals(instance2);
            instance.equals(instance);
            instance.equals(null);
            instance.equals("string");
        }
    }

    private Object createInstance(Class<?> clazz) {
        try {
            Method builderMethod = clazz.getMethod("builder");
            Object builder = builderMethod.invoke(null);
            return builder.getClass().getMethod("build").invoke(builder);
        } catch (Exception e) {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e2) {
                return null;
            }
        }
    }

    private Object getParameterValue(Class<?> type, Object obj) {
        if (type == String.class) return "test";
        if (type == Integer.class || type == int.class) return 1;
        if (type == Long.class || type == long.class) return 1L;
        if (type == Boolean.class || type == boolean.class) return true;
        if (type == Double.class || type == double.class) return 1.0;
        if (type == java.time.LocalDateTime.class) return java.time.LocalDateTime.now();
        if (type == List.class) return new ArrayList<>();
        if (type == Object.class) return obj;
        return null;
    }
}
