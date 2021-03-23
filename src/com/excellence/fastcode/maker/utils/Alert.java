package com.excellence.fastcode.maker.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * <pre>
 *     author : VeiZhang
 *     blog   : http://tiimor.cn
 *     time   : 2021/3/23
 *     desc   :
 * </pre> 
 */
public class Alert {

    public static void showErrorAlert(Throwable e) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR, "", new ButtonType("确定", ButtonBar.ButtonData.YES));
        alert.setTitle("Warning");
        alert.setHeaderText("Parse file error");
        alert.setContentText(e.getMessage());

        /**************详细信息**************/
        Label label = new Label("Exception：");
        TextArea textArea = new TextArea(printException(e));
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // 设置可隐藏的窗口
        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);
        /**************详细信息**************/

        alert.showAndWait();
    }

    private static String printException(Throwable e) {
        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
