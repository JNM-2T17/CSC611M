import java.math.BigInteger;

public class SubPrime {
	public static void main(String args[]) {
		BigInteger prime = new BigInteger(args[0]);
		BigInteger s = new BigInteger(args[1]);
		BigInteger e = new BigInteger(args[2]);

        boolean tempIsPrime = true;

        for(; s.compareTo(e) <= 0; s = s.add(BigInteger.ONE)){
            if(prime.mod(s).equals(BigInteger.ZERO)){
                tempIsPrime = false;
                break;
            }
        }

        System.out.println(tempIsPrime);
	}
}