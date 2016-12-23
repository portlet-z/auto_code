package cn.people;

import com.intellij.openapi.ui.Messages;

/**
 * Created by zhangxinzheng on 2016/12/22.
 */
public class MessagesUtil {
    public static void showErrorMessage(String context,String title){
        Messages.showMessageDialog(context,title,Messages.getErrorIcon());
    }
    public static void showMessage(String context,String title){
        Messages.showMessageDialog(context,title,Messages.getInformationIcon());
    }

    public static void showDebugMessage(String context,String title){
        if(false) {
            Messages.showMessageDialog(context, title, Messages.getErrorIcon());
        }
    }
}
