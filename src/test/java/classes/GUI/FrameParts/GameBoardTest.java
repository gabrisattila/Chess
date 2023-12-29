package classes.GUI.FrameParts;

import classes.GUI.Frame.Window;
import classes.Model.Game.I18N.Location;
import classes.Model.Game.Structure.IField;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static classes.GUI.FrameParts.GameBoard.clearLeftIcons;
import static classes.GUI.FrameParts.GameBoard.edgeCoordinates;
import static classes.Model.Game.I18N.VARS.FINALS.usualFens;

public class GameBoardTest {

    @Test
    public void testClearLeftIcons() {
        Window window = Window.getWindow();
        Window.setUpSides(usualFens.get("whiteDownStarter"));
        clearLeftIcons();
        for (ArrayList<IField> fields : window.getGameBoard().getParentBoard().getFields()) {
            for (IField f : fields) {
                Assert.assertFalse(f.isGotPiece());
            }
        }
    }

    @Test
    public void testCollectEdges(){
        GameBoard.collectEdges();
        Assert.assertEquals(32, edgeCoordinates.size());
        for (Location l : edgeCoordinates) {
            Assert.assertNotEquals(l, new Location());
        }
    }

    @Test
    public void testLabels(){
        GameBoard gameBoard = new GameBoard();
        GameBoard.addLabelsByLocation(gameBoard);
        Assert.assertEquals(32, GameBoard.getLabels().size());
        Assert.assertEquals(edgeCoordinates.size(), GameBoard.getLabels().size());
        for (int i = 0; i < edgeCoordinates.size(); i++) {
            Assert.assertEquals(
                    edgeCoordinates.get(i),
                    new Location(GameBoard.getLabels().get(i).getXCoordinate(), GameBoard.getLabels().get(i).getYCoordinate())
            );
        }
    }
}