package cn.people;

import org.apache.commons.lang.WordUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by zhangxinzheng on 2016/12/22.
 */
public class ClassCreateHelper {

    public static void createDao(String dir,String packageName,String ClassName,String moduleName,Map<String,Object> map) throws IOException {
        String fileDir = dir + "/" + packageName.replace(".","/") + "/modules/" + moduleName + "/dao/";
        String filePath = fileDir + ClassName + "Dao.java";
        createFile(fileDir,filePath,"code/dao.vm",map);
    }
    public static void createService(String dir,String packageName,String ClassName,String moduleName,Map<String,Object> map) throws IOException {
        String fileDir = dir + "/" + packageName.replace(".","/") + "/modules/" + moduleName + "/service/";
        String filePath = fileDir + "I" + ClassName + "Service.java";
        createFile(fileDir,filePath,"code/service.vm",map);
    }
    public static void createServiceImpl(String dir,String packageName,String ClassName,String moduleName,Map<String,Object> map) throws IOException {
        String fileDir = dir + "/" + packageName.replace(".","/") + "/modules/" + moduleName + "/service/impl/";
        String filePath = fileDir + ClassName + "Service.java";
        createFile(fileDir,filePath,"code/serviceImpl.vm",map);
    }
    public static void createModel(String dir,String packageName,String ClassName,String moduleName,Map<String,Object> map) throws IOException {
        String fileDir = dir + "/" + packageName.replace(".","/") + "/modules/" + moduleName + "/model/";
        String filePath = fileDir + ClassName + ".java";
        createFile(fileDir,filePath,"code/model.vm",map);
    }
    public static void createController(String dir,String packageName,String ClassName,String moduleName,Map<String,Object> map) throws IOException{
        String fileDir = dir + "/" + packageName.replace(".","/") + "/modules/" + moduleName + "/web/";
        String filePath = fileDir + ClassName + "Controller.java";
        createFile(fileDir,filePath,"code/controller.vm",map);
    }

    private static void createFile(String dir,String path,String vmPath,Map<String,Object> map) throws IOException{
        File dirs = new File(dir);
        File file = new File(path);
        if(!dirs.exists()){
            dirs.mkdir();
        }
        file.createNewFile();
        VelocityContext context = new VelocityContext();
        for(String key : map.keySet()){
            context.put(key,map.get(key));
            if(key.equals("ClassName")){
                context.put("tableName",CamelhumpToUnderline(map.get(key).toString()));
                context.put("className", WordUtils.uncapitalize(map.get(key).toString()));
                context.put("controllerPath",CamelhumpToPath(map.get(key).toString()));
            }
        }
        StringWriter str = new StringWriter();
        InputStream inputStream = ClassCreateHelper.class.getClassLoader().getResourceAsStream(vmPath);
        String template = new BufferedReader(new InputStreamReader(inputStream,"UTF-8")).lines().parallel().collect(Collectors.joining("\n"));
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty("runtime.references.strict", false);
        engine.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
        engine.init();
        engine.evaluate(context, str, "generator", template);
        Writer w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
        BufferedWriter writer = new BufferedWriter(w);
        writer.write(str.toString());
        writer.flush();
        writer.close();
    }

    // 将驼峰风格替换为下划线风格
    private static String CamelhumpToUnderline(String str) {
        Matcher matcher = Pattern.compile("[A-Z]").matcher(str);
        StringBuilder builder = new StringBuilder(str);
        for (int i = 0; matcher.find(); i++) {
            builder.replace(matcher.start() + i, matcher.end() + i, "_" + matcher.group().toLowerCase());
        }
        if (builder.charAt(0) == '_') {
            builder.deleteCharAt(0);
        }
        return builder.toString();
    }

    private static String CamelhumpToPath(String str) {
        Matcher matcher = Pattern.compile("[A-Z]").matcher(str);
        StringBuilder builder = new StringBuilder(str);
        for (int i = 0; matcher.find(); i++) {
            builder.replace(matcher.start() + i, matcher.end() + i, "/" + matcher.group().toLowerCase());
        }
        if (builder.charAt(0) == '/') {
            builder.deleteCharAt(0);
        }
        return builder.toString();
    }
}
