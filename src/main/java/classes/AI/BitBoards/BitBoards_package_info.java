package classes.AI.BitBoards;


//      binary move bits
//
// 0000 0000 0000 0000 0011 1111    source square       Because we can represent 63 squares in 6 bits
// 0000 0000 0000 1111 1100 0000    target square       ------------------- || ----------------------
// 0000 0000 1111 0000 0000 0000    piece               Because we can represent 12 piece whitePawn (0000) - blackKing 1111
// 0000 1111 0000 0000 0000 0000    promoted piece      ----------------------- || ----------------------
// 0001 0000 0000 0000 0000 0000    capture flag        Is move was capture
// 0010 0000 0000 0000 0000 0000    check flag          Is move check
// 0100 0000 0000 0000 0000 0000    emPassant flag      Is move was emPassant
// 1000 0000 0000 0000 0000 0000    castling flag       Is move was castling
//
// Decoding helpers in order:
// 0x3f
// 0xfc0
// 0xf000
// 0xf0000
// 0x100000
// 0x200000
// 0x400000
// 0x800000


//TODO :
// Zobrist törlése, úgy ahogy van
// Tesztelés
// - először legfontosabb régi függvények kiválogatása update range összevetése generateMove-al
// - sima fontos régi függvények
// - régi függvények
// - bitBoard tesztelés
// - bitBoard és range updater vica verza tesztelés
// - Könyebb két lépéses mattokat megtalálja - e? Tesztként
