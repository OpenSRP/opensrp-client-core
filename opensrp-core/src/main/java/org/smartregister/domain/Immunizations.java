package org.smartregister.domain;

public enum Immunizations implements Displayable {
    bcg("BCG"), opv_0("OPV 0"), hepb_0("HepB 0"), opv_1("OPV 1"), opv_2("OPV 2"), measles(
            "Measles"), pentavalent_1("Pentavalent 1"), pentavalent_2("Pentavalent 2"), opv_3(
            "OPV 3"), pentavalent_3("Pentavalent 3"), je("JE"), mmr("MMR"), dptbooster_1(
            "DPT " + "Booster 1"), opvbooster("OPV Booster"), measlesbooster(
            "Measles Booster"), je_2("JE " + "2"), dptbooster_2("DPT Booster 2");
    private String displayValue;

    Immunizations(String displayValue) {
        this.displayValue = displayValue;
    }

    public static Immunizations value(String name) {
        try {
            return valueOf(name);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String displayValue() {
        return displayValue;
    }
}
