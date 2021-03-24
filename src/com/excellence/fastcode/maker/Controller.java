package com.excellence.fastcode.maker;


import com.excellence.fastcode.maker.entity.FastCode;
import com.excellence.fastcode.maker.events.FileDragEventHandler;
import com.excellence.fastcode.maker.utils.AlertKit;
import com.excellence.fastcode.maker.utils.JsonParser;
import com.excellence.fastcode.maker.utils.TxtParser;

import java.io.File;
import java.net.URL;
import java.util.List;
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

import static com.excellence.fastcode.maker.utils.EmptyUtils.isEmpty;
import static com.excellence.fastcode.maker.utils.EmptyUtils.isNotEmpty;

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
    private final JsonParser mJsonParser = JsonParser.newInstance();
    private final TxtParser mTxtParser = TxtParser.newInstance();

    public void setStage(Stage primaryStage) {
        mPrimaryStage = primaryStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /**
         * 拖拽文件到编辑框，显示路径
         */
        root.setOnDragOver(new FileDragEventHandler("*.json", "*.txt"));
    }

    private void loadFastCodeFile() {
        String path = fastcodeFilePathTv.getText();
        try {
            String content = "";
            if (path.endsWith(".txt")) {
                content = mJsonParser.parse(mTxtParser.parse(new File(path)));
            } else {
                content = mJsonParser.parse(new File(path));
            }
            contentTv.setText(content);
        } catch (Exception exception) {
            fastcodeFilePathTv.setText(null);
            contentTv.setText(null);
            AlertKit.showErrorAlert("Parse file error", exception);
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

    public void selectFileEvent(ActionEvent event) {
        File file = getChooserFile("Json or Txt file", "*.json", "*.txt");
        if (file != null) {
            fastcodeFilePathTv.setText(file.getPath());
            loadFastCodeFile();
        }
    }

    private File getChooserFile(final String description, final String... extensions) {
        FileChooser fileChooser = newFileChooser();

        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(description, extensions);
        fileChooser.getExtensionFilters().add(extensionFilter);

        File selectedFile = fileChooser.showOpenDialog(mPrimaryStage);
        if (selectedFile != null) {
            mFileChooserInitPath = selectedFile.getParent();
        }
        return selectedFile;
    }

    private FileChooser newFileChooser() {
        FileChooser fileChooser = new FileChooser();
        if (mFileChooserInitPath != null) {
            File initFile = new File(mFileChooserInitPath);
            if (initFile.exists()) {
                fileChooser.setInitialDirectory(initFile);
            }
        }
        return fileChooser;
    }

    public void saveFileEvent(ActionEvent actionEvent) {
        FileChooser fileChooser = newFileChooser();
        fileChooser.setInitialFileName("maker_fastcode");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Json file", "*.json"),
                new FileChooser.ExtensionFilter("Txt file", "*.txt"));

        File savedFile = fileChooser.showSaveDialog(mPrimaryStage);
        String jsonContent = contentTv.getText();

        if (savedFile != null) {
            if (isEmpty(jsonContent)) {
                AlertKit.showErrorAlert("Content can't be empty");
            } else {
                mFileChooserInitPath = savedFile.getParent();
                saveFile(savedFile, jsonContent, fileChooser.getSelectedExtensionFilter().getExtensions());
            }
        }
    }

    private void saveFile(File savedFile, String jsonContent, List<String> extensions) {
        if (savedFile != null) {
            if (isNotEmpty(extensions)) {
                if (extensions.get(0).equals("*.txt")) {
                    TxtParser.saveFile(savedFile, mJsonParser.getFastCodeList());
                    return;
                }
            }

            JsonParser.saveFile(savedFile, jsonContent);
        }
    }

    public void addItemEvent(ActionEvent actionEvent) {
        String code = codeTv.getText();
        String serverURL = serverUrlTv.getText();
        String serverName = serverNameTv.getText();
        String mac = macTv.getText();
        String userName = userNameTv.getText();
        String password = passwordTv.getText();

        if (isEmpty(code)) {
            AlertKit.showErrorAlert("Code can't be empty");
            return;
        }

        selectContentCode(code);
        if (isEmpty(serverURL)) {
            AlertKit.showErrorAlert("Server URL can't be empty");
            return;
        }

        FastCode.Server server = new FastCode.Server();
        server.setServer_url(isEmpty(serverURL) ? null : serverURL);
        server.setServer_name(isEmpty(serverName) ? null : serverName);
        server.setServer_mac(isEmpty(mac) ? null : mac);
        server.setUser_name(isEmpty(userName) ? null : userName);
        server.setUser_password(isEmpty(password) ? null : password);

        if (mJsonParser.addItem(code, server)) {
            contentTv.setText(mJsonParser.parse());
        }

        selectContentCode(code);
    }

    private void selectContentCode(String code) {
        String content = contentTv.getText();
        String codeContent = String.format("\"code\":\"%s\"", code);
        int index = content.indexOf(codeContent);
        if (index >= 0) {
            contentTv.selectRange(index, index + codeContent.length());
        }
    }
}
