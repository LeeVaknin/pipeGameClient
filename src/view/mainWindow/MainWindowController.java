package view.mainWindow;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.ThemeManagerService;
import utils.DIHelper;
import services.FilesLoaderService;
import viewModels.MainWindowViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import view.pipeGame.BoardDisplayer;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Observer;
import java.util.ResourceBundle;


public class MainWindowController implements Observer, Initializable {

    // Variables
    private MainWindowViewModel vm;
    private ThemeManagerService themeManager;
    private FilesLoaderService filesLoaderService;
    private StringProperty theme;

    // C-TORs and overrides
    public MainWindowController(ThemeManagerService themeManagerService, MainWindowViewModel mainWindowViewModel) {

        // Set the themeManager service and bind the theme type to it
        this.themeManager = themeManagerService;
        this.themeManager.addObserver(this);

        // Set the vm and bind the logged in user to the vm
        this.vm = mainWindowViewModel;
        this.vm.addObserver(this);

        this.filesLoaderService = new FilesLoaderService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            URL resource = getClass().getClassLoader().getResource("view/pipeGame/PipeGame.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(resource);
            DIHelper.injectServiceAndVM(fxmlLoader, "PipeGameViewModel", new Object[] { themeManager, this.filesLoaderService});
            GridPane gameBoard = fxmlLoader.load();
            rootBorder.centerProperty().setValue(gameBoard);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | IOException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update (java.util.Observable o, Object arg){
        if (o == this.themeManager) {
            theme = new SimpleStringProperty();
            theme.bindBidirectional(this.themeManager.themeName);
            themeToggle.textProperty().bindBidirectional(theme);

        }
        if (o == this.vm){
            this.vm.userName.bindBidirectional(userName.textProperty());
        }

    }

    // FXMl variables
    @FXML protected TextField userName;
    @FXML protected Button themeToggle;
    @FXML protected  BorderPane rootBorder;

    @FXML protected void handleToggleAction(ActionEvent t) {
        if (theme.getValue().equals("Dark")) {
            theme.setValue("Bright");
        }
        else {
            theme.setValue("Dark");
        }
    }

    @FXML
    private void showSettings() {
        this.loadSettingsWindow();
    }

    @FXML
    private void loadLevel() {
        this.filesLoaderService.isGameLoadRequested.setValue(true);
    }

    @FXML
    private void saveLevel() {
        this.filesLoaderService.isGameSaveRequested.setValue(true);
    }

    @FXML
    private void showTimer() {
    }

    @FXML
    private void showStepper() {
    }

    private void loadSettingsWindow() {
        URL resource = getClass().getClassLoader().getResource("view/settings/Settings.fxml");

        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        try {
            DIHelper.injectServiceAndVM(fxmlLoader, "SettingsViewModel", new Object[] {});
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.toFront();

            stage.show();

        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | InstantiationException | IOException e) {
            e.printStackTrace();
        }


    }

}

