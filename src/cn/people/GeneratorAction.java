package cn.people;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by zhangxinzheng on 2016/12/21.
 */
public class GeneratorAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent event) {
        try {
            createFiles(event);
            refreshProject(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void refreshProject(AnActionEvent e) {
        e.getProject().getBaseDir().refresh(false,true);
    }

    private void createFiles(AnActionEvent event) throws IOException{
        VirtualFile currentFile = DataKeys.VIRTUAL_FILE.getData(event.getDataContext());
        String path = currentFile.getPath();
        if(!path.endsWith("resources/generator.yml")){
            Messages.showMessageDialog("只能读取配置文件下的generator.yml文件","error",Messages.getErrorIcon());
            return;
        }
        String dir = path.replace("resources/generator.yml","java");
        InputStream inputStream = new FileInputStream(path);
        Yaml yaml = new Yaml();
        Map<String,Object> map = (Map<String, Object>)yaml.load(inputStream);
        if(null == map){
            Messages.showMessageDialog("配置文件读取错误","error",Messages.getErrorIcon());
            return;
        }
        if(null == map.get("packageName") || StringUtils.isBlank(map.get("packageName").toString())){
            Messages.showMessageDialog("packageName为空不能创建文件路径","error",Messages.getErrorIcon());
            return;
        }
        if(null == map.get("ClassName") || StringUtils.isBlank(map.get("ClassName").toString())){
            Messages.showMessageDialog("ClassName不能为空","error",Messages.getErrorIcon());
            return;
        }
        if(null == map.get("className") || StringUtils.isBlank(map.get("className").toString())){
            Messages.showMessageDialog("className不能为空","error",Messages.getErrorIcon());
        }
        if(null == map.get("moduleName") || StringUtils.isBlank(map.get("moduleName").toString())){
            Messages.showMessageDialog("moduleName不能为空","error",Messages.getErrorIcon());
            return;
        }
        String packageName = map.get("packageName").toString();
        String ClassName = map.get("ClassName").toString();
        String moduleName = map.get("moduleName").toString();
        ClassCreateHelper.createDao(dir,packageName,ClassName,moduleName,map);
        ClassCreateHelper.createService(dir,packageName,ClassName,moduleName,map);
        ClassCreateHelper.createServiceImpl(dir,packageName,ClassName,moduleName,map);
        ClassCreateHelper.createModel(dir,packageName,ClassName,moduleName,map);
    }
}
