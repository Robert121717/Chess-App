import javafx.scene.paint.LinearGradient;
import javafx.scene.shape.Rectangle;

public class Themes {

    private static final LinearGradient DEFAULT =
            LinearGradient.valueOf("linear-gradient(from 20% 25% to 100% 100%, #983cc9, #1a54db)");

    private static final LinearGradient GRAY =
            LinearGradient.valueOf("linear-gradient(from 20% 25% to 100% 100%, #bdc3c7, #2c3e50)");
    private static final LinearGradient SKY =
            LinearGradient.valueOf("linear-gradient(from 20% 25% to 100% 100%, #ffd89b, #19547b)");

    private static final LinearGradient DARK_PURPLE =
            LinearGradient.valueOf("linear-gradient(from 20% 25% to 100% 100%, #2193b0, #6dd5ed)");
    private static final LinearGradient PURPLE =
            LinearGradient.valueOf("linear-gradient(from 20% 25% to 100% 100%, #42275a, #734b6d)");

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



    protected static Rectangle[][] getGradients() {
        final int width = 30, height = 30;
        Rectangle[][] gradients = new Rectangle[2][6];

        gradients[0][0] = new Rectangle(width, height, GRAY);
        gradients[0][1] = new Rectangle(width, height, SKY);
        gradients[0][2] = new Rectangle(width, height, DEFAULT);
        gradients[0][3] = new Rectangle(width, height, DARK_PURPLE);
        gradients[0][4] = new Rectangle(width, height, PURPLE);
        gradients[0][5] = new Rectangle(width, height, NAVY);

        gradients[1][0] = new Rectangle(width, height, DARK_BLUE);
        gradients[1][1] = new Rectangle(width, height, BLUE);
        gradients[1][2] = new Rectangle(width, height, TEAL);
        gradients[1][3] = new Rectangle(width, height, RED);
        gradients[1][4] = new Rectangle(width, height, ORANGE);
        gradients[1][5] = new Rectangle(width, height, PEACH);

        return gradients;
    }
}