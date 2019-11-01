package com.km.nn.ac;

import com.km.nn.ac.impl.SoftSign;
// TODO add to config
public class ACFactory {
    public static AC createInstance(ACType type) {
        switch (type) {
            case SOFT_SIGN:
                return new SoftSign();
        }
        return null;
    }
}
