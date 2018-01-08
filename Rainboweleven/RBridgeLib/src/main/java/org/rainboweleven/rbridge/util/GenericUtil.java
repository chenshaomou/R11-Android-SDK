package org.rainboweleven.rbridge.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author andy(Andy)
 * @datetime 2017-12-23 12:02 GMT+8
 * @email 411086563@qq.com
 */
public class GenericUtil {

    /**
     * 获取一个对象泛型类型
     *
     * @param genericObj 含有泛型的对象
     * @param position   泛型的位置
     * @return
     */
    public static Type getGenericParameterType(Object genericObj, String fieldName, int position) {
        Type type = getGenericClassParameterType(genericObj.getClass(), fieldName, position);
        if (type == null) {
            type = getGenericClassParameterType(genericObj.getClass().getSuperclass(), fieldName, position);
        }
        return type;
    }

    /**
     * 获取一个对象泛型参数类型
     *
     * @param clazz     含有泛型参数的对象Class
     * @param fieldName 字段名字
     * @param position  泛型参数的位置，从0开始
     * @return
     */
    private static Type getGenericClassParameterType(Class clazz, String fieldName, int position) {
        try {
            return ((ParameterizedType) clazz.getDeclaredField(fieldName).getGenericType()).getActualTypeArguments()
                    [position];
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取一个对象泛型类型
     *
     * @param genericObj 含有泛型的对象
     * @param position   泛型的位置
     * @return
     */
    private static Type getGenericParameterType(Object genericObj, int position) {
        Type type = getGenericInterfacesParameterType(genericObj.getClass(), position);
        if (type == null) {
            type = getGenericClassParameterType(genericObj.getClass(), position);
        }
        if (type == null) {
            type = getGenericClassParameterType(genericObj.getClass().getSuperclass(), position);
        }
        return type;
    }

    /**
     * 获取一个对象泛型参数类型
     *
     * @param clazz    含有泛型参数的对象Class
     * @param position 泛型参数的位置，从0开始
     * @return
     */
    private static Type getGenericClassParameterType(Class clazz, int position) {
        try {
            return ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[position];
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取一个接口泛型参数类型
     *
     * @param clazz    含有泛型参数的接口Class
     * @param position 泛型参数的位置，从0开始
     * @return
     */
    private static Type getGenericInterfacesParameterType(Class clazz, int position) {
        try {
            return ((ParameterizedType) clazz.getGenericInterfaces()[0]).getActualTypeArguments()[position];
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return null;
    }
}
