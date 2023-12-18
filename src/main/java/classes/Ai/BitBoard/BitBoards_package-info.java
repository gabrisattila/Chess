package classes.Ai.BitBoard;

//This package contains a fully new and very efficient implementation of making moves
// and create boards in the head of the AI.
//The new implementation called BitBoards. We represent the chessboard in a long which contains 64 bits.
//A separate long belongs to each piece type from both colors. (One board for white pawns, for black ones...)
//The moves implemented with bitwise operations and a moveDocString belongs to every move.
//A move doc string looks like this: G-1-7-and-stuff_
//The first character is the piece type (UpperCase if it's white, else black)
//The second character is the start index, where the piece starts,
//The third is the end index, where the piece goes
//The next ones aren't necessary. Only needed if mentioned changes occurred.
//The fourth can be k or K if the move disturbed the king side castle
//The fifth can be q or Q if the move disturbed the queen side castle
// (color depends on that the letter is capital or not) for the last two case
//The sixth is an index where shows the index of a pawn that offered itself for emPassant
//And the last is separator
