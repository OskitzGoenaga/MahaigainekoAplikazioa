package Kontsultak;

public class RolBaimenak {

    /**
     * Rola eta taularen arabera gauz batzuk erakutsi
     * Taula izenak: bezeroak, erosketak, hornitzaileak, langileak, produktuak, salmentak, arazoak
     * Rolak: langilea, arduraduna, kudeatzailea
     */
    public static Baimenak of(String rola, String taula) {
        String r = rola == null ? "" : rola.trim().toLowerCase();
        String t = taula == null ? "" : taula.trim().toLowerCase();

        switch (r) {
            case "langilea":
                return baimenakLangilea(t);
            case "arduraduna":
                return baimenakArduraduna(t);
            case "kudeatzailea":
                return baimenakKudeatzailea(t);
            default:
                return Baimenak.debekatuta();
        }
    }

    private static Baimenak baimenakLangilea(String t) {
        return switch (t) {
            case "bezeroak"     -> Baimenak.dena();               
            case "erosketak"    -> Baimenak.bakarrikIkusi();      
            case "produktuak"   -> Baimenak.dena();               
            case "salmentak"    -> Baimenak.bakarrikIkusi();      
            case "arazoak"      -> Baimenak.ikusiEtaEzabatu();    
            default             -> Baimenak.debekatuta();
        };
    }

    private static Baimenak baimenakArduraduna(String t) {
        return switch (t) {
            case "bezeroak"     -> Baimenak.dena();              
            case "erosketak"    -> Baimenak.ikusiEtaTxertatu();   
            case "produktuak"   -> Baimenak.dena();
            case "salmentak"    -> Baimenak.bakarrikIkusi();
            case "arazoak"      -> Baimenak.ikusiEtaEzabatu();
            case "hornitzaileak"-> Baimenak.dena();             
            case "langileak"    -> Baimenak.ikusiEtaEguneratu();  
            default             -> Baimenak.debekatuta();
        };
    }

    private static Baimenak baimenakKudeatzailea(String t) {

        return switch (t) {
            case "langileak"    -> Baimenak.dena();               
            default             -> baimenakArduraduna(t);
        };
    }
}