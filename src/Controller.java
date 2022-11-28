import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.*;
import static javafx.scene.effect.BlurType.GAUSSIAN;

public class Controller implements Initializable {

    private Stage stage;
    private PopupControl newGamePopup;
    private PopupControl appearancePopup;
    private ToggleButton themeToggleButton;
    private ToggleButton boardToggleButton;
    private Paint tileFill1;
    private Paint tileFill2;
    private int scrollPos = 0;
    private final int minScrollPos = 0;
    private final int maxScrollPos = 200;

    @FXML
    private VBox backgroundVBox;
    @FXML
    private HBox navBar;
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
        initializeGameMenu();
        initializeAppearanceMenu();

        tileFill1 = Color.TRANSPARENT;
        tileFill2 = Color.web("#ededed", 0.9);

        boardBackground = new CheckerBoard(tileFill1, tileFill2, 55);
        chessBoard.add(boardBackground, 0, 0);
    }

    private void initializeGameMenu() {
        newGamePopup = new PopupControl();
        newGamePopup.setId("new-game-popup");

        addGameOptions();

        newGameButton.selectedProperty().addListener((observable, wasSelected, isSelected) -> {
            if (appearancePopup.isShowing()) appearancePopup.hide();

            if (isSelected) {
                appearanceButton.setSelected(false);
                showPopup(newGamePopup, newGameButton, PopupWindow.AnchorLocation.WINDOW_TOP_LEFT);

            } else newGamePopup.hide();
        });
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

    private void initializeAppearanceMenu() {
        appearancePopup = new PopupControl();
        appearancePopup.setId("appearance-popup");

        addAppearanceOptions();

        appearanceButton.selectedProperty().addListener((observable, wasSelected, isSelected) -> {
            if (newGamePopup.isShowing()) newGamePopup.hide();

            if (isSelected) {
                newGameButton.setSelected(false);
                showPopup(appearancePopup, appearanceButton, PopupWindow.AnchorLocation.WINDOW_TOP_RIGHT);

            } else appearancePopup.hide();
        });
    }

    private void addAppearanceOptions() {
        VBox root = new VBox();
        root.setId("appearance-root-vbox");

        HBox navBar = createAppearanceNavBar();
        Separator line = new Separator(Orientation.HORIZONTAL);

        HBox themes = createColorThemes();
        ScrollPane boards = createBoardThemes();

        themeToggleButton.setOnAction(e -> {
            themeToggleButton.setSelected(true);
            boardToggleButton.setSelected(false);
            root.getChildren().remove(2);
            root.getChildren().add(themes);
        });

        boardToggleButton.setOnAction(e -> {
            boardToggleButton.setSelected(true);
            themeToggleButton.setSelected(false);
            root.getChildren().remove(2);
            root.getChildren().add(boards);
        });

        root.getChildren().addAll(navBar, line, themes);
        appearancePopup.getScene().setRoot(root);
    }

    private HBox createAppearanceNavBar() {
        HBox navBar = new HBox(25);
        navBar.setId("appearance-nav-bar");

        themeToggleButton = new ToggleButton("Themes");
        themeToggleButton.setSelected(true);
        boardToggleButton = new ToggleButton("Boards");

        navBar.getChildren().addAll(themeToggleButton, boardToggleButton);
        return navBar;
    }

    private HBox createColorThemes() {
        HBox gradientVBox = new HBox();
        gradientVBox.setId("gradient-options-container");

        GridPane gradients = getColorGrid();
        gradientVBox.getChildren().addAll(gradients);

        return gradientVBox;
    }

    private GridPane getColorGrid() {

        Rectangle[][] colors = Themes.getGradients();
        GridPane display = new GridPane();

        for (int y = 0; y < colors.length; ++y) {
            Rectangle[] row = colors[y];

            for (int x = 0; x < row.length; ++x) {
                Rectangle rect = row[x];
                rect.setArcHeight(10);
                rect.setArcWidth(10);

                rect.setOnMouseClicked(e -> setTheme(rect));
                display.add(rect, x, y);
            }
        }
        display.setHgap(15);
        display.setVgap(10);

        return display;
    }

    private void setTheme(Rectangle rect) {
        String color = rect.getFill().toString();
        color = color.replace("0x", "#");

        navBar.setStyle("-fx-background-color: " + color + ";");
    }

    private ScrollPane createBoardThemes() {
        final int delta = 50;

        ScrollPane root = new ScrollPane();
        root.setHmin(minScrollPos);
        root.setHmax(maxScrollPos);

        HBox container = getBoards();
        container.setOnScroll(e -> {
            if (e.getDeltaY() < 0 || e.getDeltaX() < 0)
                root.setHvalue(scrollPos == minScrollPos ? minScrollPos : (scrollPos -= delta));
            else
                root.setHvalue(scrollPos + delta == maxScrollPos ? maxScrollPos : (scrollPos += delta));
        });

        root.setContent(container);
        return root;
    }

    private HBox getBoards() {
        HBox container = new HBox(30);
        container.setId("board-theme-container");

        for (CheckerBoard board : Themes.getBoards()) {
            container.getChildren().add(board);
            board.setOnMouseClicked(e -> setBoardTheme(container, board));
        }
        return container;
    }

    private void setBoardTheme(HBox boardContainer, CheckerBoard boardSelected) {

        for (Node board : boardContainer.getChildren()) {
            board.setEffect(new InnerShadow(GAUSSIAN, Color.WHITE, 10, 0.5, 0, 0));
        }
        // todo make this better
        boardSelected.setEffect(
                new InnerShadow(GAUSSIAN, Color.web("#3aafdc", 0.88), 50, 0.1, 0, 0));
        Paint[] theme = boardSelected.getTheme();
        tileFill1 = theme[1];
        tileFill2 = theme[0];
    }

    private void showPopup(PopupControl popup, Control ownerNode, PopupWindow.AnchorLocation anchorLoc) {

        double layoutX = ownerNode.getWidth();
        if (anchorLoc.equals(PopupWindow.AnchorLocation.WINDOW_TOP_LEFT)) layoutX /= 2;
        else layoutX -= layoutX / 5;

        double layoutY = ownerNode.getHeight();
        Point2D anchorPoint = ownerNode.localToScreen(layoutX, layoutY);
        popup.setAnchorLocation(anchorLoc);
        popup.show(ownerNode, anchorPoint.getX(), anchorPoint.getY());
    }

    private HBox getPlayerTeamPrompt() {
        HBox container = new HBox(15);
        container.setId("team-prompt-container");

        Label teamPrompt = new Label("Team");
        teamPrompt.setFont(new Font(14));

        ToggleButton teamSelector = new ToggleButton();
        teamSelector.setId("team-toggle-button");

        teamSelector.selectedProperty().addListener((observable, wasSelected, isSelected) ->
                userColor = isSelected ? "black" : "white");
        container.getChildren().addAll(teamPrompt, teamSelector);

        return container;
    }

    private void loadBoard(ActionEvent e) {
        newGamePopup.hide();
        newGameButton.setSelected(false);
        interactiveBoard = true;

        boardBackground = new CheckerBoard(tileFill1, tileFill2, 55);

        if (userColor.equals("white") && !boardBackground.isHasDefaultTileScheme())
            boardBackground.switchTileColors(false);

        else if (userColor.equals("black") && boardBackground.isHasDefaultTileScheme())
            boardBackground.switchTileColors(true);

        game = new Game(chessBoard, boardBackground.getTiles(), userColor);
        configureBoard();
    }

    private void configureBoard() {
        HashSet<Piece> pieces = game.loadPieces(boardBackground);
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

                StackPane placeHolder = createPlaceHolder(col, row);
                tiles[col][row].setNode(placeHolder);
                chessBoard.add(placeHolder, col, row);
            }
        }
    }

    private StackPane createPlaceHolder(int x, int y) {
        StackPane filler = new StackPane();

        Tile[][] tiles = boardBackground.getTiles();
        filler.setOnMouseClicked(e -> newTileSelection(tiles[x][y]));

        return filler;
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

    private void newTileSelection(Tile clickedTile) {
       if (!interactiveBoard) return;
       closePopups();

       if (clickedTile.isDestinationTile()) {

           Tile[] modifiedTiles = game.finishMove(clickedTile);
           for (Tile tile : modifiedTiles)
               tile.getNode().setOnMouseClicked(e -> newTileSelection(tile));

           npcResponse();

       } else if (clickedTile.isSelected()) {
           game.cancelMove();

       } else if (clickedTile.isOccupied() && clickedTile.getPiece().isPlayer()) {
           game.newMove(clickedTile);
       }
    }

    private void closePopups() {

        if (appearancePopup.isShowing()) {
            appearancePopup.hide(); appearanceButton.setSelected(false);

        } else if (newGamePopup.isShowing()) {
            newGamePopup.hide(); newGameButton.setSelected(false);
        }
    }
    
    private void npcResponse() {
        interactiveBoard = false;
        // the long awaited hard part
        interactiveBoard = true;
    }

    protected void setStage(Stage stage) {
        this.stage = stage;
    }
}
