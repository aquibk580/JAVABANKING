
// import java.security.MessageDigest;
// import java.security.NoSuchAlgorithmException;
// import java.security.SecureRandom;
import java.security.*;

public class Hashing {
 public String hashPassword(String password) {
  try {
   MessageDigest digest = MessageDigest.getInstance("SHA-256");
   byte[] hashBytes = digest.digest(password.getBytes());

   StringBuilder hexString = new StringBuilder();
   for (byte b : hashBytes) {
    hexString.append(String.format("%02x", b));
   }
   return hexString.toString();
  } catch (NoSuchAlgorithmException e) {
   throw new RuntimeException("Error: Invalid algorithm", e);
  }
 }

 public boolean comparePasswords(String enteredPassword, String storedHash) {
  String enteredPasswordHash = hashPassword(enteredPassword);
  return enteredPasswordHash.equals(storedHash);
 }

}
