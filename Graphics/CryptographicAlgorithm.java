import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.BitSet;
import java.util.Random;
import java.util.Scanner;

public class CryptographicAlgorithm {

	public static BigInteger[][] generateKeys(String seed, int length) {

		int aSize = length;

		SecureRandom random = new SecureRandom(seed.getBytes());
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
	
	public static void handleKeyGen(String seed, int length) throws FileNotFoundException {
		length *= Character.SIZE;
		new File("keys").mkdirs();
		PrintWriter publicOut = new PrintWriter(new File("keys/public.txt"));
		PrintWriter privateOut = new PrintWriter(new File("keys/private.txt"));
		PrintWriter infoOut = new PrintWriter(new File("keys/info.txt"));
		
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
		System.out.println("Your private keys were saved at \"keys/private.txt\"");
		for (int i = 0; i < b.length; i++) {
			publicOut.print(b[i] + "\n");
		}
		System.out.println("Your public keys were saved at \"keys/public.txt\"");
		infoOut.println("Max supported length of message: " + length / 16);
		privateOut.close();
		publicOut.close();
		infoOut.close();
	}

	public static String handleDecrypt(String strMessage, String privateKey) throws FileNotFoundException {
		BigInteger message = new BigInteger(strMessage);
		Scanner fileIn = new Scanner(privateKey);
		int length = 0;
		while(fileIn.hasNext()) {
			fileIn.next();
			length++;
		}
		fileIn.close();
		fileIn = new Scanner(privateKey);
		BigInteger q = new BigInteger(fileIn.next());
		BigInteger p = new BigInteger(fileIn.next());
		BigInteger [] a = new BigInteger[length - 2];
		for (int i = 0; i < length - 2; i++) {
			a[i] = new BigInteger(fileIn.next());
		}
		fileIn.close();
		BigInteger qp [] = {q, p};
		BigInteger[][] key = {qp, a};
		String decrypted = bitSetToString(decrypt(message, key));
		System.out.println("Decrypted message: " + decrypted);
		return decrypted;
	}
	
	public static BigInteger handleEncrypt(String message, String publicKey) throws FileNotFoundException {
		Scanner fileIn = new Scanner(publicKey);
		PrintWriter out = new PrintWriter(new File("encrypted.txt"));
		int length = 0;
		while(fileIn.hasNext()) {
			fileIn.next();
			length++;
		}
		fileIn.close();
		fileIn = new Scanner(publicKey);
		BigInteger [] b = new BigInteger[length];
		for (int i = 0; i < length; i++) {
			b[i] = new BigInteger(fileIn.next());
		}
		BigInteger encrypted = encrypt(stringToBitSet(message), b);
		out.print(encrypted);
		out.close();
		if (!encrypted.equals(BigInteger.valueOf(0))) {
			System.out.println("Your encrypted message was saved at \"encrypted.txt\"");
			System.out.println("Encrypted message: " + encrypted);
		} else {
			System.out.println("Encryption failed. Message too long.");
			fileIn.close();
			throw new ArrayIndexOutOfBoundsException();
		}
		fileIn.close();
		return encrypted;
	}
	
	public static void main(String[] args) throws IOException {
		String message = "Hello, World!";
		System.out.println(message.length() * 16);
		handleKeyGen("abc", 13);
		String publicKey = new String(Files.readAllBytes(Paths.get("keys/public.txt")));
		String privateKey = new String(Files.readAllBytes(Paths.get("keys/private.txt")));
		String enc = "" + handleEncrypt(message, publicKey);
		String dec = handleDecrypt(enc, privateKey);
		System.out.println(enc + "\n" + dec);
	}
}