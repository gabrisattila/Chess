package classes.Ai;

import classes.Game.I18N.Location;
import classes.Game.I18N.Pair;
import classes.Game.Model.Structure.*;
import classes.Game.Model.Structure.BitBoard.BitBoardMoves;
import lombok.*;

import java.util.ArrayList;
import java.util.Random;

import static classes.Ai.AiNode.*;
import static classes.Ai.AiTree.*;
import static classes.Ai.FenConverter.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.Model.Logic.EDT.*;
import static classes.Game.Model.Structure.BitBoard.BBVars.*;
import static classes.Game.Model.Structure.BitBoard.BitBoardMoves.moveAPieceOnBoard;
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

    public void Move()  {
//        moveWithSimpleAi();
        moveWithMiniMaxAi();
    }

    public void moveWithSimpleAi() {
        synchronized (pauseFlag){

            waitOnPause();

            double gameOver = GameOverDecision(getBoard(), false, Double.MIN_VALUE);
            if (gameFinished(gameOver))
                return;

            Random random = new Random();
            Piece stepper;
            ArrayList<Piece> possibleSteppers = new ArrayList<>();
            for (IPiece p : getBoard().myPieces()) {
                if (!p.getPossibleRange().isEmpty()){
                    possibleSteppers.add((Piece) p);
                }
            }
            stepper = possibleSteppers.get(random.nextInt(0, possibleSteppers.size()));


            ArrayList<Location> ableToStepThereIn = new ArrayList<>(stepper.getPossibleRange());
            int indexOfChosen = random.nextInt(0, ableToStepThereIn.size());
            Location toStepOn = ableToStepThereIn.get(indexOfChosen);
            Pair<Integer, Location> kingCheck = kingAliveCheckRandomMove(indexOfChosen, toStepOn, ableToStepThereIn, random);
            if (kingCheck.getFirst() != indexOfChosen){
                toStepOn = kingCheck.getSecond();
            }

            Move move = new Move(stepper, toStepOn, getBoard());
            move.setMustLogged(true);
            Step(move);
        }
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

        double gameOver = GameOverDecision(getBoard(), true, Double.MIN_VALUE);
        if (!gameFinished(gameOver)) {
            setUpBitBoard(BoardToFen(getBoard()));
            AiNode tree = new AiNode(BoardToAiFen(getBoard()));

//            double bestChildValue = negaMax(tree, MINIMAX_DEPTH, -Double.MAX_VALUE, Double.MAX_VALUE);
            int startTime = (int) System.currentTimeMillis();
//            double bestChildValue = miniMax(tree, 0, whiteToPlay, -Double.MAX_VALUE, Double.MAX_VALUE);
            double bestChildValue = miniMaxWithBitBoards(tree, 0, whiteToPlay, -Double.MAX_VALUE, Double.MAX_VALUE, -1,
                    whiteSmallCastleEnabled, whiteBigCastleEnabled, blackSmallCastleEnabled, blackBigCastleEnabled,
                    whitePawn, whiteBishop, whiteKnight, whiteRook, whiteQueen, whiteKing,
                    blackPawn, blackBishop, blackKnight, blackRook, blackQueen, blackKing);
            int endTime = (int) System.currentTimeMillis();
            System.out.println(endTime - startTime);

            AiNode bestChild = sortOutBestChild(tree, bestChildValue);
//            getLogger().log(bestChild.getFen().split(" ")[6]);
            AiFenToBoard(bestChild.getFen(), getBoard());
            addToHappenedList(AiFenToFen(bestChild.getFen()));
        }
    }


    private double miniMaxWithBitBoards(
            AiNode starterPos, int depth, boolean maxNeeded, double alpha, double beta,
            int emPassant, boolean wKC, boolean wQC, boolean bKC, boolean bQC,
            long whitePawn, long whiteBishop, long whiteKnight, long whiteRook, long whiteKing, long whiteQueen,
            long blackPawn, long blackBishop, long blackKnight, long blackRook, long blackKing, long blackQueen
    ){
        synchronized (pauseFlag){
            waitOnPause();
            if (depth == MINIMAX_DEPTH){
                int pawnPoint = (int) (PAWN_BASE_VALUE * Long.bitCount(whitePawn) - PAWN_BASE_VALUE * Long.bitCount(blackPawn));
                int knightPoint = (int) (KNIGHT_OR_BISHOP_BASE_VALUE * Long.bitCount(whiteKnight)
                                        - KNIGHT_OR_BISHOP_BASE_VALUE * Long.bitCount(blackKnight));
                int bishopPoint = (int) (KNIGHT_OR_BISHOP_BASE_VALUE * Long.bitCount(whiteBishop)
                                        - KNIGHT_OR_BISHOP_BASE_VALUE * Long.bitCount(blackBishop));
                int rookPoint = (int) (ROOK_BASE_VALUE * Long.bitCount(whiteRook) - ROOK_BASE_VALUE * Long.bitCount(blackRook));
                int queenPoint = (int) (QUEEN_BASE_VALUE * Long.bitCount(whiteQueen) - QUEEN_BASE_VALUE * Long.bitCount(blackQueen));
                return pawnPoint + knightPoint + bishopPoint + rookPoint + queenPoint;
            }

            String possibleMoves = BitBoardMoves.possibleMoves(   
                    maxNeeded, emPassant, wKC,  wQC,  bKC,  bQC,
                    whitePawn,  whiteBishop,  whiteKnight,  whiteRook,  whiteKing,  whiteQueen,
                    blackPawn,  blackBishop,  blackKnight,  blackRook,  blackKing,  blackQueen
            );
            String[] possibleMovesList = possibleMoves.split("_");
            long nextWhitePawn,  nextWhiteBishop,  nextWhiteKnight,  nextWhiteRook,  nextWhiteKing,  nextWhiteQueen,
                    nextBlackPawn,  nextBlackBishop,  nextBlackKnight,  nextBlackRook,  nextBlackKing,  nextBlackQueen;
            if (maxNeeded){
                double possibleMax = -Double.MAX_VALUE;
                for (String move : possibleMovesList) {
                    nextWhitePawn = moveAPieceOnBoard(move, whitePawn);
                    nextWhiteBishop = moveAPieceOnBoard(move, whiteBishop);
                    nextWhiteKnight = moveAPieceOnBoard(move, whiteKnight);
                    nextWhiteRook = moveAPieceOnBoard(move, whiteRook);
                    nextWhiteQueen = moveAPieceOnBoard(move, whiteQueen);
                    nextWhiteKing = moveAPieceOnBoard(move, whiteKing);
                    nextBlackPawn = moveAPieceOnBoard(move, blackPawn);
                    nextBlackBishop = moveAPieceOnBoard(move, blackBishop);
                    nextBlackKnight = moveAPieceOnBoard(move, blackKnight);
                    nextBlackRook = moveAPieceOnBoard(move, blackRook);
                    nextBlackQueen = moveAPieceOnBoard(move, blackQueen);
                    nextBlackKing = moveAPieceOnBoard(move, blackKing);
                    boolean newWKC = wKC, newWQC = wQC, newBKC = bKC, newBQC = bQC;
                    if (move.split("-").length > 3 && Character.isLetter(move.split("-")[3].charAt(0))){
                        String castleModification = move.split("-")[3];
                        if (castleModification.length() == 1){
                            switch (castleModification){
                                case "K" -> newWKC = false;  
                                case "Q" -> newWQC = false;
                                case "k" -> newBKC = false;
                                case "q" -> newBQC = false;
                            }
                        }else {
                            if (Character.isUpperCase(castleModification.charAt(0))){
                                newWKC = false;
                                newWQC = false;
                            }
                        }
                    }
                    
                    double evaluationOfThisNode = miniMaxWithBitBoards(
                            new AiNode(), depth++, false, alpha, beta, 0,
                            newWKC, newWQC, newBKC, newBQC,
                            nextWhitePawn,  nextWhiteBishop,  nextWhiteKnight,  nextWhiteRook,  nextWhiteKing,  nextWhiteQueen,
                            nextBlackPawn,  nextBlackBishop,  nextBlackKnight,  nextBlackRook,  nextBlackKing,  nextBlackQueen
                    );
                    possibleMax = Math.max(evaluationOfThisNode, possibleMax);
                    alpha = Math.max(alpha, evaluationOfThisNode);
                    if (beta <= alpha)
                        break;
                }
                starterPos.setFinalValue(possibleMax);
                return possibleMax;
            }else {
                double possibleMin = Double.MAX_VALUE;
                for (String move : possibleMovesList) {
                    nextWhitePawn = moveAPieceOnBoard(move, whitePawn);
                    nextWhiteBishop = moveAPieceOnBoard(move, whiteBishop);
                    nextWhiteKnight = moveAPieceOnBoard(move, whiteKnight);
                    nextWhiteRook = moveAPieceOnBoard(move, whiteRook);
                    nextWhiteQueen = moveAPieceOnBoard(move, whiteQueen);
                    nextWhiteKing = moveAPieceOnBoard(move, whiteKing);
                    nextBlackPawn = moveAPieceOnBoard(move, blackPawn);
                    nextBlackBishop = moveAPieceOnBoard(move, blackBishop);
                    nextBlackKnight = moveAPieceOnBoard(move, blackKnight);
                    nextBlackRook = moveAPieceOnBoard(move, blackRook);
                    nextBlackQueen = moveAPieceOnBoard(move, blackQueen);
                    nextBlackKing = moveAPieceOnBoard(move, blackKing);
                    boolean newWKC = wKC, newWQC = wQC, newBKC = bKC, newBQC = bQC;
                    if (move.split("-").length > 3 && Character.isLetter(move.split("-")[3].charAt(0))){
                        String castleModification = move.split("-")[3];
                        if (castleModification.length() == 1){
                            switch (castleModification){
                                case "K" -> newWKC = false;
                                case "Q" -> newWQC = false;
                                case "k" -> newBKC = false;
                                case "q" -> newBQC = false;
                            }
                        }else {
                            if (Character.isUpperCase(castleModification.charAt(0))){
                                newWKC = false;
                                newWQC = false;
                            }
                        }
                    }
                    double evaluationOfThisNode = miniMaxWithBitBoards(
                            new AiNode(), depth++, true, alpha, beta, 0,
                            newWKC, newWQC, newBKC, newBQC,
                            nextWhitePawn,  nextWhiteBishop,  nextWhiteKnight,  nextWhiteRook,  nextWhiteKing,  nextWhiteQueen,
                            nextBlackPawn,  nextBlackBishop,  nextBlackKnight,  nextBlackRook,  nextBlackKing,  nextBlackQueen
                    );
                    possibleMin = Math.min(evaluationOfThisNode, possibleMin);
                    beta = Math.min(alpha, evaluationOfThisNode);
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

    public static boolean itWorthToGiveUp(){

        double enemyPiecesValueSum = getBoard().getPieces(!whiteToPlay).stream().mapToDouble(p -> ((Piece) p).getVALUE()).sum();
        double myPiecesValueSum = getBoard().getPieces(whiteToPlay).stream().mapToDouble(p -> ((Piece) p).getVALUE()).sum();

        return Math.abs(enemyPiecesValueSum + myPiecesValueSum) > ROOK_BASE_VALUE + KNIGHT_OR_BISHOP_BASE_VALUE &&
                                        getBoard().getPieces(whiteToPlay).stream().allMatch(p -> p.getType() == K || p.getType() == P);
    }

    public static boolean itWorthToOfferOrRecommendDraw(){
        if (getBoard().getPieces().size() == 3 && getBoard().hasTwoKings() &&
                getBoard().getPieces().stream().allMatch(p -> p.getType() == K || p.getType() == P)){
            IPiece onlyPawn = null;
            IPiece enemyKing = null;
            for (IPiece p : getBoard().getPieces()) {
                if (p.getType() == P){
                    onlyPawn = p;
                }
            }
            assert onlyPawn != null;
            enemyKing = getBoard().getKing(!onlyPawn.isWhite());
            if (onlyPawn.getJ() != 0 || onlyPawn.getJ() != MAX_WIDTH - 1){
                return false;
            }
            int pawnDistance = Math.abs(onlyPawn.getJ() - onlyPawn.getEnemyStartRow());
            int kingDistance = Math.max(Math.abs(enemyKing.getJ() - onlyPawn.getJ()), Math.abs(enemyKing.getI() - onlyPawn.getEnemyStartRow()));
            return !(kingDistance < pawnDistance);
        }
        return getBoard().hasTwoKings() && getBoard().getPieces().size() == 4 &&
                getBoard().getPieces().stream().allMatch(p -> p.getType() == K || p.getType() == N);
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
