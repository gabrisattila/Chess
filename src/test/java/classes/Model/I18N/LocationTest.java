package classes.Model.I18N;

import classes.GUI.FrameParts.ViewField;
import classes.Model.Structure.Field;
import classes.Model.Structure.Piece;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class LocationTest {

    Random random = new Random();

    @Test
    public void testTestEqualsObj() {
        Object o = new Object();
        Piece piece = new Piece();
        Field field = new Field(0, 0);
        ViewField viewField = new ViewField();
        Assert.assertNotEquals(new Location(), o);
        Assert.assertNotEquals(new Location(), piece);
        Assert.assertNotEquals(new Location(), field);
        Assert.assertNotEquals(new Location(), viewField);
    }

    @Test
    public void testTestEqualsIJ() {
        int i = random.nextInt(), j = random.nextInt();
        Location l = new Location(i, j);
        Assert.assertEquals(i, l.getI());
        Assert.assertEquals(j, l.getJ());
        Assert.assertEquals(new Location(i, j), l);
        Assert.assertNotEquals(new Location(i - 1, j - 1), l);
    }

    @Test
    public void testAdd() {
        int i1 = random.nextInt(), j1 = random.nextInt(), i2 = random.nextInt(), j2 = random.nextInt();
        Location l1 = new Location(i1, j1), l2 = new Location(i2, j2);
        Location sum = l1.add(l2);
        Assert.assertEquals(sum.getI(), i1 + i2);
        Assert.assertEquals(sum.getJ(), j1 + j2);
    }

    @Test
    public void testTimes() {
        int i1 = random.nextInt(), j1 = random.nextInt(), n = random.nextInt();
        Location l1 = new Location(i1, j1);
        Location times = l1.times(n);
        Assert.assertEquals(times.getI(), i1 * n);
        Assert.assertEquals(times.getJ(), j1 * n);
    }

    @Test
    public void testTestToString() {
        Location l = new Location(0, 0);
        String s = "[0, 0] ";
        String lToString = l.toString();
        Assert.assertEquals(s, lToString);
    }

    @Test
    public void testEmPassantStringToLocation() {
        String s1 = "";
        String s2 = "      ";
        String s3 = "-10";
        String s4 = "25 ";
        String s5 = "15";
        Location l = new Location(1, 5);
        String lEmPassantS = String.valueOf(l.getI());
        lEmPassantS += l.getJ();
        Assert.assertNotEquals(s1, lEmPassantS);
        Assert.assertNotEquals(s2, lEmPassantS);
        Assert.assertNotEquals(s3, lEmPassantS);
        Assert.assertNotEquals(s4, lEmPassantS);
        Assert.assertEquals(s5, lEmPassantS);
    }
}