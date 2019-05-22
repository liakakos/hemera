package hemera.utils;

import com.daml.ledger.javaapi.data.Unit;
import hemera.model.ethereum.utils.ABIType;
import hemera.model.ethereum.utils.abitype.*;
import hemera.model.ethereum.utils.abivalue.ABIList;
import hemera.model.ethereum.utils.abivalue.ABIValue;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;


public class ABIUtils {

    public static Type toWeb3jType(hemera.model.ethereum.utils.ABIValue v) {
        if (v instanceof ABIValue) {
            return toWeb3jDatatype((ABIValue)v);
        } else if (v instanceof ABIList) {
            List<Type> parsedList = ((ABIList)v).listValue.stream()
                    .map(ABIUtils::toWeb3jType).collect(Collectors.toList());
            return new DynamicArray<>(Type.class, parsedList);
        }

        throw new IllegalArgumentException(String.format("Could not parse ABIValue %s", v));
    }

    public static Type toWeb3jDatatype(ABIValue v) {
        ABIType abiType = v.abiType;
        if (abiType instanceof ABIBool) {
            return new Bool(Boolean.parseBoolean(v.abiValue));
        } else if (abiType instanceof ABIAddress) {
            return new Address(v.abiValue);
        } else if (abiType instanceof ABIString) {
            return new Utf8String(v.abiValue);
        } else if (abiType instanceof ABIBytes) {
            byte[] bytes = Numeric.hexStringToByteArray(v.abiValue);
            return new DynamicBytes(bytes);
        } else if (abiType instanceof ABIInt) {
            BigInteger bigInt = new BigInteger(v.abiValue);
            return new Int(bigInt);
        } else if (abiType instanceof ABIUint) {
            BigInteger bigUint = new BigInteger(v.abiValue);
            return new Uint(bigUint);
        }

        throw new IllegalArgumentException(String.format("Could not parse ABIValue %s", v));
    }

    public static List<TypeReference<?>> toWeb3jTypeReference(List<ABIType> abiTypes) {
        return abiTypes.stream().map(abiType -> {
            if (abiType instanceof ABIBool) {
                return new TypeReference<Bool>() {};
            } else if (abiType instanceof ABIAddress) {
                return new TypeReference<Address>() {};
            } else if (abiType instanceof ABIString) {
                return new TypeReference<Utf8String>() {};
            } else if (abiType instanceof ABIBytes) {
                return new TypeReference<DynamicBytes>() {};
            } else if (abiType instanceof ABIInt) {
                return new TypeReference<Int>() {};
            } else if (abiType instanceof ABIUint) {
                return new TypeReference<Uint>() {};
            }

            throw new IllegalArgumentException(String.format("Could not parse ABIType %s", abiType));
        }).collect(Collectors.toList());
    }

    public static List<hemera.model.ethereum.utils.ABIValue> fromWeb3jTypes(List<Type> types) {
        Unit unit = Unit.getInstance();
        return types.stream().map(type -> {
            if (type instanceof Bool) {
                return new ABIValue(new ABIBool(unit), ((Bool)type).getValue().toString());
            } else if (type instanceof Address) {
                return new ABIValue(new ABIAddress(unit), ((Address)type).getValue());
            } else if (type instanceof Utf8String) {
                return new ABIValue(new ABIString(unit), ((Utf8String)type).getValue());
            } else if (type instanceof DynamicBytes) {
                return new ABIValue(new ABIBytes(unit), Numeric.toHexString(((DynamicBytes)type).getValue()));
            } else if (type instanceof Int) {
                return new ABIValue(new ABIInt(unit), ((Int)type).getValue().toString());
            } else if (type instanceof Uint) {
                return new ABIValue(new ABIUint(unit), ((Uint)type).getValue().toString());
            }

            throw new IllegalArgumentException(String.format("Could not parse web3j abi type %s", type));
        }).collect(Collectors.toList());
    }
}
