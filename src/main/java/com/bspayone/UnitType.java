package com.bspayone;


public enum UnitType {
    PIECE(1),
    PACKAGE(2),
    KG(3),
    LITER(4);

    private final int measureType;

    UnitType(int type) {
        this.measureType = type;
    }
}
