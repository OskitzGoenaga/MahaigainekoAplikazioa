package Kontsultak;

public class Baimenak {
    public final boolean ikusi;
    public final boolean txertatu;
    public final boolean eguneratu;
    public final boolean ezabatu;

    public Baimenak(boolean ikusi, boolean txertatu, boolean eguneratu, boolean ezabatu) {
        this.ikusi = ikusi;
        this.txertatu = txertatu;
        this.eguneratu = eguneratu;
        this.ezabatu = ezabatu;
    }

    public static Baimenak bakarrikIkusi() { return new Baimenak(true,false,false,false); }
    public static Baimenak ikusiEtaEzabatu() { return new Baimenak(true,false,false,true); }
    public static Baimenak ikusiEtaTxertatu() { return new Baimenak(true,true,false,false); }
    public static Baimenak ikusiEtaEguneratu() { return new Baimenak(true,false,true,false); }
    public static Baimenak dena() { return new Baimenak(true,true,true,true); }
    public static Baimenak debekatuta() { return new Baimenak(false,false,false,false); }
}
