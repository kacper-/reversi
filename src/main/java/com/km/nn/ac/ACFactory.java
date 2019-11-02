package com.km.nn.ac;

import com.km.Config;
import com.km.nn.ac.impl.ArcTan;
import com.km.nn.ac.impl.Logistic;
import com.km.nn.ac.impl.Sinusoid;
import com.km.nn.ac.impl.SoftSign;

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
        }
        return null;
    }
}
