import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.BitSet;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class CryptographicAlgorithmConsole {

	public static BigInteger[][] generateKeys(int seed, int length) {

		int aSize = length;

		Random random = new Random(seed);
		BigInteger[] a = new BigInteger[aSize];
		BigInteger aSum = BigInteger.valueOf(0);
		a[0] = BigInteger.probablePrime(aSize, random);
		aSum = aSum.add(a[0]);

		for (int i = 1; i < aSize; i++) {
			a[i] = randomBigInteger(aSum.bitLength() + 1, random);
			aSum = aSum.add(a[i]);
		}

		BigInteger q = BigInteger.probablePrime(aSum.bitLength() + 1, random);
		BigInteger p = BigInteger.probablePrime(q.bitLength() - random.nextInt(10) - 1, random);

		while (!gcd(p, q).equals(BigInteger.valueOf(1))) {
			p = BigInteger.probablePrime(q.bitLength() - random.nextInt(10) - 1, random);
		}

		BigInteger[] b = new BigInteger[aSize];

		for (int i = 0; i < aSize; i++) {
			b[i] = (a[i].multiply(p)).mod(q);
		}

		BigInteger[] qpArr = { q, p };

		BigInteger[][] keys = { qpArr, a, b };

		return keys;
	}

	public static BigInteger encrypt(BitSet message, BigInteger[] key) {
		BigInteger fail = BigInteger.valueOf(0);
		if (message.length() > key.length) {
			return fail;
		}

		BigInteger sum = BigInteger.valueOf(0);
		for (int i = 0; i < message.length(); i++) {
			if (message.get(i) == true) {
				sum = sum.add(key[i]);
			}
		}
		return sum;
	}

	private static BitSet decrypt(BigInteger message, BigInteger[][] keys) {
		BigInteger q = keys[0][0];
		BigInteger p = keys[0][1];
		BigInteger[] a = keys[1];
		message = message.multiply(p.modInverse(q)).mod(q);
		BitSet dec = new BitSet();
		for (int i = a.length - 1; i >= 0; i--) {
			if (a[i].compareTo(message) <= 0) {
				dec.set(i);
				message = message.subtract(a[i]);
			}
		}
		return dec;
	}

	public static BitSet stringToBitSet(String s) {
		BitSet a = new BitSet();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			for (int j = 0; j < Character.SIZE; j++) {
				boolean bit = ((c >> j) & 1) > 0;
				a.set(Character.SIZE * i + j, bit);
			}
		}
		return a;
	}

	public static String bitSetToString(BitSet a) {
		StringBuilder sb = new StringBuilder();
		int length = (a.length() + Character.SIZE - 1) / (Character.SIZE);
		for (int i = 0; i < length; i++) {
			BitSet character = a.get(Character.SIZE * i, Character.SIZE * (i + 1));
			char c = (char) character.toLongArray()[0];
			sb.append(c);
		}
		String s = sb.toString();
		return s;
	}

	public static BigInteger randomBigInteger(int length, Random random) {
		BigInteger n;
		do {
			n = new BigInteger(length, random);
		} while (n.bitLength() != length);
		return n;
	}

	public static BigInteger gcd(BigInteger a, BigInteger b) {
		if (b.equals(BigInteger.valueOf(0))) {
			return a;
		}
		BigInteger res = gcd(b, a.mod(b));
		return res;
	}

	public static void main(String[] args) throws FileNotFoundException {
		Scanner in = new Scanner(System.in);
		System.out.println("If you want to create a new key type \"kg\" \nIf you want to encrypt a message type \"ec\" \nIf you want to decrypt a message type \"dc\"");
		boolean invalidCommand = true;
		while (invalidCommand) {
			String action = in.next();
			in.nextLine();
			if (action.equals("kg")) {
				invalidCommand = false;
				PrintWriter publicOut = new PrintWriter(new File("public.txt"));
				PrintWriter privateOut = new PrintWriter(new File("private.txt"));
				System.out.println("To create a key enter a seed:");
				int length = 256;
				int seed = 566;
				boolean seedNotEntered = true;
				while (seedNotEntered) {
					try {
						String s = in.next();
						seed = Integer.parseInt(s);
						seedNotEntered = false;
					} catch (NumberFormatException e) {
						System.out.println("Invalid seed. Try again:");
					}
				}
				System.out.println("Enter the max length of your message in bits (256 by default):");
				try {
					length = in.nextInt();
				} catch (InputMismatchException e) {
					System.out.println("Invalid length. Length was set to 256.");
				}
				
				BigInteger[][] keys = generateKeys(seed, length);
				BigInteger q = keys[0][0];
				BigInteger p = keys[0][1];
				BigInteger[] a = keys[1];
				BigInteger[] b = keys[2];
				privateOut.print(q + "\n\n");
				privateOut.print(p + "\n\n");
				for (int i = 0; i < a.length; i++) {
					privateOut.print(a[i] + "\n");
				}
				System.out.println("Your private keys were saved at \"private.txt\"");
				for (int i = 0; i < b.length; i++) {
					publicOut.print(b[i] + "\n");
				}
				System.out.println("Your public keys were saved at \"public.txt\"");
				privateOut.close();
				publicOut.close();
				System.exit(0);
			} else if (action.equals("ec")) {
				invalidCommand = false;
				PrintWriter out = new PrintWriter(new File("encrypted.txt"));
				System.out.println(
						"To encrypt a message type it in. Note that it can't be longer than stated where the public key was posted:");
				String message = in.nextLine();
				System.out.println("Now enter the directory of the file where the public key is stored:");
				boolean fileNotFound = true;
				Scanner fileIn = new Scanner(System.in);
				String filePath = "";
				while (fileNotFound) {
					filePath = in.next();
					try {
						fileIn = new Scanner(new File(filePath));
						fileNotFound = false;
					} catch (FileNotFoundException e) {
						System.out.println("Invalid file path. Try again:");
					}
				}
				int length = 0;
				while(fileIn.hasNext()) {
					fileIn.next();
					length++;
				}
				fileIn = new Scanner(new File(filePath));
				BigInteger [] b = new BigInteger[length];
				for (int i = 0; i < length; i++) {
					b[i] = new BigInteger(fileIn.next());
				}
				out.print(encrypt(stringToBitSet(message), b));
				out.close();
				if (!encrypt(stringToBitSet(message), b).equals(BigInteger.valueOf(0))) {
					System.out.println("Your encrypted message was saved at \"encrypted.txt\"");
					System.out.println("Encrypted message: " + encrypt(stringToBitSet(message), b));
				} else {
					System.out.println("Encryption falied. Message too long.");
				}
				fileIn.close();
				System.exit(0);
			} else if (action.equals("dc")) {
				invalidCommand = false;
				System.out.println("To decrypt the message enter the encrypted message or the path to the file where it is saved:");
				Scanner fileIn = new Scanner(System.in);
				String filePath = "";
				boolean fileNotFound = true;
				BigInteger message = BigInteger.valueOf(0);
				while (fileNotFound) {
					filePath = in.next();
					try {
						fileIn = new Scanner(new File(filePath));
						String strMessage = fileIn.next();
						message = new BigInteger(strMessage);
						fileNotFound = false;
					} catch (FileNotFoundException e) {
						try {
							message = new BigInteger(filePath);
							fileNotFound = false;
						} catch (NumberFormatException e1) {
							System.out.println("Invalid path or message. Try again:");
						}
					}
				}
				System.out.println("Now enter the directory of the file where the private key is stored:");
				fileNotFound = true;
				filePath = "";
				while (fileNotFound) {
					filePath = in.next();
					try {
						fileIn = new Scanner(new File(filePath));
						fileNotFound = false;
					} catch (FileNotFoundException e) {
						System.out.println("Invalid file path. Try again:");
					}
				}
				int length = 0;
				while(fileIn.hasNext()) {
					fileIn.next();
					length++;
				}
				fileIn = new Scanner(new File(filePath));
				BigInteger q = new BigInteger(fileIn.next());
				BigInteger p = new BigInteger(fileIn.next());
				BigInteger [] a = new BigInteger[length - 2];
				for (int i = 0; i < length - 2; i++) {
					a[i] = new BigInteger(fileIn.next());
				}
				fileIn.close();
				BigInteger qp [] = {q, p};
				BigInteger[][] key = {qp, a};
				System.out.println("Decrypted message: " + bitSetToString(decrypt(message, key)));
				System.exit(0);
			}
			System.out.println("Invalid command. Try again:");
		}
		in.close();
	}

}
