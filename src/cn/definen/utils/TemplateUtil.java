package cn.definen.utils;

import cn.definen.model.ClassInfo;
import cn.definen.model.FieldInfo;
import com.intellij.util.io.BaseInputStreamReader;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.bouncycastle.util.encoders.UTF8;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class TemplateUtil {

    public static void generateCode(String basePath, String templateFile, String addTemplateFile, List<ClassInfo> infos) {
        for (ClassInfo info : infos) {
            String outPath = basePath
                    + "/src/test/java/"
                    + info.getPackageName().replaceAll("\\.", "/");

            String outFile = "/" + info.getClassName() + "Test.java";

            ContentUtil.checkPath(outPath);
            File file = new File(outPath + outFile);

            if (file.exists()) {
                System.out.println("[warn] 文件已存在");

                // 更新 classInfo 的属性信息
                List<FieldInfo> fieldInfos = ContentUtil.checkFile(file, info);
                if (fieldInfos.size() == 0) {
                    System.out.println("检测出完整的测试文件, 不需要改动");
                    continue;
                }
                System.out.println("文件需要改动");

                // 插入文件
                String content = "有新的方法需要生成, 但是失败了";
                try (StringWriter writer = new StringWriter()) {
                    generate2Writer(basePath, addTemplateFile, writer, info);
                    content = writer.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // 插入文本
                try {
                    ContentUtil.insertContent(content, file);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                try (FileWriter writer = new FileWriter(file)) {
                    generate2Writer(basePath, templateFile, writer, info);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 生成文件
     * @param basePath 模板基本地址
     * @param templateName 模板文件名
     * @param writer 输出文件
     * @param dataModel 数据
     */
    private static void generate2Writer(String basePath, String templateName, Writer writer, Object dataModel) {
        Configuration conf = new Configuration();
        try {

            File templateFile = new File(basePath + '/' + templateName);
            if (templateFile.exists()) {
                conf.setDirectoryForTemplateLoading(new File(basePath));
                conf.setObjectWrapper(new DefaultObjectWrapper());
                conf.setDefaultEncoding("UTF-8");
            } else {
                // 模板不存在, 使用默认模板
                InputStream stream = TemplateUtil.class.getClassLoader().getResourceAsStream("./" + templateName);
                assert stream != null;
                String result = IOUtils.toString(stream, StandardCharsets.UTF_8);

                StringTemplateLoader loader = new StringTemplateLoader();
                loader.putTemplate(templateName, result);
                System.out.println("使用默认模板");
                conf.setTemplateLoader(loader);
            }

            Template template = conf.getTemplate(templateName);
            template.process(dataModel, writer);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }
}