package cn.definen.action;

import cn.definen.model.ClassInfo;
import cn.definen.utils.ClassUtil;
import cn.definen.utils.TemplateUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyDescriptor;
import java.util.List;

public class MainAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String currentProjectBasePath = e.getProject().getBasePath();

        // 得到目录下所有能加载的 class
        List<Class<?>> clazzs = ClassUtil.getDirClazzs(currentProjectBasePath + "/target/classes");

        System.out.println(clazzs);

        // 转出所有自定义的 ClassInfo, 方便模板解析
        List<ClassInfo> infos = ClassUtil.convertClassInfo(
                clazzs,
                // 如果 类名 存在 $, !返回 false, 筛掉
                (clz) -> !clz.getSimpleName().contains("$")
        );

        System.out.println(infos);

        TemplateUtil.generateCode(currentProjectBasePath, "template.ftl", infos);
    }
}
