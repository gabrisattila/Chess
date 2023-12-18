package classes.Ai.BitBoards;

import classes.Game.I18N.Pair;
import lombok.*;

import java.security.SecureRandom;
import java.util.HashMap;

import static classes.Ai.BitBoards.BBVars.*;

@Getter
@Setter
public class Zobrist {

    public static long random64() {
        SecureRandom random = new SecureRandom();
        long rand = random.nextLong();
        while(ZOBRIST_KEYS.contains(rand)) {
            rand = random.nextLong();
        }
        return rand;
    }

    public static void fillZobristTable(){
        for (int i = 0; i < 64; i++) {
            ZOBRIST_KEY_FIELD_LIST.add(fillAFieldWithZKeys().getFirst());
            INVERSE_ZOBRIST_FIELD_LIST.add(fillAFieldWithZKeys().getSecond());
        }


        for (int i = 0; i < 4; i++) {
            ZOBRIST_CASTLE_LIST[i] = random64();
        }

        ZOBRIST_WHITE_BLACK_TO_PLAY[0] = random64();
        ZOBRIST_WHITE_BLACK_TO_PLAY[1] = random64();
    }

    private static Pair<HashMap<Character, Long>, HashMap<Long, Character>> fillAFieldWithZKeys(){

        HashMap<Character, Long> fieldList = new HashMap<>();
        HashMap<Long, Character> inverseFieldList = new HashMap<>();

        long random = 0;

        for (char c : englishPieceLetters) {
            random64();
            fieldList.put(c, random);
            inverseFieldList.put(random, c);
        }

        //For emPassant
        random = random64();
        fieldList.put('E', random);
        inverseFieldList.put(random, 'E');

        return new Pair<>(fieldList, inverseFieldList);
    }

    public static long getZobristKey(boolean forWhite, int emPassant,
                                     boolean wKC, boolean wQC, boolean bKC, boolean bQC,
                                     long WP, long WN, long WB, long WR, long WQ, long WK,
                                     long BP, long BN, long BB, long BR, long BQ, long BK){
        long finalZKey = forWhite ? ZOBRIST_WHITE_BLACK_TO_PLAY[0] : ZOBRIST_WHITE_BLACK_TO_PLAY[1];
        for (int i = 0; i < 64; i++) {
            if ((1L << i & WP) != 0){
                finalZKey ^= ZOBRIST_KEY_FIELD_LIST.get(i).get('P');
            }
            if ((1L << i & WN) != 0){
                finalZKey ^= ZOBRIST_KEY_FIELD_LIST.get(i).get('N');
            }
            if ((1L << i & WB) != 0){
                finalZKey ^= ZOBRIST_KEY_FIELD_LIST.get(i).get('B');
            }
            if ((1L << i & WR) != 0){
                finalZKey ^= ZOBRIST_KEY_FIELD_LIST.get(i).get('R');
            }
            if ((1L << i & WQ) != 0){
                finalZKey ^= ZOBRIST_KEY_FIELD_LIST.get(i).get('Q');
            }
            if ((1L << i & WK) != 0){
                finalZKey ^= ZOBRIST_KEY_FIELD_LIST.get(i).get('K');
            }
            if ((1L << i & BP) != 0){
                finalZKey ^= ZOBRIST_KEY_FIELD_LIST.get(i).get('p');
            }
            if ((1L << i & BN) != 0){
                finalZKey ^= ZOBRIST_KEY_FIELD_LIST.get(i).get('n');
            }
            if ((1L << i & BB) != 0){
                finalZKey ^= ZOBRIST_KEY_FIELD_LIST.get(i).get('b');
            }
            if ((1L << i & BR) != 0){
                finalZKey ^= ZOBRIST_KEY_FIELD_LIST.get(i).get('r');
            }
            if ((1L << i & BQ) != 0){
                finalZKey ^= ZOBRIST_KEY_FIELD_LIST.get(i).get('q');
            }
            if ((1L << i & BK) != 0){
                finalZKey ^= ZOBRIST_KEY_FIELD_LIST.get(i).get('k');
            }
            if (emPassant == i){
                finalZKey ^= ZOBRIST_KEY_FIELD_LIST.get(i).get('E');
            }
            if (wKC)
                finalZKey ^= ZOBRIST_CASTLE_LIST[0];
            if (wQC)
                finalZKey ^= ZOBRIST_CASTLE_LIST[1];
            if (bKC)
                finalZKey ^= ZOBRIST_CASTLE_LIST[2];
            if (bQC)
                finalZKey ^= ZOBRIST_CASTLE_LIST[3];
        }
        return finalZKey;
    }

}
