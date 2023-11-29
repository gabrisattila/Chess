package classes;

public class TODO {

    //TODO
    // EmPassant, Sánc, Feladás, Döntetlen, hogyan legyen megoldva,
    // hogy különböző ágakon ezeknek különböző értékeik vannak.
    // ??????????????????????????

    //TODO Feladás és döntetlen gombok, illetve feladás és döntetlen AI választás átgondolni.
    // Tehát amiket kell tudnia ezeknek a gomboknak:
    //      - Feladás esetén, illetve matt esetén is összes figura possibleRange.clear() véglegesen!! Ne lehessen frissíteni.
    //      - Ezt elérni a gameEndFlag manipulációjával. Egész pontosan, az rangeUpdater attól függővétételével.
    //      - Ez nyilván akkor, ha a player lépi, vagy az AI által utoljára kihozott lépés az.
    //      - AI esetén feladási lehetőségek:
    //                                       - egész belátható részfa minden ágán matt látható előre.
    //                                       - Az ellenfélnek 1 vezér előnye van olyankor,
    //                                          mikor nekem (AI) csupán királyom esetleg 1-2 gyalogom van.
    //      - Döntetlen:
    //                  - 3 lépéses döntetlen -> eddigiek eltárolása.
    //                  - Player Ajánlás -> ha player ajánlja az AI elfogadásra akkor adja a kezét, ha már feladás közelben van.
    //                          (Random generátor 1 a 10-hez eséllyel fogadja el), 1 szélső gyalogos döntetlen eset
    //                  - AI Ajánlás -> 1 szélső gyalogos döntetlen eset, ha van más tipikus döntetlen eset.

    //TODO EmPassant és Sánc átgondolása, hogy a fenébe is fog működni miniMax esetén

    //TODO Gráfos megoldása a miniMax fának

    //TODO Teszt mode view megfelelő kidolgozása:
    // - Ugorjon fel egy új box a játék típus választó helyén.
    // - Amiből kiválasztjuk a kellő fent
    // - Tűnjön el, majd rajzolódjon újra a GameBoard


}
