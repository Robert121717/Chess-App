
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private Stage stage;
    private PopupControl newGamePopup;
    private PopupControl appearancePopup;

    @FXML
    private Circle avatar;
    @FXML
    private Label level;
    @FXML
    private ToggleButton newGameButton;
    @FXML
    private ToggleButton appearanceButton;
    @FXML
    private GridPane chessBoard;
    private CheckerBoard checkerBoard;
    private String userColor = "white";
    private boolean interactiveBoard = false;

    private Game game;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeAvatar();
        initializeStartMenu();

        checkerBoard = new CheckerBoard();
        chessBoard.add(checkerBoard, 0, 0);
    }

    private void initializeStartMenu() {
        newGamePopup = new PopupControl();
        newGamePopup.setId("new-game-popup");

        addGameOptions();

        newGameButton.selectedProperty().addListener((observable, wasSelected, isSelected) -> {
            if (isSelected) showPopup(newGamePopup, newGameButton);
            else newGamePopup.hide();
        });
    }

    private void showPopup(PopupControl popup, Control ownerNode) {

        double layoutX = ownerNode.getWidth() / 2;
        double layoutY = ownerNode.getHeight();
        Point2D anchorPoint = ownerNode.localToScreen(layoutX, layoutY);

        popup.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_LEFT);
        popup.show(ownerNode, anchorPoint.getX(), anchorPoint.getY());
    }

    private void addGameOptions() {
        VBox root = new VBox(15);
        root.setId("new-game-root-vbox");

        HBox teamPrompt = getPlayerTeamPrompt();

        Button playButton = new Button("Play!");
        playButton.setId("play-button");
        playButton.setOnAction(this::loadBoard);

        HBox playPrompt = new HBox();
        playPrompt.setId("play-prompt-container");
        playPrompt.getChildren().add(playButton);

        root.getChildren().addAll(teamPrompt, playPrompt);
        newGamePopup.getScene().setRoot(root);
    }

    private HBox getPlayerTeamPrompt() {
        HBox container = new HBox(15);
        container.setId("team-prompt-container");

        Label teamPrompt = new Label("Team");
        teamPrompt.setFont(new Font(14));

        ToggleButton teamSelector = new ToggleButton();
        teamSelector.setId("team-toggle-button");

        teamSelector.selectedProperty().addListener((observable, wasSelected, isSelected) -> {
            userColor = isSelected ? "black" : "white";
        });
        container.getChildren().addAll(teamPrompt, teamSelector);

        return container;
    }

    private void loadBoard(ActionEvent e) {
        newGamePopup.hide();
        newGameButton.setSelected(false);
        interactiveBoard = true;

        if (userColor.equals("white"))  checkerBoard.switchTileColors();
        else  checkerBoard.revertTileColors();

        game = new Game(chessBoard, checkerBoard.getTiles(), userColor);
    }

    private void initializeAppearanceMenu() {

    }

    private void initializeAvatar() {
        try {
            String imageUrl = Objects.requireNonNull(
                    getClass().getResource("Images/default avatar.png")).toExternalForm();

            Image avatarPic = new Image(imageUrl);
            avatar.setFill(new ImagePattern(avatarPic));

        } catch (IllegalArgumentException e) {
            System.out.println("Failed to load avatar picture: illegal argument."); //TODO

        } catch (NullPointerException e) {
            System.out.println("Failed to load avatar picture: null path."); //TODO
        }
    }

    private void newTileSelection(Tile tile) {
       if (!interactiveBoard || !tile.allowsInteraction()) return;
       game.newMove(tile);
       System.out.println("why hello there");

       getNpcResponse();
    }
    
    private void getNpcResponse() {
        // the long awaited hard part
    }

    protected void setStage(Stage stage) {
        this.stage = stage;
    }

    private class CheckerBoard extends Pane {

        private final int tiles = 8;
        private final Tile[][] checkerBoard = new Tile[tiles][tiles];
        protected CheckerBoard() {

            for (int x = 0; x < tiles; x++) {
                for (int y = 0; y < tiles; y++) {
                    createTile(x, y);
                }
            }
        }

        private void createTile(int x, int y) {
            checkerBoard[x][y] = new Tile(x, y);

            checkerBoard[x][y].setFill(getTileColor(x, y, false));
            checkerBoard[x][y].setOnMouseClicked(e -> newTileSelection(checkerBoard[x][y]));

            getChildren().add(checkerBoard[x][y]);
        }

        private Color getTileColor(int x, int y, boolean switchColor) {
            boolean transparent;

            if (switchColor)
                transparent = (x % 2 == 0 && y % 2 == 1) || (x % 2 == 1 && y % 2 == 0);
            else
                transparent = (x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1);

            Color darkGray = Color.web("#ededed", 0.9);
            return transparent ? Color.TRANSPARENT : darkGray;
        }

        protected void switchTileColors() {

            for (int x = 0; x < tiles; ++x) {
                for (int y = 0; y < tiles; ++y) {
                    checkerBoard[x][y].setFill(getTileColor(x, y, true));
                }
            }
        }

        protected void revertTileColors() {
            for (int x = 0; x < tiles; ++x) {
                for (int y = 0; y < tiles; ++y) {

                    checkerBoard[x][y].setFill(getTileColor(x, y, false));
                }
            }
        }

        protected Tile[][] getTiles() {
            return checkerBoard;
        }
    }
}
