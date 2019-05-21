package hemera.utils;

import hemera.model.ethereum.utils.EtherUnits;
import hemera.model.ethereum.utils.etherunits.*;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

public class UnitUtils {

    public static EtherUnits fromWeiToEtherUnits(BigInteger weiAmount) {
        BigDecimal weiDec = new BigDecimal(weiAmount);
        Convert.Unit formattedUnit = Convert.Unit.WEI;
        BigDecimal formattedAmount = weiDec;
        for (int i = Convert.Unit.values().length - 1; i >= 0; i--) {
            formattedAmount = Convert.fromWei(weiDec, Convert.Unit.values()[i]);
            if (formattedAmount.compareTo(BigDecimal.ONE) >= 0) {
                formattedUnit = Convert.Unit.values()[i];
                break;
            }
        }

        switch (formattedUnit) {
            case KWEI:
                return new Kwei(formattedAmount);
            case MWEI:
                return new Mwei(formattedAmount);
            case GWEI:
                return new Gwei(formattedAmount);
            case SZABO:
                return new Szabo(formattedAmount);
            case FINNEY:
                return new Finney(formattedAmount);
            case ETHER:
                return new Ether(formattedAmount);
            case KETHER:
                return new Kether(formattedAmount);
            case METHER:
                return new Mether(formattedAmount);
            case GETHER:
                return new Gether(formattedAmount);
            case WEI:
            default:
                return new Wei(formattedAmount);
        }
    }

    public static BigInteger fromEtherUnitsToWei(EtherUnits amount) {
        if (amount instanceof Wei) {
            return Convert.toWei(((Wei)amount).bigDecimalValue, Convert.Unit.WEI).toBigInteger();
        } else if (amount instanceof Kwei) {
            return Convert.toWei(((Kwei)amount).bigDecimalValue, Convert.Unit.KWEI).toBigInteger();
        } else if (amount instanceof Mwei) {
            return Convert.toWei(((Mwei)amount).bigDecimalValue, Convert.Unit.MWEI).toBigInteger();
        } else if (amount instanceof Gwei) {
            return Convert.toWei(((Gwei)amount).bigDecimalValue, Convert.Unit.GWEI).toBigInteger();
        } else if (amount instanceof Szabo) {
            return Convert.toWei(((Szabo)amount).bigDecimalValue, Convert.Unit.SZABO).toBigInteger();
        } else if (amount instanceof Finney) {
            return Convert.toWei(((Finney)amount).bigDecimalValue, Convert.Unit.FINNEY).toBigInteger();
        } else if (amount instanceof Ether) {
            return Convert.toWei(((Ether)amount).bigDecimalValue, Convert.Unit.ETHER).toBigInteger();
        } else if (amount instanceof Kether) {
            return Convert.toWei(((Kether)amount).bigDecimalValue, Convert.Unit.KETHER).toBigInteger();
        } else if (amount instanceof Mether) {
            return Convert.toWei(((Mether)amount).bigDecimalValue, Convert.Unit.METHER).toBigInteger();
        } else if (amount instanceof Gether) {
            return Convert.toWei(((Gether) amount).bigDecimalValue, Convert.Unit.GETHER).toBigInteger();
        }

        throw new IllegalArgumentException(String.format("Failed to convert %s to wei", amount));
    }
}
