import javafx.util.Pair;

import java.math.BigInteger;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        new Main().exec();
    }

    void exec() {

        /**
         * Get a message for encryption
         */
        Scanner scanner = new Scanner(System.in);
        BigInteger m = new BigInteger(String.valueOf(scanner.nextInt()));

        /**
         * Get random number p more than length of m
         */
        BigInteger p = randPrime(m);

        /**
         * Get random number g primitive root modulo p.
         */
        BigInteger g = getPRoot(p);

        int voters = 5; //number of voters

        BigInteger[] arrX = new BigInteger[voters]; //secret key array
        BigInteger[] arrY = new BigInteger[voters]; //public key array

        /**
         * Generating keys for each voter
         */
        for (int i = 0; i < voters; i++) {
            System.out.println("  ");

            arrX[i] = rand(p); // Secret key for Ti
            arrY[i] = pow(g, arrX[i]).mod(p); // Public key for Ti
        }

        /**
         * General Public key Y
         */
        BigInteger Y = BigInteger.ONE;

        for (int i = 0; i < voters; i++) {
            Y = Y.multiply(arrY[i]);
        }

        Pair<BigInteger, BigInteger> pair = encrypt(m, g, Y, p);

        /**
         * Each voter calculates di
         */
        BigInteger[] arrD = new BigInteger[voters];

        for (int i = 0; i < voters; i++) {
            arrD[i] = pow(pair.getKey(), arrX[i]).mod(p);
        }

        /**
         * Calculated multiplication of arrD[i]
         */
        BigInteger D = BigInteger.ONE;

        for (int i = 0; i < voters; i++) {
            D = D.multiply(arrD[i]);
        }

        /**
         * Decrypt
         */
        System.out.println("Message decrypted as " + decrypt(pair, D, p));

    }

    /**
     * Decrypt message
     *
     * @param pair pair of keys (a,b)
     * @param D    decryption factor
     * @param p    module
     * @return decrypted message
     */
    private BigInteger decrypt(Pair<BigInteger, BigInteger> pair, BigInteger D, BigInteger p) {
        return ((pair.getValue()
                .multiply(getMod(pow(D, BigInteger.ONE), p)))
                .mod(p) );
    }


    /**
     * Get random prime BigInteger from (m,m+1000)
     * @param m low border
     * @return random prime BigInteger
     */
    private BigInteger randPrime(BigInteger m) {
        BigInteger number = rand2(m);
        while (!number.isProbablePrime(100)) {
            number = rand2(m);
        }
        return number;
    }


    /**
     * Get random number from m to max (m+1000)
     *
     * @param m low border
     * @return rand BigInteger (m,m+1000)
     */
    private BigInteger rand2(BigInteger m) {
        Random r = new Random();
        int low = m.intValue();
        int max = m.intValue() + 1000;
        int high = max;
        return new BigInteger(String.valueOf(r.nextInt(high - low) + low));
    }


    /**
     * Get BigInteger random number from (1,p-1)
     *
     * @param n max border
     * @return random BigInteger (1,p-1)
     */
    private BigInteger rand(BigInteger n) {
        Random rand = new Random();
        BigInteger result = new BigInteger(n.bitLength(), rand);
        while (result.compareTo(n) >= 0) {
            result = new BigInteger(n.bitLength(), rand);
        }
        if (result.equals(new BigInteger("0"))
                || result.equals(new BigInteger("1"))
                || result.equals(n.subtract(BigInteger.ONE)))
            result = new BigInteger("2");
        return result;
    }

    /**
     * Encrypt message
     *
     * @param m message to encrypt
     * @param g random number
     * @param y private key
     * @param p module
     * @return encrypted message
     */
    private Pair<BigInteger, BigInteger> encrypt(BigInteger m, BigInteger g, BigInteger y, BigInteger p) {
        BigInteger r = rand(p);
        return new Pair<>(
                pow(g, r).mod(p),
                m.multiply(pow(y, r).mod(p))
        );
    }

    /**
     * finds pow from BigIntegers
     *
     * @param base     power from
     * @param exponent power to
     * @return base^exponent
     */
    private static BigInteger pow(BigInteger base, BigInteger exponent) {
        BigInteger result = BigInteger.ONE;
        while (exponent.signum() > 0) {
            if (exponent.testBit(0)) result = result.multiply(base);
            base = base.multiply(base);
            exponent = exponent.shiftRight(1);
        }
        return result;
    }

    /**
     * Finds P Root for p
     *
     * @param p number for witch P Root should be found
     * @return P Root for p
     */
    private static BigInteger getPRoot(BigInteger p) {
        for (BigInteger i = BigInteger.ZERO; i.compareTo(p) < 0; i = i.add(BigInteger.ONE))
            if (isPRoot(p, i))
                return i;
        return BigInteger.ZERO;
    }

    /**
     * Check if a is P Root ( p mod a == 1)
     *
     * @param a module
     * @return is p mod a == 1
     */
    private static boolean isPRoot(BigInteger p, BigInteger a) {
        if (a.equals(BigInteger.ZERO) || a.equals(BigInteger.ONE))
            return false;
        BigInteger last = BigInteger.ONE;

        Set<BigInteger> set = new HashSet<>();
        for (BigInteger i = BigInteger.ZERO; i.compareTo(p.subtract(BigInteger.ONE)) < 0; i = i.add(BigInteger.ONE)) {
            last = (last.multiply(a)).mod(p);
            if (set.contains(last)) // Если повтор
                return false;
            set.add(last);
        }
        return true;
    }

    /**
     * find b from a * b == m
     * b goes from 1 to INFINITY
     *
     * @param a one of multipliers
     * @param m module
     * @return another multipliers
     */
    private BigInteger getMod(BigInteger a, BigInteger m) {
        BigInteger b = BigInteger.ONE;
        while (!a.multiply(b).mod(m).equals(BigInteger.ONE)) {
            b = b.add(BigInteger.ONE);
        }
        return b;
    }
}
