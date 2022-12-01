package cn.definen.utils;

import cn.definen.model.ClassInfo;

import freemarker.core.ParseException;
import freemarker.template.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TemplateUtil {

    public static void generateCode(String basePath, String templateFile, List<ClassInfo> infos) {
        for (ClassInfo info : infos) {
            String outPath = basePath
                    + "/src/test/java/"
                    + info.getPackageName().replaceAll("\\.", "/");

            String outFile = "/" + info.getClassName() + "Test.java";

            File file = new File(outPath + outFile);

            if (file.exists()) {
                System.out.println("[warn] 文件已存在");
            } else {
                generateFile(basePath, templateFile, file, info);
            }
        }
    }

    /**
     * 生成文件
     * @param basePath 模板基本地址
     * @param templateFile 模板文件名
     * @param outFile 输出文件
     * @param dataModel 数据
     */
    private static void generateFile(String basePath, String templateFile, File outFile, Object dataModel) {
        Configuration conf = new Configuration();
        FileWriter out;
        try {
            conf.setDirectoryForTemplateLoading(new File(basePath));
            conf.setObjectWrapper(new DefaultObjectWrapper());
            conf.setDefaultEncoding("UTF-8");

            Template template = conf.getTemplate(templateFile);

            out = new FileWriter(outFile);
            template.process(dataModel, out);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }
}