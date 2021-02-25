package general;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class PasswordHelper {

	public static String getSha256(String s) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(s.getBytes("UTF-8"));
        byte[] digest = md.digest();
        return String.format("%064x", new java.math.BigInteger(1, digest));
	}
	
	public static String getGeneratedSalt(String s)
	{
		try{
			s = getSha256(s);
		}
		catch(NoSuchAlgorithmException e)
		{
			//TODO LOG
		}
		catch(UnsupportedEncodingException e)
		{
			//TODO LOG
		}
		
		char[] c = new char[32];
		Random r = new Random();
		for(int i = 0; i < c.length; i++)
		{
			c[i] = s.charAt(r.nextInt(s.length()));
		}
		
		return new String(c).toString();
	}
	
}
