package cn.people;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;
import java.util.stream.Collectors;

/**
 * Created by zhangxinzheng on 2016/12/22.
 */
public class ClassCreateHelper {
    public static void createService(String path, String ClassName, String moduleName) throws IOException {
        String dir = path +"modules/"+moduleName+"/service/";
        path = dir + ClassName+"Service.java";
        createFile(dir,path,ClassName,moduleName,"code/service.vm");
    }

    public static void createModel(String path, String ClassName, String moduleName) throws IOException {
        String dir = path +"modules/"+moduleName+"/model/";
        path = dir + ClassName+".java";
        createFile(dir,path,ClassName,moduleName,"code/model.vm");
    }

    public static void createDao(String path, String ClassName, String moduleName) throws IOException {
        String dir = path +"modules/"+moduleName+"/dao/";
        path = dir + ClassName+"Dao.java";
        createFile(dir,path,ClassName,moduleName,"code/dao.vm");
    }

    private static void createFile(String dir,String path, String ClassName, String moduleName,String vmPath) throws IOException{
        File dirs = new File(dir);
        File file = new File(path);
        if(!dirs.exists()){
            dirs.mkdir();
        }
        file.createNewFile();
        VelocityContext context = new VelocityContext();
        context.put("ClassName", ClassName);
        context.put("className",StringUtils.lowerCase(ClassName));
        String packagePath = getPackageName(path);
        context.put("packageName", packagePath.substring(0,packagePath.length()-1));
        context.put("moduleName",moduleName);
        StringWriter str = new StringWriter();
        InputStream inputStream = ClassCreateHelper.class.getClassLoader().getResourceAsStream(vmPath);
        String template = new BufferedReader(new InputStreamReader(inputStream)).lines().parallel().collect(Collectors.joining("\n"));
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty("runtime.references.strict", false);
        engine.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
        engine.init();
        engine.evaluate(context, str, "generator", template);
        Writer w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
        BufferedWriter writer = new BufferedWriter(w);
        writer.write(str.toString());
        writer.flush();
    }
    private static String getPackageName(String path) {
        String[] strings = path.split("/");
        StringBuilder packageName = new StringBuilder();
        int index = 0;
        int length = strings.length;
        for(int i = 0;i<strings.length;i++){
            if(strings[i].equals("com") || strings[i].equals("org") || strings[i].equals("cn")){
                index = i;
                break;
            }
        }
        for(int j = index;j<length-1;j++){
            packageName.append(strings[j]+".");
        }
        return packageName.toString();
    }

    public static String getCurrentPath(AnActionEvent e, String classFullName){
        VirtualFile currentFile = DataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        String path = currentFile.getPath().replace(classFullName+".java","");
        return path;
    }

}
