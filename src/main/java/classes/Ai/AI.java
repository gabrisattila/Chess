package classes.Ai;

import classes.Game.I18N.Location;
import classes.Game.I18N.Pair;
import classes.Game.Model.Structure.*;
import classes.Game.Model.Structure.BitBoard.Zobrist;
import lombok.*;

import java.util.ArrayList;
import java.util.Random;

import static classes.Ai.AiNode.*;
import static classes.Ai.AiNodeBBStyle.*;
import static classes.Ai.AiTree.*;
import static classes.Ai.FenConverter.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.Model.Logic.EDT.*;
import static classes.Game.Model.Structure.BitBoard.BBVars.*;
import static classes.Game.Model.Structure.BitBoard.BitBoardMoves.*;
import static classes.Game.Model.Structure.BitBoard.BitBoards.*;
import static classes.Game.Model.Structure.Board.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Structure.GameOverOrPositionEnd.*;
import static classes.Game.Model.Structure.Move.*;

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
    }

    public void Move() {
        moveWithMiniMaxAi();
    }
    
    private boolean gameFinished(){
        return gameEndFlag.get();
    }


    private boolean gameFinished(double gameOver){
        return GAME_OVER_CASES.contains(gameOver);
    }

    private Pair<Integer, Location> kingAliveCheckRandomMove(int indexOfChosen, Location toStepOn, ArrayList<Location> ableToStepThere, Random random)  {
        while (notNull(getBoard().getPiece(toStepOn)) && getBoard().getPiece(toStepOn).getType() == K){
            indexOfChosen = random.nextInt(0, ableToStepThere.size());
            toStepOn = ableToStepThere.get(indexOfChosen);
        }
        return new Pair<>(indexOfChosen, toStepOn);
    }


    //region Mini Max

    private void moveWithMiniMaxAi() {
        convertOneBoardToAnother(getViewBoard(), getBoard());
        GameOverDecision(getBoard(), true, Double.MIN_VALUE);
        if (!gameFinished()) {
            MINIMAX("bitBoard");
        }
    }

    private void MINIMAX(String miniMaxType){
        nodeNum = 0;

        AiNode treeOld;
        AiNode bestChildOld = new AiNode();
        if ("bitBoard".equals(miniMaxType)){

            setUpBitBoard(BoardToFen(getBoard()));

            int emPassant = emPassantToBitBoard(emPassantChance);
            long zKey = Zobrist.getZobristKey(whiteToPlay, emPassant,
                    whiteSmallCastleEnabled, whiteBigCastleEnabled, blackSmallCastleEnabled, blackBigCastleEnabled,
                    whitePawn, whiteBishop, whiteKnight, whiteRook, whiteQueen, whiteKing,
                    blackPawn, blackBishop, blackKnight, blackRook, blackQueen, blackKing);
            AiNodeBBStyle tree = new AiNodeBBStyle(zKey, "");

            int startTime = (int) System.currentTimeMillis();

            double bestChildValue = miniMaxWithBitBoards(
                    tree, 0, whiteToPlay, -Double.MAX_VALUE, Double.MAX_VALUE,
                    emPassant, whiteSmallCastleEnabled, whiteBigCastleEnabled, blackSmallCastleEnabled, blackBigCastleEnabled,
                    whitePawn, whiteBishop, whiteKnight, whiteRook, whiteQueen, whiteKing,
                    blackPawn, blackBishop, blackKnight, blackRook, blackQueen, blackKing);

            printDataOfMiniMax(startTime);

            whiteToPlay = !whiteToPlay;
            Pair<AiNodeBBStyle, String> nodeAndItsFen = sortOutBestChild(tree, bestChildValue);
            AiNodeBBStyle bestChild = nodeAndItsFen.getFirst();
            addToHappenedList(bestChild.getZobristKey());
            FenToBoard(nodeAndItsFen.getSecond(), getBoard());
        }else {
            if ("old".equals(miniMaxType)) {

                int startTime = (int) System.currentTimeMillis();
                treeOld = new AiNode(BoardToAiFen(getBoard()));
                double bestChildValue = miniMax(treeOld, 0, whiteToPlay, -Double.MAX_VALUE, Double.MAX_VALUE);
                printDataOfMiniMax(startTime);
                bestChildOld = sortOutBestChild(treeOld, bestChildValue);

            } else if ("negaMax".equals(miniMaxType)) {

                int startTime = (int) System.currentTimeMillis();
                treeOld = new AiNode(BoardToAiFen(getBoard()));
                double bestChildValue = negaMax(treeOld, MINIMAX_DEPTH, -Double.MAX_VALUE, Double.MAX_VALUE);
                printDataOfMiniMax(startTime);
                bestChildOld = sortOutBestChild(treeOld, bestChildValue);
            }
            AiFenToBoard(bestChildOld.getFen(), getBoard());
            addToHappenedList(AiFenToFen(bestChildOld.getFen()));
        }

    }

    private double miniMaxWithBitBoards(
            AiNodeBBStyle starterPos, int depth, boolean maxNeeded, double alpha, double beta,
            int emPassant, boolean wKC, boolean wQC, boolean bKC, boolean bQC,
            long whitePawn, long whiteBishop, long whiteKnight, long whiteRook, long whiteQueen, long whiteKing,
            long blackPawn, long blackBishop, long blackKnight, long blackRook, long blackQueen, long blackKing
    ){
        synchronized (pauseFlag){
            waitOnPause();
            if (depth == MINIMAX_DEPTH){
                double finalValue = getEvaluationOfThisMove(
                        starterPos.getTheMoveWhatsCreatedIt(),
                        whitePawn, whiteKnight, whiteBishop, whiteRook, whiteQueen,
                        blackPawn, blackKnight, blackBishop, blackRook, blackQueen
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
                                    whitePawn, whiteBishop, whiteKnight, whiteRook, whiteQueen, whiteKing,
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
                        possibleMax = WHITE_GOT_CHECKMATE;
                        evaluationOfTheNode = possibleMax;
                        alpha = evaluationOfTheNode;
                        if (beta <= alpha)
                            break;
                        break;
                    }
                    ArrayList<Object> emPassantAndCastle = emPassantAndCastleCases(legalMove, wKC, wQC, bKC, bQC);
                    AiNodeBBStyle next = putNewToNodeMap(starterPos, legalMove,
                            true, (Integer) emPassantAndCastle.get(0),
                            (boolean) emPassantAndCastle.get(1), (boolean) emPassantAndCastle.get(2),
                            (boolean) emPassantAndCastle.get(3), (boolean) emPassantAndCastle.get(4),
                            nextBoards.get(0), nextBoards.get(1), nextBoards.get(2), nextBoards.get(3), nextBoards.get(4), nextBoards.get(5),
                            nextBoards.get(6), nextBoards.get(7), nextBoards.get(8), nextBoards.get(9), nextBoards.get(10), nextBoards.get(11));
                    nodeNum++;
                    evaluationOfTheNode = miniMaxWithBitBoards(
                            next, depth + 1, false, alpha, beta,
                            (Integer) emPassantAndCastle.get(0),
                            (boolean) emPassantAndCastle.get(1), (boolean) emPassantAndCastle.get(2),
                            (boolean) emPassantAndCastle.get(3), (boolean) emPassantAndCastle.get(4),
                            nextBoards.get(0), nextBoards.get(1), nextBoards.get(2), nextBoards.get(3), nextBoards.get(4), nextBoards.get(5),
                            nextBoards.get(6), nextBoards.get(7), nextBoards.get(8), nextBoards.get(9), nextBoards.get(10), nextBoards.get(11)
                    );
                    possibleMax = Math.max(evaluationOfTheNode, possibleMax);
                    alpha = Math.max(alpha, evaluationOfTheNode);
                    if (beta <= alpha)
                        break;
                }
                
                starterPos.setFinalValue(possibleMax);
                return possibleMax;
            }else {

                double possibleMin = Double.MAX_VALUE;
                double evaluationOfTheNode = BLACK_GOT_CHECKMATE;

                if (legalMoves.isEmpty()){
                    if ((blackKing &
                            unsafeFor(false,
                                    whitePawn, whiteKnight, whiteBishop, whiteRook, whiteQueen, whiteKing,
                                    blackPawn, blackKnight, blackBishop, blackRook, blackQueen, blackKing)) == 0){
                        //DRAW
                        evaluationOfTheNode = DRAW;
                    }
                    possibleMin = evaluationOfTheNode;
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
                        possibleMin = WHITE_GOT_CHECKMATE;
                        evaluationOfTheNode = possibleMin;
                        alpha = evaluationOfTheNode;
                        if (beta <= alpha)
                            break;
                        break;
                    }
                    ArrayList<Object> emPassantAndCastle = emPassantAndCastleCases(legalMove, wKC, wQC, bKC, bQC);
                    AiNodeBBStyle next = putNewToNodeMap(starterPos, legalMove,
                            false, (Integer) emPassantAndCastle.get(0),
                            (boolean) emPassantAndCastle.get(1), (boolean) emPassantAndCastle.get(2),
                            (boolean) emPassantAndCastle.get(3), (boolean) emPassantAndCastle.get(4),
                            nextBoards.get(0), nextBoards.get(1), nextBoards.get(2), nextBoards.get(3), nextBoards.get(4), nextBoards.get(5),
                            nextBoards.get(6), nextBoards.get(7), nextBoards.get(8), nextBoards.get(9), nextBoards.get(10), nextBoards.get(11));
                    nodeNum++;
                    evaluationOfTheNode = miniMaxWithBitBoards(
                            next, depth + 1, true, alpha, beta,
                            (Integer) emPassantAndCastle.get(0),
                            (boolean) emPassantAndCastle.get(1), (boolean) emPassantAndCastle.get(2),
                            (boolean) emPassantAndCastle.get(3), (boolean) emPassantAndCastle.get(4),
                            nextBoards.get(0), nextBoards.get(1), nextBoards.get(2), nextBoards.get(3), nextBoards.get(4), nextBoards.get(5),
                            nextBoards.get(6), nextBoards.get(7), nextBoards.get(8), nextBoards.get(9), nextBoards.get(10), nextBoards.get(11)
                    );
                    possibleMin = Math.min(evaluationOfTheNode, possibleMin);
                    beta = Math.min(alpha, evaluationOfTheNode);
                    if (beta <= alpha)
                        break;
                }
                starterPos.setFinalValue(possibleMin);
                return possibleMin;
            }
        }
    }

    private double miniMax(AiNode starterPos, int depth, boolean maxNeeded, double alpha, double beta) {

        synchronized (pauseFlag){

            waitOnPause();

            double gameOver = GameOverDecision(starterPos, false, Double.MIN_VALUE);

            if (depth == MINIMAX_DEPTH || gameFinished(gameOver)) {
                starterPos.setFinalValue(gameOver);
                return gameOver;
            }

            ArrayList<String> possibilities = starterPos.collectPossibilities(maxNeeded);
            AiNode nextChild;
            if (maxNeeded) {
                double possibleMax = -Double.MAX_VALUE;
                for (String child : possibilities) {

                    nextChild = calcNextAndAddToTree(starterPos, child);

                    nodeNum++;
                    double evaluatedMiniMax = miniMax(nextChild, depth + 1, false, alpha, beta);
                    possibleMax = Math.max(possibleMax, evaluatedMiniMax);
                    alpha = Math.max(alpha, evaluatedMiniMax);
                    if (beta <= alpha)
                        break;
                }
                starterPos.setFinalValue(possibleMax);
                return possibleMax;
            } else {
                double possibleMin = Double.MAX_VALUE;
                for (String child : possibilities) {

                    nextChild = calcNextAndAddToTree(starterPos, child);

                    nodeNum++;
                    double evaluatedMiniMax = miniMax(nextChild, depth + 1, true, alpha, beta);
                    possibleMin = Math.min(possibleMin, evaluatedMiniMax);
                    beta = Math.min(beta, evaluatedMiniMax);
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
        System.out.println("Minimax evaluated "+ nodeNum + " nodes.");
        int elapsedTime = endTime - startTime;
        System.out.println("Under " + ((double) elapsedTime / 1000) + " seconds.");
        double nps =((double) nodeNum / ((double) elapsedTime / 1000));
        System.out.println("That means the effectiveness is: " + nps + " node / sec.");
        System.out.println(transPosNum + " transposition happened.");
        System.out.println();
    }

    private AiNode sortOutBestChild(AiNode tree, double bestChildValue){
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
        emPassantChance = bestChild.getFen().split(" ")[3];
        castleCaseFenToBoard(bestChild.getFen().split(" ")[2]);
        return bestChild;
    }

    private Pair<AiNodeBBStyle, String> sortOutBestChild(AiNodeBBStyle tree, double bestChildValue){
        ArrayList<AiNodeBBStyle> bestChildren = new ArrayList<>();
        for (AiNodeBBStyle child : tree.getChildren()) {
            if (child.getFinalValue() == bestChildValue) {
                bestChildren.add(child);
            }
        }
        AiNodeBBStyle bestChild;
        if (bestChildren.size() == 1) {
            bestChild = bestChildren.get(0);
        } else {
            Random random = new Random();
            int randomChosenBestIndex = random.nextInt(0, bestChildren.size());
            bestChild = bestChildren.get(randomChosenBestIndex);
        }
        String fen = aiNodeBBToFen(bestChild);
        emPassantChance = fen.split(" ")[3];
        castleCaseFenToBoard(fen.split(" ")[2]);
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
        /*
        switch (legalMove.charAt(0)){
            case 'P' ->
            case 'N' ->
            case 'B' ->
            case 'R' ->
            case 'Q' ->
            case 'K' ->
            case 'p' ->
            case 'n' ->
            case 'b' ->
            case 'r' ->
            case 'q' ->
            case 'k' ->
        }
        if (neededBoardsForTheMove.length() > 1){
            if (neededBoardsForTheMove.length() == 2){
                switch (neededBoardsForTheMove.charAt(1)){
                    case 'P' -> whitePawn = moveAPieceOnBoard(legalMove, whitePawn, "P");
                    case 'N' -> whiteKnight = moveAPieceOnBoard(legalMove, whiteKnight, "N");
                    case 'B' -> whiteBishop = moveAPieceOnBoard(legalMove, whiteBishop, "B");
                    case 'R' -> whiteRook = moveAPieceOnBoard(legalMove, whiteRook, "R");
                    case 'Q' -> whiteQueen = moveAPieceOnBoard(legalMove, whiteQueen, "Q");
                    case 'K' -> whiteKing = moveAPieceOnBoard(legalMove, whiteKing, "K");
                    case 'p' -> blackPawn = moveAPieceOnBoard(legalMove, blackPawn, "p");
                    case 'n' -> blackKnight = moveAPieceOnBoard(legalMove, blackKnight, "n");
                    case 'b' -> blackBishop = moveAPieceOnBoard(legalMove, blackBishop, "b");
                    case 'r' -> blackRook = moveAPieceOnBoard(legalMove, blackRook, "r");
                    case 'q' -> blackQueen = moveAPieceOnBoard(legalMove, blackQueen, "q");
                    case 'k' -> blackKing = moveAPieceOnBoard(legalMove, blackKing, "k");
                }
            }else {
                //Only Pawn Promotion by capture
                switch (neededBoardsForTheMove.charAt(1)){
                    case 'Q' -> whiteQueen = moveAPieceOnBoard(legalMove, whiteQueen, "Q");
                    case 'q' -> blackQueen = moveAPieceOnBoard(legalMove, blackQueen, "q");
                }
                switch (neededBoardsForTheMove.charAt(2)){
                    case 'P' -> whitePawn = moveAPieceOnBoard(legalMove, whitePawn, "P");
                    case 'N' -> whiteKnight = moveAPieceOnBoard(legalMove, whiteKnight, "N");
                    case 'B' -> whiteBishop = moveAPieceOnBoard(legalMove, whiteBishop, "B");
                    case 'R' -> whiteRook = moveAPieceOnBoard(legalMove, whiteRook, "R");
                    case 'Q' -> whiteQueen = moveAPieceOnBoard(legalMove, whiteQueen, "Q");
                    case 'p' -> blackPawn = moveAPieceOnBoard(legalMove, blackPawn, "p");
                    case 'n' -> blackKnight = moveAPieceOnBoard(legalMove, blackKnight, "n");
                    case 'b' -> blackBishop = moveAPieceOnBoard(legalMove, blackBishop, "b");
                    case 'r' -> blackRook = moveAPieceOnBoard(legalMove, blackRook, "r");
                    case 'q' -> blackQueen = moveAPieceOnBoard(legalMove, blackQueen, "q");
                }
            }
        }*/


         return getLongs(nexts, Character.isUpperCase(legalMove.charAt(0)),
                whitePawn, whiteKnight, whiteBishop, whiteRook, whiteQueen, whiteKing,
                blackPawn, blackKnight, blackBishop, blackRook, blackQueen, blackKing);
    }

    private static String decideWhichBoardsWillBeUsed(String move){
        String[] moveParts = move.split("-");
        String neededBoards = moveParts[0];

        if (moveParts.length == 4){
            if (moveParts[3].charAt(0) == 'C' || moveParts[3].charAt(0) == 'P' || moveParts[3].charAt(0) == 'p'){
                neededBoards += moveParts[3].charAt(1);
            } else if ("K".equals(moveParts[0]) || "k".equals(moveParts[0])) {
                if (Math.abs(Integer.parseInt(moveParts[1]) - Integer.parseInt(moveParts[2])) == 2){
                    neededBoards += "K".equals(moveParts[0]) ? "R" : "r";
                }
            } else if (!Character.isDigit(moveParts[3].charAt(0))) {
                neededBoards += moveParts[3];
            }
        } else if (moveParts.length == 5) {
            if (moveParts[3].charAt(0) == 'P' || moveParts[3].charAt(0) == 'p'){
                neededBoards += moveParts[3].charAt(1);
            }
            neededBoards += moveParts[4].charAt(1);
        }

        return neededBoards;
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

    //NegaMax
    private double negaMax(AiNode starterPos, int depth, double alpha, double beta) {

        synchronized (pauseFlag){

            waitOnPause();

            double gameEnd = GameOverDecision(starterPos, false, Double.MIN_VALUE);
            if (depth == 0 || gameFinished(gameEnd)){
                starterPos.setFinalValue(gameEnd);
                return gameEnd;
            }

            ArrayList<String> possibilities = starterPos.collectPossibilities(false);

            AiNode nextChild;
            for (String child : possibilities) {

                nextChild = calcNextAndAddToTree(starterPos, child);
                starterPos.getChildren().add(nextChild);

                double evaluation = -negaMax(nextChild, depth - 1, -beta, -alpha);

                if (evaluation >= beta){
                    break;
                }

                alpha = Math.max(alpha, evaluation);

            }

            starterPos.setFinalValue(alpha);
            return alpha;
        }

    }

    //endregion

    //endregion

}
