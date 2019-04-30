package sample;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {
    @FXML
    private RadioButton radioButtonPrewittId;
    @FXML
    private RadioButton radioButtonSobelId;
    @FXML
    private RadioButton radioButtonKirschId;
    @FXML
    private ImageView imageViewLeftId;
    @FXML
    private ImageView imageViewRightId;
    @FXML
    private MenuItem menuBiomedicalFilterId;
    @FXML
    private RadioButton radioButtonLaplacianId;
    @FXML
    private MenuItem menuMedianFilterId;
    //window items
    @FXML
    private Menu menuFileId;
    @FXML
    private MenuBar menuBarId;
    @FXML
    private Menu menuEditId;
    @FXML
    private Menu menuPunctualFilterId;
    @FXML
    private MenuItem menuAddContrastId;
    @FXML
    private Menu menuHelpId;
    @FXML
    private MenuItem menuItemLoad;
    @FXML
    private MenuItem menuItemClose;
    @FXML
    private ToggleGroup radiobuttonsGroup;

    //controller elements
    private Stage stage;
    private BufferedImage bufferedImage;
    private PunctualFilters punctualFilters;
    private SpacialFilters spacialFilters;
    private BufferedImage imageToSave;
    private Boolean imageModified;
    private Boolean imageLoaded;

    @FXML
    public void initialize() {
         this.punctualFilters = new PunctualFilters();
         this.spacialFilters = new SpacialFilters();
         this.imageModified = false;
         this.imageLoaded = false;

         radiobuttonsGroup = new ToggleGroup();
         radioButtonLaplacianId.setToggleGroup(radiobuttonsGroup);
         radioButtonSobelId.setToggleGroup(radiobuttonsGroup);
         radioButtonKirschId.setToggleGroup(radiobuttonsGroup);
         radioButtonPrewittId.setToggleGroup(radiobuttonsGroup);
         radioButtonLaplacianId.setSelected(true);
    }

    private Optional<ButtonType> displayConfirmationMessage(String filterType)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm operation");
        alert.setContentText("Do you want to "+ filterType+" to the image?");
        return alert.showAndWait();
    }

    private void displayWarningMessage(String alertTitle,String contentText) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(alertTitle);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private void displayErrorMessage(String operationType){
        Alert innerAlert = new Alert(Alert.AlertType.ERROR);
        innerAlert.setTitle("Operation failed");
        innerAlert.setContentText("Cannot perform operation " + operationType);
    }

    public void setImageViewRight(BufferedImage modifiedImage) {
        WritableImage image = SwingFXUtils.toFXImage(modifiedImage, null);
        imageViewRightId.setImage(image);
        imageToSave = modifiedImage;
        imageModified = true;
    }

    public void menuItemAddContrast() {
        if(imageLoaded){
            Optional<ButtonType> selected = displayConfirmationMessage("add contrast");
            if (selected.isPresent()) {
                if (selected.get() == ButtonType.OK) {
                    BufferedImage modifiedImage = punctualFilters.addContrast(bufferedImage);
                    if(modifiedImage != null) {
                        setImageViewRight(modifiedImage);
                        System.out.println("AddContrast:OK");
                    }
                    else {
                        displayErrorMessage("Add contrast");
                        System.out.println("AddContrast:Failed");
                    }
                } else {
                    System.out.println("Cancel");
                }
            }
        }else{
            displayWarningMessage("Invalid operation","No image to apply filter on");
        }
    }

    public void menuItemClose() {
        Platform.exit();
    }


    public void setStage(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void menuItemLoad(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg")
        );
        File file = fileChooser.showOpenDialog(stage);
        if(file != null)
        {
            try {
                this.bufferedImage = ImageIO.read(file);
                if(this.bufferedImage != null) {
                    WritableImage image = SwingFXUtils.toFXImage(bufferedImage, null);
                    imageViewLeftId.setImage(image);
                    imageLoaded = true;
                    System.out.println("Image read");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            displayWarningMessage("Empty selection","No file selected");
            System.out.println("No file selected");
        }
    }

    public void menuItemMedianFilter(){
        if(imageLoaded) {
            Optional<ButtonType> selected = displayConfirmationMessage("median filter");
            if (selected.isPresent()) {
                if (selected.get() == ButtonType.OK) {
                    BufferedImage modifiedImage = spacialFilters.medianFilter(5,bufferedImage);
                    if(modifiedImage != null) {
                        setImageViewRight(modifiedImage);
                        imageToSave = modifiedImage;
                        imageModified = true;
                        System.out.println("MedianFilter:OK");
                    }
                    else {
                        displayErrorMessage("Median Filter");
                        System.out.println("MedianFilter:Failed");
                    }
                } else {
                    System.out.println("Cancel");
                }
            }
        }
        else {
            displayWarningMessage("Invalid operation","No image to apply filter on");
        }
    }

    public void menuItemBiomedicalFilter(){
        if(imageLoaded) {
            Optional<ButtonType> selected = displayConfirmationMessage("biomedical improvement");
            if (selected.isPresent()) {
                if (selected.get() == ButtonType.OK) {
                    ArrayList<Integer> laplacian = new ArrayList(Arrays.asList(-1,-1,-1,-1,9,-1,-1,-1,-1));
                    ArrayList<Integer> prewittHorizontal = new ArrayList(Arrays.asList(-1,-1,-1,0,0,0,-1,-1,-1));
                    ArrayList<Integer> sobelHorizontal = new ArrayList(Arrays.asList(1,2,1,0,0,0,-1,-2,-1));
                    ArrayList<Integer> krschHorizontal = new ArrayList(Arrays.asList(-3,-3,-5,-3,0,-5,-3,-3,-5));

                    BufferedImage modifiedImage = null;
                    if(radioButtonLaplacianId.isSelected()) {
                         modifiedImage = spacialFilters.biomedicalImprovement(laplacian, bufferedImage);
                    }
                    if(radioButtonPrewittId.isSelected()) {
                        modifiedImage = spacialFilters.biomedicalImprovement(prewittHorizontal, bufferedImage);
                    }
                    if(radioButtonSobelId.isSelected()) {
                        modifiedImage = spacialFilters.biomedicalImprovement(sobelHorizontal, bufferedImage);
                    }
                    if(radioButtonKirschId.isSelected()) {
                        modifiedImage = spacialFilters.biomedicalImprovement(krschHorizontal, bufferedImage);
                    }

                    if(modifiedImage != null) {
                        setImageViewRight(modifiedImage);
                        imageToSave = modifiedImage;
                        imageModified = true;
                        System.out.println("BiomedicalImprovement:OK");
                    }
                    else {
                        displayErrorMessage("Biomedical Improvement");
                        System.out.println("BiomedicalImprovement:Failed");
                    }
                } else {
                    System.out.println("Cancel");
                }
            }
        }
        else {
            displayWarningMessage("Invalid operation","No image to apply filter on");
        }
    }


    public void menuItemSave() {
        if(imageModified){
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Export to");
            File defaultDirectory = new File(".");
            directoryChooser.setInitialDirectory(defaultDirectory);
            File selectedDirectory = directoryChooser.showDialog(stage);
            if(selectedDirectory != null)
            {
                String path = selectedDirectory.getAbsolutePath() + "/result";
                try
                {
                    File file = new File(path);
                    ImageIO.write(imageToSave, "jpg", file);
                    imageModified = false;
                    System.out.println("file exported");
                }
                catch(IOException e)
                {
                    System.out.println(e);
                }
                System.out.println("dir for export selected");
            }else{
                System.out.println("no dir for export selected");
            }
        }else{
            displayWarningMessage("Invalid operation","Failed to save image");
            System.out.println("SaveToFile:Failed");
        }
    }


}
