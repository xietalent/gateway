package com.hrocloud.apigw.utils;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;


/**
 * Created by liqingdong911 on 2015/1/15.
 */

public class DubboBeanCodecUtil {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final ConcurrentMap<String, Method> NAME_METHODS_CACHE = new ConcurrentHashMap<String, Method>();
    private static final ConcurrentMap<Class<?>, ConcurrentMap<String, Field>> CLASS_FIELD_CACHE =
            new ConcurrentHashMap<Class<?>, ConcurrentMap<String, Field>>();

    public static Object[] generalize(Object[] objs) {
        Object[] dests = new Object[objs.length];
        for (int i = 0; i < objs.length; i ++) {
            dests[i] = generalize(objs[i]);
        }
        return dests;
    }


    public static Object generalize(Object pojo) {
        return generalize(pojo, new IdentityHashMap<Object, Object>());
    }

    private static Map createMap(Map src) {
        Class<? extends Map> cl = src.getClass();
        Map result = null;
        if (HashMap.class == cl) {
            result = new HashMap();
        } else if (Hashtable.class == cl) {
            result = new Hashtable();
        } else if (IdentityHashMap.class == cl) {
            result = new IdentityHashMap();
        } else if (LinkedHashMap.class == cl) {
            result = new LinkedHashMap();
        } else if (Properties.class == cl) {
            result = new Properties();
        } else if (TreeMap.class == cl) {
            result = new TreeMap();
        } else if (WeakHashMap.class == cl) {
            return new WeakHashMap();
        } else if (ConcurrentHashMap.class == cl) {
            result = new ConcurrentHashMap();
        } else if (ConcurrentSkipListMap.class == cl) {
            result = new ConcurrentSkipListMap();
        } else {
            try {
                result = cl.newInstance();
            } catch (Exception e) { /* ignore */ }

            if (result == null) {
                try {
                    Constructor<?> constructor = cl.getConstructor(Map.class);
                    result = (Map)constructor.newInstance(Collections.EMPTY_MAP);
                } catch (Exception e) { /* ignore */ }
            }
        }

        if (result == null) {
            result = new HashMap<Object, Object>();
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private static Object generalize(Object pojo, Map<Object, Object> history) {
        if (pojo == null) {
            return null;
        }

        if (pojo instanceof Enum<?>) {
            return ((Enum<?>)pojo).name();
        }
        if (pojo.getClass().isArray()
                && Enum.class.isAssignableFrom(
                pojo.getClass().getComponentType())) {
            int len = Array.getLength(pojo);
            String[] values = new String[len];
            for (int i = 0; i < len; i ++) {
                values[i] = ((Enum<?>) Array.get(pojo, i)).name();
            }
            return values;
        }

        if (isPrimitives(pojo.getClass())) {
            return pojo;
        }

        if (pojo instanceof Class) {
            return ((Class)pojo).getName();
        }

        Object o = history.get(pojo);
        if(o != null){
            return o;
        }
        history.put(pojo, pojo);

        if (pojo.getClass().isArray()) {
            int len = Array.getLength(pojo);
            Object[] dest = new Object[len];
            history.put(pojo, dest);
            for (int i = 0; i < len; i ++) {
                Object obj = Array.get(pojo, i);
                dest[i] = generalize(obj, history);
            }
            return dest;
        }
        if (pojo instanceof Collection<?>) {
            Collection<Object> src = (Collection<Object>)pojo;
            int len = src.size();
            Collection<Object> dest = (pojo instanceof List<?>) ? new ArrayList<Object>(len) : new HashSet<Object>(len);
            history.put(pojo, dest);
            for (Object obj : src) {
                dest.add(generalize(obj, history));
            }
            return dest;
        }
        if (pojo instanceof Map<?, ?>) {
            Map<Object, Object> src = (Map<Object, Object>)pojo;
            Map<Object, Object> dest= createMap(src);
            history.put(pojo, dest);
            for (Map.Entry<Object, Object> obj : src.entrySet()) {
                dest.put(generalize(obj.getKey(), history), generalize(obj.getValue(), history));
            }
            return dest;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        history.put(pojo, map);
        map.put("class", pojo.getClass().getName());
        for (Method method : pojo.getClass().getMethods()) {
            if (isBeanPropertyReadMethod(method)) {
                try {
                    map.put(getPropertyNameFromBeanReadMethod(method),
                            generalize(method.invoke(pojo), history));
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        // public field
        for(Field field : pojo.getClass().getFields()) {
            if (isPublicInstanceField(field)) {
                try {
                    Object fieldValue = field.get(pojo);
                    // public filed同时也有get/set方法，如果get/set存取的不是前面那个 public field 该如何处理
                    if (history.containsKey(pojo)) {
                        Object pojoGenerilizedValue = history.get(pojo);
                        if (pojoGenerilizedValue instanceof Map
                                && ((Map)pojoGenerilizedValue).containsKey(field.getName())) {
                            continue;
                        }
                    }
                    if (fieldValue != null) {
                        map.put(field.getName(), generalize(fieldValue, history));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private static Collection<Object> createCollection(Class<?> type, int len) {
        if (type.isAssignableFrom(ArrayList.class)) {
            return  new ArrayList<Object>(len);
        }
        if (type.isAssignableFrom(HashSet.class)) {
            return new HashSet<Object>(len);
        }
        if (! type.isInterface() && ! Modifier.isAbstract(type.getModifiers())) {
            try {
                return (Collection<Object>) type.newInstance();
            } catch (Exception e) {
                // ignore
            }
        }
        return new ArrayList<Object>();
    }


    /**
     * 获取范型的类型
     * @param genericType
     * @param index
     * @return List<Person>  返回Person.class ,Map<String,Person> index=0 返回String.class index=1 返回Person.class
     */
    private static Type getGenericClassByIndex(Type genericType, int index){
        Type clazz = null ;
        //范型参数转换
        if (genericType instanceof ParameterizedType){
            ParameterizedType t = (ParameterizedType)genericType;
            Type[] types = t.getActualTypeArguments();
            clazz = types[index];
        }
        return clazz;
    }

    private static Object newInstance(Class<?> cls) {
        try {
            return cls.newInstance();
        } catch (Throwable t) {
            try {
                Constructor<?>[] constructors = cls.getConstructors();
                if (constructors != null && constructors.length == 0) {
                    throw new RuntimeException("Illegal constructor: " + cls.getName());
                }
                Constructor<?> constructor = constructors[0];
                if (constructor.getParameterTypes().length > 0) {
                    for (Constructor<?> c : constructors) {
                        if (c.getParameterTypes().length <
                                constructor.getParameterTypes().length) {
                            constructor = c;
                            if (constructor.getParameterTypes().length == 0) {
                                break;
                            }
                        }
                    }
                }
                return constructor.newInstance(new Object[constructor.getParameterTypes().length]);
            } catch (InstantiationException e) {
                throw new RuntimeException(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage(), e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    private static Method getSetterMethod(Class<?> cls, String property, Class<?> valueCls) {
        String name = "set" + property.substring(0, 1).toUpperCase() + property.substring(1);
        Method method = NAME_METHODS_CACHE.get(cls.getName() + "." + name + "(" +valueCls.getName() + ")");
        if(method == null){
            try {
                method = cls.getMethod(name, valueCls);
            } catch (NoSuchMethodException e) {
                for (Method m : cls.getMethods()) {
                    if (isBeanPropertyWriteMethod(m)
                            && m.getName().equals(name)) {
                        method = m;
                    }
                }
            }
            if(method != null){
                NAME_METHODS_CACHE.put(cls.getName() + "." + name + "(" +valueCls.getName() + ")", method);
            }
        }
        return method;
    }

    private static Field getField(Class<?> cls, String fieldName) {
        Field result = null;
        if (CLASS_FIELD_CACHE.containsKey(cls)
                && CLASS_FIELD_CACHE.get(cls).containsKey(fieldName)) {
            return CLASS_FIELD_CACHE.get(cls).get(fieldName);
        }
        try {
            result = cls.getField(fieldName);
        } catch (NoSuchFieldException e) {
            for(Field field : cls.getFields()) {
                if (fieldName.equals(field.getName())
                        && isPublicInstanceField(field)) {
                    result = field;
                    break;
                }
            }
        }
        if (result != null) {
            ConcurrentMap<String, Field> fields = CLASS_FIELD_CACHE.get(cls);
            if (fields == null) {
                fields = new ConcurrentHashMap<String, Field>();
                CLASS_FIELD_CACHE.putIfAbsent(cls, fields);
            }
            fields = CLASS_FIELD_CACHE.get(cls);
            fields.putIfAbsent(fieldName, result);
        }
        return result;
    }

    public static boolean isPojo(Class<?> cls) {
        return ! isPrimitives(cls)
                && ! Collection.class.isAssignableFrom(cls)
                && ! Map.class.isAssignableFrom(cls);
    }

    
    //from ReflectUtils

    public static boolean isPrimitives(Class<?> cls) {
        if (cls.isArray()) {
            return isPrimitive(cls.getComponentType());
        }
        return isPrimitive(cls);
    }

    public static boolean isPrimitive(Class<?> cls) {
        return cls.isPrimitive() || cls == String.class || cls == Boolean.class || cls == Character.class
                || Number.class.isAssignableFrom(cls) || Date.class.isAssignableFrom(cls);
    }

    public static boolean isBeanPropertyReadMethod(Method method) {
        return method != null
                && Modifier.isPublic(method.getModifiers())
                && ! Modifier.isStatic(method.getModifiers())
                && method.getReturnType() != void.class
                && method.getDeclaringClass() != Object.class
                && method.getParameterTypes().length == 0
                && ((method.getName().startsWith("get") && method.getName().length() > 3)
                || (method.getName().startsWith("is") && method.getName().length() > 2));
    }

    public static String getPropertyNameFromBeanReadMethod(Method method) {
        if (isBeanPropertyReadMethod(method)) {
            if (method.getName().startsWith("get")) {
                return method.getName().substring(3, 4).toLowerCase()
                        + method.getName().substring(4);
            }
            if (method.getName().startsWith("is")) {
                return method.getName().substring(2, 3).toLowerCase()
                        + method.getName().substring(3);
            }
        }
        return null;
    }

    public static boolean isBeanPropertyWriteMethod(Method method) {
        return method != null
                && Modifier.isPublic(method.getModifiers())
                && !Modifier.isStatic(method.getModifiers())
                && method.getDeclaringClass() != Object.class
                && method.getParameterTypes().length == 1
                && method.getName().startsWith("set")
                && method.getName().length() > 3;
    }

    public static boolean isPublicInstanceField(Field field) {
        return Modifier.isPublic(field.getModifiers())
                && !Modifier.isStatic(field.getModifiers())
                && !Modifier.isFinal(field.getModifiers())
                && !field.isSynthetic();
    }
}