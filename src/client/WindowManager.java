package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class WindowManager {
    private int width;
    private int height;
    private String path;
    private String title;
    private boolean isModal;
    private boolean isHideOwner;
    private boolean isWaitOwner;

    public WindowManager(int width, int height, String path, String title, boolean isModal, boolean isHideOwner, boolean isWaitOwner) {
        this.width = width;
        this.height = height;
        this.path = path;
        this.title = title;
        this.isModal = isModal;
        this.isHideOwner = isHideOwner;
        this.isWaitOwner = isWaitOwner;
    }
    public FXMLLoader getFXMLLoader() {
        return new FXMLLoader(getClass().getResource(path));
    }

    public void openNewWindow(Node owner, Parent root) {
        Stage stage;
        if (isHideOwner) {
            stage = (Stage) owner.getScene().getWindow();
        } else {
            stage = new Stage();
            stage.initModality(isModal ? Modality.WINDOW_MODAL: Modality.NONE);
            stage.initOwner(owner.getScene().getWindow());
        }
        Scene scene = new Scene(root, width, height);
        stage.setTitle(title);
        stage.setResizable(false);
        stage.setScene(scene);
        if(isWaitOwner) {
            stage.showAndWait();
        } else {
            stage.show();
        }
    }


    static public void closeWindow(ActionEvent source) {
        Stage stage = (Stage) ((Node)source.getSource()).getScene().getWindow();
        stage.close();
    }
}
