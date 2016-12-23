package cn.people;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

/**
 * Created by zhangxinzheng on 2016/12/21.
 */
public class GeneratorAction extends AnAction {
    private ClassModel classModel;
    private Editor editor;
    private String content;
    private boolean canCreate;
    private AnActionEvent event;
    private String path;
    @Override
    public void actionPerformed(AnActionEvent e) {
        this.event = e;
        canCreate = true;
        init(e);
        getClassModel();
        createFiles();
        try {
            if(canCreate) {
                createClassFiles();
                refreshProject(e);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    private void refreshProject(AnActionEvent e) {
        e.getProject().getBaseDir().refresh(false,true);
    }
    /**
     * 创建class文件
     * create class files
     * @throws IOException
     */
    private void createClassFiles() throws IOException {
        String className = classModel.getClassName();
        String moduleName = classModel.getModuleName();
        ClassCreateHelper.createService(path,className,moduleName);
        ClassCreateHelper.createDao(path,className,moduleName);
        ClassCreateHelper.createModel(path,className,moduleName);
    }

    private void createFiles() {
        if (null == classModel.getClassName()) {
            return;
        }
        path = ClassCreateHelper.getCurrentPath(event, classModel.getClassFullName());
        if(path.contains("generator")){
            path = path.replace("generator/","");
        }else {
            MessagesUtil.showErrorMessage("Your Generator should in package 'generator'.", "error");
            canCreate = false;
        }
    }

    private void getClassModel() {
        content = editor.getDocument().getText();
        String[] words = content.split(" ");
        for (String word : words) {
            if(word.contains("Generator")){
                String[] ss = word.split("(?=[A-Z])");
                String module = StringUtils.lowerCase(ss[0]);
                String className = ss[1];
                classModel.setClassName(className);
                classModel.setClassFullName(word);
                classModel.setModuleName(module);
                MessagesUtil.showDebugMessage(className, "class name");
            }
        }
        if (null == classModel.getClassName()) {
            MessagesUtil.showErrorMessage("Create failed ,Can't found 'Generator' in your class name,your class name must contain 'Generator'", "error");
            canCreate = false;
        }
    }
    private void init(AnActionEvent e) {
        editor = e.getData(PlatformDataKeys.EDITOR);
        classModel = new ClassModel();
    }
}
