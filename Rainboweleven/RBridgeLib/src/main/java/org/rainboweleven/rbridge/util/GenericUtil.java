package org.rainboweleven.rbridge.util;

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
    public static Type getGenericParameterType(Object genericObj, int position) {
        if(genericObj == null){
            return null;
        }
        Type type = null;
        Type[] types = genericObj.getClass().getGenericInterfaces();
        if (types != null && types.length > 0) {
            type = types[position];
        }
        if (type == null) {
            type = genericObj.getClass().getGenericSuperclass();
        }
        /**
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return $Gson$Types.canonicalize(parameterizedType.getActualTypeArguments()[position]);
        } else {
            return null;
        }
         */
        return type;
    }
}
