package com.km.nn.ac;

import com.km.Config;
import com.km.nn.ac.impl.*;

public class ACFactory {
    public static AC createInstance() {
        switch (Config.getNetActivationFunction()) {
            case SOFT_SIGN:
                return new SoftSign();
            case SINUSOID:
                return new Sinusoid();
            case ARC_TAN:
                return new ArcTan();
            case LOGISTIC:
                return new Logistic();
            case ISRU:
                return new Isru();
        }
        return null;
    }
}
