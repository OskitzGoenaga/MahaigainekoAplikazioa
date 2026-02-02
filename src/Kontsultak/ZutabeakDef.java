package Kontsultak;

public class ZutabeakDef {

    public enum DatuMota { TESTUA, KATEA, DATA, OSOA, DEZIMALA }

    public final String izena;
    public final DatuMota mota;
    public final boolean gakoNagusia;
    public final boolean autoGehiketa;

    public ZutabeakDef(String izena, DatuMota mota, boolean gakoNagusia, boolean autoGehiketa) {
        this.izena = izena;
        this.mota = mota;
        this.gakoNagusia = gakoNagusia;
        this.autoGehiketa = autoGehiketa;
    }

    public static ZutabeakDef pkAuto(String izena) { return new ZutabeakDef(izena, DatuMota.OSOA, true, true); }
    public static ZutabeakDef katea(String izena)  { return new ZutabeakDef(izena, DatuMota.KATEA, false, false); }
    public static ZutabeakDef testua(String izena) { return new ZutabeakDef(izena, DatuMota.TESTUA,false,false); }
    public static ZutabeakDef data(String izena)   { return new ZutabeakDef(izena, DatuMota.DATA,  false,false); }
    public static ZutabeakDef osoa(String izena)   { return new ZutabeakDef(izena, DatuMota.OSOA,  false,false); }
    public static ZutabeakDef dez(String izena)    { return new ZutabeakDef(izena, DatuMota.DEZIMALA,false,false); }
}