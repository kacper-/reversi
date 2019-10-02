package com.km.game;

class Tuning {
    static final long SIM_TIME = 1000;
    static final int SIM_COUNT_L1 = 10000;
    static final int SIM_COUNT_L2 = 2500;
    static final int SIM_COUNT_L3 = 200;
    static final int SIM_COUNT_L4 = 10;
    static final int SIM_L2 = 53;
    static final int SIM_L3 = 57;
    static final int SIM_L4 = 60;
    static final int SIM_HB = 100;
    static final int CORES = Runtime.getRuntime().availableProcessors() < 3 ? 1 : Runtime.getRuntime().availableProcessors() - 2;
}
