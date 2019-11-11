package com.km.nn.dropout;

import com.km.Config;
import com.km.nn.dropout.impl.BooleanDropOut;
import com.km.nn.dropout.impl.DoubleDropOut;
import com.km.nn.dropout.impl.NoDropOut;

public class DropOutFactory {
    public static DropOut createInstance() {
        switch (Config.getDropOutFunction()) {
            case NONE:
                return new NoDropOut();
            case DOUBLE:
                return new DoubleDropOut();
            case BOOLEAN:
                return new BooleanDropOut();
        }
        return null;
    }
}
