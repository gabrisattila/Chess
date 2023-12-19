package classes.Ai.AI;

import classes.Game.I18N.Pair;
import classes.Ai.BitBoards.Zobrist;
import lombok.*;

import java.util.ArrayList;
import java.util.Random;

import static classes.Ai.AI.AiNode.*;
import static classes.Ai.FenConverter.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.Model.Logic.EDT.*;
import static classes.Ai.BitBoards.BBVars.*;
import static classes.Ai.BitBoards.BitBoardMoves_.*;
import static classes.Ai.BitBoards.BitBoards.*;
import static classes.Game.Model.Structure.Board.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Structure.GameOverOrPositionEnd.*;
import static classes.GUI.FrameParts.Logger.*;
import static classes.Game.Model.Structure.IBoard.convertOneBoardToAnother;


/**
 * thread descriptor object
 */
@Getter
@Setter
public class AI extends Thread {

    //region Fields

    //endregion


    //region Constructor

    public AI(){}

    //endregion


    //region Methods

    @Override
    public void run(){
        try {
            aiMove();
            receivedMoveFromAi(BoardToFen(getBoard()));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void aiMove() throws InterruptedException {
        convertOneBoardToAnother(getViewBoard(), getBoard());
        Move();
        logAiStep(detectChessMove(BoardToFen(getViewBoard()), BoardToFen(getBoard())));
    }

    public void Move() {
        moveWithMiniMaxAi();
    }
    
    private boolean gameFinished(){
        return gameEndFlag.get();
    }

    public static void waitOnPause(){
        while(pauseFlag.get()) {
            try {
                pauseFlag.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    //region Mini Max

    private void moveWithMiniMaxAi() {
        convertOneBoardToAnother(getViewBoard(), getBoard());
        GameOverDecision(getBoard(), Double.MIN_VALUE);
        if (!gameFinished()) {
            MINIMAX();
        }
    }

    private void MINIMAX(){
        nodeNum = 0;

        setUpBitBoard(BoardToFen(getBoard()));

        int emPassant = emPassantToBitBoardEmPassant(emPassantChance);
        long zKey = Zobrist.getZobristKey(whiteToPlay, emPassant,
                whiteSmallCastleEnabled, whiteBigCastleEnabled, blackSmallCastleEnabled, blackBigCastleEnabled,
                whitePawn, whiteKnight, whiteBishop, whiteRook, whiteQueen, whiteKing,
                blackPawn, blackKnight, blackBishop, blackRook, blackQueen, blackKing);
        AiNode tree = new AiNode(zKey, "");

        int startTime = (int) System.currentTimeMillis();

        double bestChildValue = miniMax(
                tree, 0, whiteToPlay, -Double.MAX_VALUE, Double.MAX_VALUE,
                emPassant, whiteSmallCastleEnabled, whiteBigCastleEnabled, blackSmallCastleEnabled, blackBigCastleEnabled,
                whitePawn, whiteKnight, whiteBishop, whiteRook, whiteQueen, whiteKing,
                blackPawn, blackKnight, blackBishop, blackRook, blackQueen, blackKing);

        printDataOfMiniMax(startTime);

        whiteToPlay = !whiteToPlay;
        Pair<AiNode, String> nodeAndItsFen = sortOutBestChild(tree, bestChildValue);
        AiNode bestChild = nodeAndItsFen.getFirst();
        putToAlreadyWatchedZKeys(bestChild.getZobristKey(), bestChild);
        FenToBoard(nodeAndItsFen.getSecond(), getBoard());

    }

    private double miniMax(
            AiNode starterPos, int depth, boolean maxNeeded, double alpha, double beta,
            int emPassant, boolean wKC, boolean wQC, boolean bKC, boolean bQC,
            long whitePawn, long whiteKnight, long whiteBishop, long whiteRook, long whiteQueen, long whiteKing,
            long blackPawn, long blackKnight, long blackBishop, long blackRook, long blackQueen, long blackKing
    ){
        synchronized (pauseFlag){
            waitOnPause();
            if (depth == MINIMAX_DEPTH ||
                isDraw(whitePawn, whiteKnight, whiteBishop, whiteRook, whiteQueen, whiteKing,
                        blackPawn, blackKnight, blackBishop, blackRook, blackQueen, blackKing)){
                double finalValue = evaluationOfAMoveWithOutFieldValues(
                        starterPos.getTheMoveWhatsCreatedIt(),
                        whitePawn, whiteKnight, whiteBishop, whiteRook, whiteQueen, whiteKing,
                        blackPawn, blackKnight, blackBishop, blackRook, blackQueen, blackKing
                ); 
                starterPos.setFinalValue(finalValue);
                return finalValue;
            }

            ArrayList<String> legalMoves = possibleMoves(maxNeeded,
             emPassant,  wKC,  wQC,  bKC,  bQC,
             whitePawn, whiteKnight, whiteBishop, whiteRook,  whiteQueen,  whiteKing,
             blackPawn, blackKnight, blackBishop, blackRook,  blackQueen,  blackKing);

            if (maxNeeded){
                
                double possibleMax = -Double.MAX_VALUE;
                double evaluationOfTheNode = WHITE_GOT_CHECKMATE;
                
                if (legalMoves.isEmpty()){
                    if ((whiteKing &
                            unsafeFor(true,
                                    whitePawn, whiteKnight, whiteBishop, whiteRook, whiteQueen, whiteKing,
                                    blackPawn, blackKnight, blackBishop, blackRook, blackQueen, blackKing)) == 0){
                        //DRAW
                        evaluationOfTheNode = DRAW;
                    }
                    possibleMax = evaluationOfTheNode;
                }
                
                for (int i = 0; i < legalMoves.size(); i++) {
                    ArrayList<Long> nextBoards = new ArrayList<>();
                    String legalMove = "";
                    while (nextBoards.isEmpty()){
                        legalMove = legalMoves.get(i);
                        nextBoards = nextBoards(legalMove,
                                whitePawn, whiteKnight, whiteBishop, whiteRook, whiteQueen, whiteKing,
                                blackPawn, blackKnight, blackBishop, blackRook, blackQueen, blackKing);
                        if (nextBoards.isEmpty())
                            i++;
                        if (i == legalMoves.size())
                            break;
                    }
                    if (nextBoards.isEmpty()){
                        break;
                    }

                    ArrayList<Object> emPassantAndCastle = emPassantAndCastleCases(legalMove, wKC, wQC, bKC, bQC);

                    AiNode next = putNewToNodeMap(starterPos, legalMove,
                            true, (Integer) emPassantAndCastle.get(0),
                            (boolean) emPassantAndCastle.get(1), (boolean) emPassantAndCastle.get(2),
                            (boolean) emPassantAndCastle.get(3), (boolean) emPassantAndCastle.get(4),
                            nextBoards.get(0), nextBoards.get(1), nextBoards.get(2), nextBoards.get(3), nextBoards.get(4), nextBoards.get(5),
                            nextBoards.get(6), nextBoards.get(7), nextBoards.get(8), nextBoards.get(9), nextBoards.get(10), nextBoards.get(11));

                    nodeNum++;
                    evaluationOfTheNode = miniMax(
                            next, depth + 1, false, alpha, beta,
                            (Integer) emPassantAndCastle.get(0),
                            (boolean) emPassantAndCastle.get(1), (boolean) emPassantAndCastle.get(2),
                            (boolean) emPassantAndCastle.get(3), (boolean) emPassantAndCastle.get(4),
                            nextBoards.get(0), nextBoards.get(1), nextBoards.get(2), nextBoards.get(3), nextBoards.get(4), nextBoards.get(5),
                            nextBoards.get(6), nextBoards.get(7), nextBoards.get(8), nextBoards.get(9), nextBoards.get(10), nextBoards.get(11)
                    );


                    possibleMax = Math.max(possibleMax, evaluationOfTheNode);
                    alpha = Math.max(alpha, evaluationOfTheNode);
                    if (beta <= alpha)
                        break;
                }
                
                starterPos.setFinalValue(possibleMax);
                return possibleMax;
            }else {

                double possibleMin = BLACK_GOT_CHECKMATE;

                if (legalMoves.isEmpty()){
                    if ((blackKing &
                            unsafeFor(false,
                                    whitePawn, whiteKnight, whiteBishop, whiteRook, whiteQueen, whiteKing,
                                    blackPawn, blackKnight, blackBishop, blackRook, blackQueen, blackKing)) == 0){
                        //DRAW
                        possibleMin = DRAW;
                    }
                }

                for (int i = 0; i < legalMoves.size(); i++) {
                    ArrayList<Long> nextBoards = new ArrayList<>();
                    String legalMove = "";
                    while (nextBoards.isEmpty()){
                        legalMove = legalMoves.get(i);
                        nextBoards = nextBoards(legalMove,
                                whitePawn, whiteKnight, whiteBishop, whiteRook, whiteQueen, whiteKing,
                                blackPawn, blackKnight, blackBishop, blackRook, blackQueen, blackKing);
                        if (nextBoards.isEmpty())
                            i++;
                        if (i == legalMoves.size())
                            break;
                    }
                    if (nextBoards.isEmpty()){
                        break;
                    }

                    ArrayList<Object> emPassantAndCastle = emPassantAndCastleCases(legalMove, wKC, wQC, bKC, bQC);
                    AiNode next = putNewToNodeMap(starterPos, legalMove,
                            false, (Integer) emPassantAndCastle.get(0),
                            (boolean) emPassantAndCastle.get(1), (boolean) emPassantAndCastle.get(2),
                            (boolean) emPassantAndCastle.get(3), (boolean) emPassantAndCastle.get(4),
                            nextBoards.get(0), nextBoards.get(1), nextBoards.get(2), nextBoards.get(3), nextBoards.get(4), nextBoards.get(5),
                            nextBoards.get(6), nextBoards.get(7), nextBoards.get(8), nextBoards.get(9), nextBoards.get(10), nextBoards.get(11));
                    nodeNum++;
                    double evaluationOfTheNode = miniMax(
                            next, depth + 1, true, alpha, beta,
                            (Integer) emPassantAndCastle.get(0),
                            (boolean) emPassantAndCastle.get(1), (boolean) emPassantAndCastle.get(2),
                            (boolean) emPassantAndCastle.get(3), (boolean) emPassantAndCastle.get(4),
                            nextBoards.get(0), nextBoards.get(1), nextBoards.get(2), nextBoards.get(3), nextBoards.get(4), nextBoards.get(5),
                            nextBoards.get(6), nextBoards.get(7), nextBoards.get(8), nextBoards.get(9), nextBoards.get(10), nextBoards.get(11)
                    );

                    possibleMin = Math.min(possibleMin, evaluationOfTheNode);
                    beta = Math.min(beta, evaluationOfTheNode);
                    if (beta <= alpha)
                        break;
                }
                starterPos.setFinalValue(possibleMin);
                return possibleMin;
            }
        }
    }

    private void printDataOfMiniMax(int startTime){
        System.out.println();
        int endTime = (int) System.currentTimeMillis();
        System.out.println("Minimax evaluated "+ nodeNum + " nodes on\n\t\t\t\t  " + MINIMAX_DEPTH + " depth.");
        int elapsedTime = endTime - startTime;
        System.out.println("Under " + ((double) elapsedTime / 1000) + " seconds.");
        double nps =((double) nodeNum / ((double) elapsedTime / 1000));
        System.out.println("That means the effectiveness is: " + nps + " node / sec.");
        System.out.println(transPosNum + " transposition happened.");
        System.out.println();
    }

    private Pair<AiNode, String> sortOutBestChild(AiNode tree, double bestChildValue){
        ArrayList<AiNode> bestChildren = new ArrayList<>();
        for (AiNode child : tree.getChildren()) {
            if (child.getFinalValue() == bestChildValue) {
                bestChildren.add(child);
            }
        }
        AiNode bestChild;
        if (bestChildren.size() == 1) {
            bestChild = bestChildren.get(0);
        } else {
            Random random = new Random();
            int randomChosenBestIndex = random.nextInt(0, bestChildren.size());
            bestChild = bestChildren.get(randomChosenBestIndex);
        }
        String fen = aiNodeToFen(bestChild);
        emPassantChance = fen.split(" ")[3];
        castleCaseFenToBoard(fen.split(" ")[2]);
        appendToHappenedList(fen);
        return new Pair<>(bestChild, fen);
    }

    /**
     * @param legalMove the move what about we make the modifications in the variables
     * @param wKC       castle bools
     * @param wQC       castle bools
     * @param bKC       castle bools
     * @param bQC       castle bools
     * @returns a list which contains the consequences of the move and also creates a node
     * emPassantChance, newWKC, newWQC, newBKC, newBQC
     */
    private ArrayList<Object> emPassantAndCastleCases(String legalMove,
                                                      boolean wKC, boolean wQC, boolean bKC, boolean bQC){
        
        int emPassantChance = 0;
        boolean newWKC = wKC, newWQC = wQC, newBKC = bKC, newBQC = bQC;
        
        if (legalMove.split("-").length > 3) {
            if (Character.isLetter(legalMove.split("-")[3].charAt(0))) {
                String castleModification = legalMove.split("-")[3];
                if (castleModification.length() == 1) {
                    switch (castleModification) {
                        case "K" -> newWKC = false;
                        case "Q" -> newWQC = false;
                        case "k" -> newBKC = false;
                        case "q" -> newBQC = false;
                    }
                } else {
                    if (Character.isUpperCase(castleModification.charAt(0))) {
                        newWKC = false;
                        newWQC = false;
                    } else {
                        newBKC = false;
                        newBQC = false;
                    }
                }
                emPassantChance = -1;
            } else {
                emPassantChance = Integer.parseInt(legalMove.split("-")[3]);
            }
        }
        
        ArrayList<Object> ret = new ArrayList<>();
        ret.add(emPassantChance);
        ret.add(newWKC);
        ret.add(newWQC);
        ret.add(newBKC);
        ret.add(newBQC);
        
        return ret;
    }

    /**
     * @return a list of longs which are the next node's bitboards
     */
    private ArrayList<Long> nextBoards(String legalMove,
            long whitePawn, long whiteKnight, long whiteBishop, long whiteRook, long whiteQueen, long whiteKing,
            long blackPawn, long blackKnight, long blackBishop, long blackRook, long blackQueen, long blackKing){

        ArrayList<Long> nexts = new ArrayList<>();

        if (Character.isUpperCase(legalMove.charAt(0))){
            whitePawn = moveAPieceOnBoard(legalMove, whitePawn, "P");
            whiteKnight = moveAPieceOnBoard(legalMove, whiteKnight, "N");
            whiteBishop = moveAPieceOnBoard(legalMove, whiteBishop, "B");
            whiteRook = moveAPieceOnBoard(legalMove, whiteRook, "R");
            whiteQueen = moveAPieceOnBoard(legalMove, whiteQueen, "Q");
            whiteKing = moveAPieceOnBoard(legalMove, whiteKing, "K");
            blackPawn = moveAPieceOnBoard(legalMove, blackPawn, "p");
            blackKnight = moveAPieceOnBoard(legalMove, blackKnight, "n");
            blackBishop = moveAPieceOnBoard(legalMove, blackBishop, "b");
            blackRook = moveAPieceOnBoard(legalMove, blackRook, "r");
            blackQueen = moveAPieceOnBoard(legalMove, blackQueen, "q");
            blackKing = moveAPieceOnBoard(legalMove, blackKing, "k");
        } else {
            blackPawn = moveAPieceOnBoard(legalMove, blackPawn, "p");
            blackKnight = moveAPieceOnBoard(legalMove, blackKnight, "n");
            blackBishop = moveAPieceOnBoard(legalMove, blackBishop, "b");
            blackRook = moveAPieceOnBoard(legalMove, blackRook, "r");
            blackQueen = moveAPieceOnBoard(legalMove, blackQueen, "q");
            blackKing = moveAPieceOnBoard(legalMove, blackKing, "k");
            whitePawn = moveAPieceOnBoard(legalMove, whitePawn, "P");
            whiteKnight = moveAPieceOnBoard(legalMove, whiteKnight, "N");
            whiteBishop = moveAPieceOnBoard(legalMove, whiteBishop, "B");
            whiteRook = moveAPieceOnBoard(legalMove, whiteRook, "R");
            whiteQueen = moveAPieceOnBoard(legalMove, whiteQueen, "Q");
            whiteKing = moveAPieceOnBoard(legalMove, whiteKing, "K");
        }

         return getLongs(nexts, Character.isUpperCase(legalMove.charAt(0)),
                whitePawn, whiteKnight, whiteBishop, whiteRook, whiteQueen, whiteKing,
                blackPawn, blackKnight, blackBishop, blackRook, blackQueen, blackKing);
    }

    private static ArrayList<Long> getLongs(
            ArrayList<Long> nexts, boolean forWhite,
            long whitePawn, long whiteKnight, long whiteBishop, long whiteRook, long whiteQueen, long whiteKing,
            long blackPawn, long blackKnight, long blackBishop, long blackRook, long blackQueen, long blackKing) {
        OCCUPIED = whitePawn | whiteKnight | whiteBishop | whiteRook | whiteQueen | whiteKing |
                    blackPawn | blackKnight | blackBishop | blackRook | blackQueen | blackKing;
        if ((forWhite && (whiteKing & unsafeFor(true, whitePawn, whiteKnight, whiteBishop, whiteRook, whiteQueen, whiteKing,
                                                blackPawn, blackKnight, blackBishop, blackRook, blackQueen, blackKing)) == 0) ||
            (!forWhite && (blackKing & unsafeFor(false, whitePawn, whiteKnight, whiteBishop, whiteRook, whiteQueen, whiteKing,
                                                blackPawn, blackKnight, blackBishop, blackRook, blackQueen, blackKing)) == 0)){

            nexts.add(whitePawn);
            nexts.add(whiteKnight);
            nexts.add(whiteBishop);
            nexts.add(whiteRook);
            nexts.add(whiteQueen);
            nexts.add(whiteKing);
            nexts.add(blackPawn);
            nexts.add(blackKnight);
            nexts.add(blackBishop);
            nexts.add(blackRook);
            nexts.add(blackQueen);
            nexts.add(blackKing);

        }

        return nexts;
    }

    //endregion

    //endregion

}
