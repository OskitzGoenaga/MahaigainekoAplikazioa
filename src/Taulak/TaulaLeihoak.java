package Taulak;

import Kontsultak.*;
import java.util.Arrays;
import java.util.List;

public class TaulaLeihoak {

    // ------------------ BEZEROAK ------------------
    public static class Bezeroak extends KontsultaOrokorrak {
        public Bezeroak() { super("Bezeroak"); }
        @Override protected String getTaula() { return "bezeroak"; }
        @Override protected List<ZutabeakDef> getZutabeak() {
            return Arrays.asList(
                ZutabeakDef.pkAuto("id"),
                ZutabeakDef.katea("izena"),
                ZutabeakDef.katea("abizena"),
                ZutabeakDef.katea("email"),
                ZutabeakDef.katea("telefonoa"),
                ZutabeakDef.katea("helbidea")
            );
        }
    }

    // ------------------ EROSKETAK ------------------
    public static class Erosketak extends KontsultaOrokorrak {
        public Erosketak() { super("Erosketak"); }
        @Override protected String getTaula() { return "erosketak"; }
        @Override protected List<ZutabeakDef> getZutabeak() {
            return Arrays.asList(
                ZutabeakDef.pkAuto("id"),
                ZutabeakDef.data("data"),
                ZutabeakDef.osoa("kantitatea"),
                ZutabeakDef.osoa("hornitzailea_id"),
                ZutabeakDef.osoa("produktua_id")
            );
        }
    }

    // ------------------ HORNTZAILEAK ------------------
    public static class Hornitzaileak extends KontsultaOrokorrak {
        public Hornitzaileak() { super("Hornitzaileak"); }
        @Override protected String getTaula() { return "hornitzaileak"; }
        @Override protected List<ZutabeakDef> getZutabeak() {
            return Arrays.asList(
                ZutabeakDef.pkAuto("id"),
                ZutabeakDef.katea("enpresa"),
                ZutabeakDef.katea("telefonoa"),
                ZutabeakDef.katea("email")
            );
        }
    }

    // ------------------ LANGILEAK ------------------
    public static class Langileak extends KontsultaOrokorrak {
        public Langileak() { super("Langileak"); }
        @Override protected String getTaula() { return "langileak"; }
        @Override protected List<ZutabeakDef> getZutabeak() {
            return Arrays.asList(
                ZutabeakDef.pkAuto("id"),
                ZutabeakDef.katea("izena"),
                ZutabeakDef.katea("abizena"),
                ZutabeakDef.katea("rol"),
                ZutabeakDef.katea("nan"),
                ZutabeakDef.katea("email"),
                ZutabeakDef.katea("pasahitza")
            );
        }
    }

    // ------------------ PRODUKTUAK ------------------
    public static class Produktuak extends KontsultaOrokorrak {
        public Produktuak() { super("Produktuak"); }
        @Override protected String getTaula() { return "produktuak"; }
        @Override protected List<ZutabeakDef> getZutabeak() {
            return Arrays.asList(
                ZutabeakDef.pkAuto("id"),
                ZutabeakDef.katea("mota"),
                ZutabeakDef.katea("izena"),
                ZutabeakDef.dez("prezioa"),
                ZutabeakDef.osoa("stock"),
                ZutabeakDef.katea("argazkia"),
                ZutabeakDef.osoa("hornitzaile_id")
            );
        }
    }

    // ------------------ SALMENTAK ------------------
    public static class Salmentak extends KontsultaOrokorrak {
        public Salmentak() { super("Salmentak"); }
        @Override protected String getTaula() { return "salmentak"; }
        @Override protected List<ZutabeakDef> getZutabeak() {
            return Arrays.asList(
                ZutabeakDef.pkAuto("id"),
                ZutabeakDef.katea("faktura_path")
            );
        }
    }

    // ------------------ SASKIA ------------------
    public static class Saskia extends KontsultaOrokorrak {
        public Saskia() { super("Saskia"); }
        @Override protected String getTaula() { return "saskia"; }
        @Override protected List<ZutabeakDef> getZutabeak() {
            return Arrays.asList(
                ZutabeakDef.pkAuto("id"),
                ZutabeakDef.osoa("kantitatea"),
                ZutabeakDef.data("data"),
                ZutabeakDef.osoa("bezeroa_id"),
                ZutabeakDef.osoa("produktua_id"),
                ZutabeakDef.osoa("salmenta_id")
            );
        }
    }

    // ------------------ ARAZOAK / SOPORTEA ------------------
    public static class Arazoak extends KontsultaOrokorrak {
        public Arazoak() { super("Arazoak (Soportea)"); }
        @Override protected String getTaula() { return "arazoak"; }
        @Override protected List<ZutabeakDef> getZutabeak() {
            return Arrays.asList(
                ZutabeakDef.pkAuto("id"),
                ZutabeakDef.testua("arazoa"),
                ZutabeakDef.osoa("bezeroa_id"),
                ZutabeakDef.osoa("langilea_id")
            );
        }
    }
}