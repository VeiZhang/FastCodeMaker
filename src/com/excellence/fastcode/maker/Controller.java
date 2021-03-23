package com.excellence.fastcode.maker;


import com.excellence.fastcode.maker.events.FileDragEventHandler;
import com.excellence.fastcode.maker.utils.Alert;
import com.excellence.fastcode.maker.utils.JsonParser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controller implements Initializable {

    @FXML
    private TextField fastcodeFilePathTv;
    @FXML
    private Button openFastcodeFileBtn;

    @FXML
    public AnchorPane root;
    @FXML
    private TextField codeTv;
    @FXML
    private TextField serverNameTv;
    @FXML
    public TextField serverUrlTv;
    @FXML
    public TextField macTv;
    @FXML
    public TextField userNameTv;
    @FXML
    public TextField passwordTv;
    @FXML
    public Button itemAddBtn;
    @FXML
    public TextArea contentTv;
    @FXML
    public Button exportFileBtn;

    private Stage mPrimaryStage;
    private String mFileChooserInitPath = null;

    public void setStage(Stage primaryStage) {
        mPrimaryStage = primaryStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /**
         * 拖拽文件到编辑框，显示路径
         */
        root.setOnDragOver(new FileDragEventHandler("*.txt", "*.json"));
    }

    private void loadFastCodeFile() {
        String path = fastcodeFilePathTv.getText();
        try {
            String content = "";
            if (path.endsWith(".txt")) {

            } else {
                content = JsonParser.parse(path);
            }
            contentTv.setText(content);
        } catch (Exception exception) {
            fastcodeFilePathTv.setText(null);
            contentTv.setText(null);
            Alert.showErrorAlert(exception);
        }
    }

    public void dragDropEvent(DragEvent event) {
        fastcodeFilePathTv.setText(getDragDropFilePath(event));
        loadFastCodeFile();
    }

    private String getDragDropFilePath(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasFiles()) {
            mFileChooserInitPath = dragboard.getFiles().get(0).getParent();
            return dragboard.getFiles().get(0).getPath();
        }
        return null;
    }

    public void selectEvent(ActionEvent event) {
        File file = getChooserFile("Txt or Json file", "*.txt", "*.json");
        if (file != null) {
            fastcodeFilePathTv.setText(file.getPath());
            loadFastCodeFile();
        }
    }

    private File getChooserFile(final String description, final String... extensions) {
        FileChooser fileChooser = new FileChooser();
        if (mFileChooserInitPath != null) {
            File initFile = new File(mFileChooserInitPath);
            if (initFile.exists()) {
                fileChooser.setInitialDirectory(initFile);
            }
        }
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(description, extensions);
        fileChooser.getExtensionFilters().add(extensionFilter);
        File selectedFile = fileChooser.showOpenDialog(mPrimaryStage);
        if (selectedFile != null) {
            mFileChooserInitPath = selectedFile.getParent();
        }
        return selectedFile;
    }

}
