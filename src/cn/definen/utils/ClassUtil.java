package cn.definen.utils;

import cn.definen.model.ClassInfo;
import cn.definen.model.FieldInfo;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * 反射, 加载目录下的 class
 */
public class ClassUtil {

    /**
     *  将list 的class 转为 ClassInfo
     * @param classes clz
     * @param function 函数, 如果是 null 返回true, 留下
     * @return list
     */
    public static List<ClassInfo> convertClassInfo(List<Class<?>> classes, Function<Class<?>, Boolean> function) {
        List<ClassInfo> classInfos = new ArrayList<>();

        for (Class<?> clz : classes) {

            // 初步 filter
            if (function != null && !function.apply(clz)) {
                continue;
            }

            ClassInfo info = convertClassInfo(clz);

            // 他的 Bean属性必须大于 0 个
            if (info.getFields().size() > 0) {
                classInfos.add(info);
            }
        }

        return classInfos;
    }

    /**
     * 将一个 class 转为 <see>cn.definen.model.ClassInfo</see>
     * @param clz class
     * @return ClassInfo
     */
    private static ClassInfo convertClassInfo(Class<?> clz) {
        List<PropertyDescriptor> fields = ClassUtil.getBeanProperty(clz);
        ClassInfo info = new ClassInfo();

        info.setPackageName(clz.getPackage().getName());
        info.setClassName(clz.getSimpleName());

        List<FieldInfo> fieldInfos = new ArrayList<>();
        for (PropertyDescriptor field : fields) {
            FieldInfo fieldInfo = new FieldInfo();

            fieldInfo.setName(field.getName());
            fieldInfo.setType(field.getPropertyType().toString());
            fieldInfo.setOrigin(field);
            calcType(fieldInfo, info);

            fieldInfos.add(fieldInfo);
        }
        info.setFields(fieldInfos);

        return info;
    }

    /**
     * 计算类型, 通过类型, 计算demo等属性
     * @param info fieldInfo
     * @param classInfo classInfo
     */
    private static void calcType(FieldInfo info, ClassInfo classInfo) {
        PropertyDescriptor origin = info.getOrigin();
        Class<?> type = origin.getPropertyType();

        if (type.isAssignableFrom(int.class) || type.isAssignableFrom(Integer.class)
                || type.isAssignableFrom(long.class) || type.isAssignableFrom(Long.class)) {
            info.setDemo("1");
        } else if (type.isAssignableFrom(double.class) || type.isAssignableFrom(Double.class)) {
            info.setDemo("1.1");
        } else if (type.isAssignableFrom(float.class) || type.isAssignableFrom(Float.class)) {
            info.setDemo("1.1F");
        } else if (type.isAssignableFrom(String.class)) {
            info.setDemo("\"1\"");
        } else if (type.isAssignableFrom(Date.class)) {
            classInfo.addImport("import java.util.Date;");
            info.setPatchInfo("Date date = new Date();");
            info.setDemo("date");
        } else {
            info.setDemo("unknown type: " + type);
        }
    }

    /**
     * 获取一个类所有符合 bean 规范的属性
     * @param clz class
     * @return list
     */
    public static List<PropertyDescriptor> getBeanProperty(Class<?> clz) {
        List<PropertyDescriptor> ans = new ArrayList<>();

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(clz);
            PropertyDescriptor[] fields = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor field : fields) {
                Method getter = field.getReadMethod();
                Method setter = field.getWriteMethod();
                if (getter != null && setter != null) {
                    ans.add(field);
                }
            }

        } catch (Throwable ignored) {

        }

        return ans;
    }

    /**
     * 递归加载所有的类
     * @param path 目录
     * @return list
     */
    public static List<Class<?>> getDirClazzs(String path) {
        List<Class<?>> ans = new ArrayList<>();
        List<File> files = new ArrayList<>();

        // 一个路径, 需要一个urls
        List<URL> urls = new ArrayList<>();
        getAllClassFile(new File(path), files, urls);

        // 构造 loader
        URLClassLoader loader = new URLClassLoader(urls.toArray(new URL[0]));

        // 循环加载类
        for (File file : files) {
            String name = getClassName(path.length() + 1, file);
            try {
                ans.add(loader.loadClass(name));
            } catch (Throwable e) {
                //有些类继承了Spring等, 加载这个类需要加载父类(Spring), 但是没找到父类, 所以抛出异常, 把这个异常捕获掉即可
            }
        }

        return ans;
    }

    private static String getClassName(int rootPathLength, File file) {
        String name = file.getAbsolutePath()
                .substring(rootPathLength);

        return name.replace(".class", "")
                .replaceAll("\\\\", ".");
    }

    private static void getAllClassFile(File file, List<File> files, List<URL> urls) {
        if (file == null) return;
        if (file.isDirectory()) {
            // 如果是一个路径, name添加到urls里
            try {
                urls.add(file.toURI().toURL());
            } catch (MalformedURLException e) {
                System.err.println("[error] 文件夹在转换成 URL 对象时报错");
                e.printStackTrace();
            }

            File[] listFiles = file.listFiles();
            if (listFiles == null) return;
            for (File f : listFiles) {
                getAllClassFile(f, files, urls);
            }
        } else {
            if (file.getName().endsWith(".class")) {
                files.add(file);
            }
        }
    }
}
