package de.orbit.ToB;

public class ToBMath {

    /**
     * <p>
     *    A simple method to do summation.
     * </p>
     *
     * @param i The lower border.
     * @param n The upper border.
     *
     * @return The value.
     */
    public static int summation(int i, int n) {

        assert i > 0 && n > i;

        //--- if the lower border is 1, we don't have to subtract the lower "area"
        if(i == 1) {
            return ((n * (n + 1)) / 2);
        }

        return ((n * (n + 1)) / 2) - (((i - 1) * ((i - 1) + 1) / 2));

    }

}
