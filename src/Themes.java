import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;

public class Themes {

    private static final LinearGradient DEFAULT =
            LinearGradient.valueOf("linear-gradient(from 20% 25% to 100% 100%, #983cc9, #1a54db)");
    private static final LinearGradient GRAY =
            LinearGradient.valueOf("linear-gradient(from 20% 25% to 100% 100%, #bdc3c7, #2c3e50)");
    private static final LinearGradient SKY =
            LinearGradient.valueOf("linear-gradient(from 20% 25% to 100% 100%, #ffd89b, #19547b)");
    private static final LinearGradient DARK_PURPLE =
            LinearGradient.valueOf("linear-gradient(from 20% 25% to 100% 100%, #42275a, #734b6d)");
    private static final LinearGradient PURPLE =
            LinearGradient.valueOf("linear-gradient(from 20% 25% to 100% 100%, #614385, #516395)");
    private static final LinearGradient NAVY =
            LinearGradient.valueOf("linear-gradient(from 20% 25% to 100% 100%, #141e30, #243b55)");
    private static final LinearGradient DARK_BLUE =
            LinearGradient.valueOf("linear-gradient(from 20% 25% to 100% 100%, #000428, #004e92)");
    private static final LinearGradient BLUE =
            LinearGradient.valueOf("linear-gradient(from 20% 25% to 100% 100%, #2193b0, #6dd5ed)");
    private static final LinearGradient TEAL =
            LinearGradient.valueOf("linear-gradient(from 20% 25% to 100% 100%, #06beb6, #48b1bf)");
    private static final LinearGradient RED =
            LinearGradient.valueOf("linear-gradient(from 20% 25% to 100% 100%, #eb3349, #f45c43)");
    private static final LinearGradient ORANGE =
            LinearGradient.valueOf("linear-gradient(from 20% 25% to 100% 100%, #de6262, #ffb88c)");
    private static final LinearGradient PEACH =
            LinearGradient.valueOf("linear-gradient(from 20% 25% to 100% 100%, #ff5f6d, #ffc371)");

    private static final int tileSize = 12;



    protected static Rectangle[][] getGradients() {
        final int width = 30, height = 30;
        Rectangle[][] gradients = new Rectangle[3][4];

        gradients[0][0] = new Rectangle(width, height, GRAY);
        gradients[1][0] = new Rectangle(width, height, NAVY);
        gradients[2][0] = new Rectangle(width, height, SKY);

        gradients[0][1] = new Rectangle(width, height, DARK_PURPLE);
        gradients[1][1] = new Rectangle(width, height, PURPLE);
        gradients[2][1] = new Rectangle(width, height, DEFAULT);

        gradients[0][2] = new Rectangle(width, height, DARK_BLUE);
        gradients[1][2] = new Rectangle(width, height, BLUE);
        gradients[2][2] = new Rectangle(width, height, TEAL);

        gradients[0][3] = new Rectangle(width, height, RED);
        gradients[1][3] = new Rectangle(width, height, ORANGE);
        gradients[2][3] = new Rectangle(width, height, PEACH);

        return gradients;
    }

    protected static ArrayList<CheckerBoard> getBoards() {
        ArrayList<CheckerBoard> checkerBoards = new ArrayList<>();
        //https://www.chess.com/article/view/what-your-chess-board-theme-says-about-you

        checkerBoards.add(getDefaultBoard());
        checkerBoards.add(getOriginalBoard());
        checkerBoards.add(getWoodBoard());
        checkerBoards.add(getBlackWoodBoard());
        checkerBoards.add(getNavyBoard());
        checkerBoards.add(getLightBoard());

        return checkerBoards;
    }

    private static CheckerBoard getDefaultBoard() {
        Paint transparent = Color.WHITE;
        Paint darkGray = Color.web("#5a5a5a");

        return new CheckerBoard(transparent, darkGray, tileSize);
    }

    private static CheckerBoard getBlackWoodBoard() {
        ImagePattern blackWood = new ImagePattern(new Image("Images/blackWood.jpg"));

        return new CheckerBoard(Color.WHITESMOKE, blackWood, tileSize);
    }

    private static CheckerBoard getNavyBoard() {
        Paint blueGray = Color.web("#7a8593");
        Paint navyBlue = Color.web("#303743");

        return new CheckerBoard(blueGray, navyBlue, tileSize);
    }

    private static CheckerBoard getOriginalBoard() {
        Paint tan = Color.web("#f0d9b5");
        Paint brown = Color.web("#b58863");

        return new CheckerBoard(tan, brown, tileSize);
    }

    private static CheckerBoard getWoodBoard() {
        ImagePattern walnutWood = new ImagePattern(new Image("Images/walnutWood.jpg"));
        ImagePattern darkWood = new ImagePattern(new Image("Images/darkWood.jpg"));

        return new CheckerBoard(walnutWood, darkWood, tileSize);
    }

    private static CheckerBoard getLightBoard() {
        Paint lightGray = Color.web("#d9e4e8");
        Paint lightBlue = Color.web("#789bb1");

        return new CheckerBoard(lightGray, lightBlue, tileSize);
    }
}