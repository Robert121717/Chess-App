import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
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
    private CheckerBoard boardBackground;
    private String userColor = "white";
    private boolean interactiveBoard = false;

    private Game game;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeAvatar();
        initializeStartMenu();

        boardBackground = new CheckerBoard();
        chessBoard.add(boardBackground, 0, 0);
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

        if (userColor.equals("white"))  boardBackground.switchTileColors();
        else  boardBackground.revertTileColors();

        game = new Game(chessBoard, boardBackground, userColor);
        configureBoard();
    }

    private void configureBoard() {
        HashSet<Piece> pieces = game.loadPieces();
        fillUnoccupiedTiles();

        for (Piece piece : pieces) {
            StackPane node = piece.getNode();
            node.setOnMouseClicked(e -> newTileSelection(piece.getTileOccupying()));

            int[] coordinate = piece.getLocation();
            chessBoard.add(node, coordinate[0], coordinate[1]);
        }
    }


    private void fillUnoccupiedTiles() {
        Tile[][] tiles = boardBackground.getTiles();

        for (int col = 0; col <= 7; ++col) {
            for (int row = 2; row <= 5; ++row) {

                StackPane filler = getFillerNode(col, row);
                tiles[col][row].setNode(filler);
                chessBoard.add(filler, col, row);
            }
        }
    }

    private StackPane getFillerNode(int x, int y) {
        StackPane filler = new StackPane();

        Tile[][] tiles = boardBackground.getTiles();
        filler.setOnMouseClicked(e -> newTileSelection(tiles[x][y]));

        return filler;
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
       if (!interactiveBoard) return;

       if (tile.isDestinationTile()) { // todo swapping kind & rook
           Tile tilePlaceHolder = game.finishRound(tile);

           tilePlaceHolder.getNode().setOnMouseClicked(e ->
                   newTileSelection(tilePlaceHolder));

           npcResponse();

       } else if (tile.isSelected()) {
           game.cancelRound();

       } else if (tile.isOccupied() && tile.getPiece().isPlayer()) {
           game.newRound(tile);
       }
    }
    
    private void npcResponse() {
        // the long awaited hard part
    }

    protected void setStage(Stage stage) {
        this.stage = stage;
    }
}
