package classes;

import static classes.Game.Model.Structure.BitBoard.BitBoards.*;

public class Main {

    public static void main(String[] args) {
//           exceptionIgnorer();
//        new EDT();
        setUpStarterBitBoards();
        System.out.println(bitBoardsToFenPieces());


    }
}