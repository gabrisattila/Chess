package classes.Game.I18N;

import classes.Model.Game.I18N.Location;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class METHODSTest {

    Random random = new Random();

    Set<Integer> integers1 = new HashSet<>(){{
        add(1); add(2); add(3); add(4); add(5); add(6); add(7);
    }};

    Set<Integer> integers2 = new HashSet<>(){{
        add(4); add(5); add(6); add(7); add(8); add(9); add(10);
    }};

    @Test
    public void testChangeEvenOrOddStep() {
        int evenOrOdd = evenOrOddStep;
        changeEvenOrOddStep();
        if (evenOrOdd == 0)
            Assert.assertEquals(evenOrOddStep, 1);
        else
            Assert.assertEquals(evenOrOddStep, 0);
    }

    @Test
    public void testUnion() {
        Set<Integer> ints = (Set<Integer>) union(integers1, integers2);
        Assert.assertEquals(10, ints.size());
        Assert.assertEquals(new HashSet<Integer>(){{add(1); add(2); add(3); add(4); add(5); add(6); add(7); add(8); add(9); add(10);}}, ints);
    }

    @Test
    public void testIntersection() {
        Set<Integer> ints = (Set<Integer>) intersection(integers1, integers2);
        Assert.assertEquals(4, ints.size());
        Assert.assertEquals(new HashSet<Integer>(){{add(4); add(5); add(6); add(7);}}, ints);
    }

    @Test
    public void testMinus() {
        Set<Integer> ints = (Set<Integer>) minus(integers1, integers2);
        Assert.assertEquals(3, ints.size());
        Assert.assertEquals(new HashSet<Integer>(){{add(1); add(2); add(3);}}, ints);
    }

    @Test
    public void testLocationCollectionContainsOrNotContains() {

        Set<Location> locationCollection = new HashSet<>(){{
            add(new Location(0, 0));
            add(new Location(0, 1));
            add(new Location(0, 2));
            add(new Location(0, 3));
            add(new Location(0, 4));
            add(new Location(0, 5));
        }};
        for (int i = 0; i < 10; i++) {
            if (i <= 5)
                Assert.assertTrue(locationCollectionContains(locationCollection, new Location(0, i)));
            else
                Assert.assertTrue(collectionNotContains(locationCollection, new Location(0, i)));
        }
    }

    @Test
    public void testContainsLocation() {
        for (int i = 0; i < 10; i++) {
            int j = random.nextInt(-i, i + 1), k = random.nextInt(-i, i + 1);
            Assert.assertEquals(0 <= j && j <= 7 && 0 <= k && k <= 7, containsLocation(j, k));
        }
    }

    @Test
    public void testLocationSetTimesN() {
        Set<Location> set = new HashSet<>(){{
            add(new Location(1, 1)); add(new Location(1, 1)); add(new Location(1, 1));
            add(new Location(1, 1)); add(new Location(1, 1)); add(new Location(1, 1));
        }};
        for (int i = 1; i < 6; i++) {
            set = locationSetTimesN(set, i);
        }
        for (Location l : set) {
            Assert.assertEquals(2 * 3 * 4 * 5, l.getI());
            Assert.assertEquals(2 * 3 * 4 * 5, l.getJ());
        }
    }

    @Test
    public void testCountOccurrences() {
        String hello = "Hello world.";
        Assert.assertEquals(1, countOccurrences(hello, 'H'));
        Assert.assertEquals(1, countOccurrences(hello, 'e'));
        Assert.assertEquals(3, countOccurrences(hello, 'l'));
        Assert.assertEquals(2, countOccurrences(hello, 'o'));
    }
}