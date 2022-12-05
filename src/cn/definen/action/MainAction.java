package cn.definen.action;

import cn.definen.model.ClassInfo;
import cn.definen.utils.ClassUtil;
import cn.definen.utils.TemplateUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.logging.Logger;

public class MainAction extends AnAction {

    private static final Logger logger = Logger.getLogger(MainAction.class.getSimpleName());

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        // 当前项目路径
        String currentProjectBasePath = e.getProject().getBasePath();
        logger.info("当前项目路径: " + currentProjectBasePath);

        //获得类精准的位置
        VirtualFile file = DataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        String currentPath = file.getPath();
        logger.info("当前详细位置: " + currentPath);

        currentPath = currentPath.replace("/src/main/java", "/target/classes");

        // 得到目录下所有能加载的 class
        List<Class<?>> clazzs = ClassUtil.getDirClazzs(currentProjectBasePath + "/target/classes", currentPath);

        logger.info(clazzs.toString());

        // 转出所有自定义的 ClassInfo, 方便模板解析
        List<ClassInfo> infos = ClassUtil.convertClassInfo(
                clazzs,
                // 如果 类名 存在 $, !返回 false, 筛掉
                (clz) -> !clz.getSimpleName().contains("$")
        );

        System.out.println(infos);

        TemplateUtil.generateCode(currentProjectBasePath, "template.ftl", "add.ftl", infos);
    }
}
