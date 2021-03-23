package com.excellence.fastcode.maker.events;

import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

/**
 * <pre>
 *     author : VeiZhang
 *     blog   : http://tiimor.cn
 *     time   : 2018/5/14
 *     desc   :
 * </pre>
 */

public class FileDragEventHandler implements EventHandler<DragEvent> {

    private List<String> mExtension = null;

    public FileDragEventHandler(String... extensions) {
        mExtension = new ArrayList<String>();
        if (extensions != null) {
            for (String extension : extensions) {
                if (extension != null) {
                    mExtension.add(extension.replace("*", ""));
                }
            }
        }
    }

    @Override
    public void handle(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasFiles()) {
            for (String extension : mExtension) {
                if (dragboard.getFiles().get(0).getPath().endsWith(extension)) {
                    /**
                     * 显示文件拖拽的状态，否则图标是禁止的
                     */
                    event.acceptTransferModes(TransferMode.ANY);
                }
            }
        }
    }
}
