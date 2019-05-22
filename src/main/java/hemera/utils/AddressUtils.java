package hemera.utils;

import org.web3j.crypto.Hash;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AddressUtils {

    public static boolean isValidAddress(String address) {
        if(!Pattern.matches("(?i)^(0x)?[0-9a-f]{40}$", address)) {
            return false;
        } else if (Pattern.matches("^(0x)?[0-9a-f]{40}$", address) ||
                Pattern.matches("^(0x)?[0-9A-F]{40}$", address)) {
            return true;
        } else {
            return isCheckedAddress(address);
        }
    }

    public static String checkedAddress(final String address) {
        final String cleanAddress = Numeric.cleanHexPrefix(address).toLowerCase();

        StringBuilder o = new StringBuilder();
        String keccak = Hash.sha3String(cleanAddress);
        char[] checkChars = keccak.substring(2).toCharArray();

        char[] cs = cleanAddress.toLowerCase().toCharArray();
        for (int i = 0; i < cs.length; i++) {
            char c = cs[i];
            c = (Character.digit(checkChars[i], 16) & 0xFF) > 7 ?
                    Character.toUpperCase(c) : Character.toLowerCase(c);
            o.append(c);
        }
        return Numeric.prependHexPrefix(o.toString());
    }

    public static boolean isCheckedAddress(final String address) {
        return Numeric.prependHexPrefix(address).equals(checkedAddress(address));
    }

    public static Map<String, BigInteger> fetchAddressNonces(Web3j web3j, Collection<String> addresses) {
        Map<String, BigInteger> nonces = new HashMap<>();

        addresses.forEach(a -> {
            if (nonces.containsKey(a)) {
                System.out.println("Found non unique addresses. Nonce will be valid only for " +
                        "the first mined transaction");
                return;
            }

            try {
                EthGetTransactionCount resp = web3j.ethGetTransactionCount(a, DefaultBlockParameterName.LATEST).send();
                nonces.putIfAbsent(a, resp.getTransactionCount());
            } catch (Exception e) {
                System.err.println(String.format("Failed to fetch nonce for address %s, Exception: %s",
                        a, e.toString()));
            }
        });

        return nonces;
    }
}
