package common;

import com.github.jordanpottruff.jgml.Vec;

public class Util {
    public static String vecToString(Vec vec) {
        StringBuilder sb = new StringBuilder("[");
        double[] values = vec.toArray();
        for (int i=0; i<values.length; i++) {
            sb.append(values[i]);
            if (i != values.length-1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
