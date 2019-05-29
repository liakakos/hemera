package hemera.utils;

import com.daml.ledger.javaapi.data.Unit;
import hemera.model.ethereum.utils.ABIType;
import hemera.model.ethereum.utils.abitype.*;
import hemera.model.ethereum.utils.abivalue.ABIList;
import hemera.model.ethereum.utils.abivalue.ABIValue;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.*;
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

    private static Type toWeb3jDatatype(ABIValue v) {
        ABIType abiType = v.abiType;
        if (abiType instanceof ABIBool) {
            return new Bool(Boolean.parseBoolean(v.abiValue));
        } else if (abiType instanceof ABIAddress) {
            return new Address(v.abiValue);
        } else if (abiType instanceof ABIString) {
            return new Utf8String(v.abiValue);
        } else if (abiType instanceof ABIBytes) {
            return toWeb3jFixedSizeBytes((ABIBytes) abiType, v.abiValue);
        } else if (abiType instanceof ABIDynamicBytes) {
            byte[] bytes = Numeric.hexStringToByteArray(v.abiValue);
            return new DynamicBytes(bytes);
        } else if (abiType instanceof ABIInt) {
            return toWeb3jInt((ABIInt) abiType, v.abiValue);
        } else if (abiType instanceof ABIUint) {
            return toWeb3jUint((ABIUint) abiType, v.abiValue);
        }

        throw new IllegalArgumentException(String.format("Could not parse ABIValue %s", v));
    }

    private static Type toWeb3jFixedSizeBytes(ABIBytes abiBytes, String hex) {
        Long byteSize = abiBytes.longValue;
        if (byteSize < 1 || byteSize > 32) {
            throw new IllegalArgumentException(String.format("Could not parse ABIBytes %d", byteSize));
        }
        byte[] bytes = Numeric.hexStringToByteArray(hex);
        switch (byteSize.intValue()) {
            case 1:  return new Bytes1(bytes);
            case 2:  return new Bytes2(bytes);
            case 3:  return new Bytes3(bytes);
            case 4:  return new Bytes4(bytes);
            case 5:  return new Bytes5(bytes);
            case 6:  return new Bytes6(bytes);
            case 7:  return new Bytes7(bytes);
            case 8:  return new Bytes8(bytes);
            case 9:  return new Bytes9(bytes);
            case 10: return new Bytes10(bytes);
            case 11: return new Bytes11(bytes);
            case 12: return new Bytes12(bytes);
            case 13: return new Bytes13(bytes);
            case 14: return new Bytes14(bytes);
            case 15: return new Bytes15(bytes);
            case 16: return new Bytes16(bytes);
            case 17: return new Bytes17(bytes);
            case 18: return new Bytes18(bytes);
            case 19: return new Bytes19(bytes);
            case 20: return new Bytes20(bytes);
            case 21: return new Bytes21(bytes);
            case 22: return new Bytes22(bytes);
            case 23: return new Bytes23(bytes);
            case 24: return new Bytes24(bytes);
            case 25: return new Bytes25(bytes);
            case 26: return new Bytes26(bytes);
            case 27: return new Bytes27(bytes);
            case 28: return new Bytes28(bytes);
            case 29: return new Bytes29(bytes);
            case 30: return new Bytes30(bytes);
            case 31: return new Bytes31(bytes);
            case 32: return new Bytes32(bytes);
            default: throw new IllegalArgumentException(String.format("Could not parse ABIBytes %d",
                    byteSize.intValue()));
        }
    }

    private static Type toWeb3jInt(ABIInt abiInt, String integer) {
        Long bitSize = abiInt.longValue;
        if (bitSize < 8 || bitSize > 256 || bitSize % 8 != 0) {
            throw new IllegalArgumentException(String.format("Could not parse ABIInt %d", bitSize));
        }
        BigInteger bigInt = new BigInteger(integer);
        switch (bitSize.intValue()) {
            case 8:   return new Int8(bigInt);
            case 16:  return new Int16(bigInt);
            case 24:  return new Int24(bigInt);
            case 32:  return new Int32(bigInt);
            case 40:  return new Int40(bigInt);
            case 48:  return new Int48(bigInt);
            case 56:  return new Int56(bigInt);
            case 64:  return new Int64(bigInt);
            case 72:  return new Int72(bigInt);
            case 80:  return new Int80(bigInt);
            case 88:  return new Int88(bigInt);
            case 96:  return new Int96(bigInt);
            case 104: return new Int104(bigInt);
            case 112: return new Int112(bigInt);
            case 120: return new Int120(bigInt);
            case 128: return new Int128(bigInt);
            case 136: return new Int136(bigInt);
            case 144: return new Int144(bigInt);
            case 152: return new Int152(bigInt);
            case 160: return new Int160(bigInt);
            case 168: return new Int168(bigInt);
            case 176: return new Int176(bigInt);
            case 184: return new Int184(bigInt);
            case 192: return new Int192(bigInt);
            case 200: return new Int200(bigInt);
            case 208: return new Int208(bigInt);
            case 216: return new Int216(bigInt);
            case 224: return new Int224(bigInt);
            case 232: return new Int232(bigInt);
            case 240: return new Int240(bigInt);
            case 248: return new Int248(bigInt);
            case 256: return new Int256(bigInt);
            default:  throw new IllegalArgumentException(String.format("Could not parse ABIInt %d",
                    bitSize.intValue()));
        }
    }

    private static Type toWeb3jUint(ABIUint abiUint, String uInteger) {
        Long bitSize = abiUint.longValue;
        if (bitSize < 8 || bitSize > 256 || bitSize % 8 != 0) {
            throw new IllegalArgumentException(String.format("Could not parse ABIUint %d", bitSize));
        }
        BigInteger bigUint = new BigInteger(uInteger);
        switch (bitSize.intValue()) {
            case 8:   return new Uint8(bigUint);
            case 16:  return new Uint16(bigUint);
            case 24:  return new Uint24(bigUint);
            case 32:  return new Uint32(bigUint);
            case 40:  return new Uint40(bigUint);
            case 48:  return new Uint48(bigUint);
            case 56:  return new Uint56(bigUint);
            case 64:  return new Uint64(bigUint);
            case 72:  return new Uint72(bigUint);
            case 80:  return new Uint80(bigUint);
            case 88:  return new Uint88(bigUint);
            case 96:  return new Uint96(bigUint);
            case 104: return new Uint104(bigUint);
            case 112: return new Uint112(bigUint);
            case 120: return new Uint120(bigUint);
            case 128: return new Uint128(bigUint);
            case 136: return new Uint136(bigUint);
            case 144: return new Uint144(bigUint);
            case 152: return new Uint152(bigUint);
            case 160: return new Uint160(bigUint);
            case 168: return new Uint168(bigUint);
            case 176: return new Uint176(bigUint);
            case 184: return new Uint184(bigUint);
            case 192: return new Uint192(bigUint);
            case 200: return new Uint200(bigUint);
            case 208: return new Uint208(bigUint);
            case 216: return new Uint216(bigUint);
            case 224: return new Uint224(bigUint);
            case 232: return new Uint232(bigUint);
            case 240: return new Uint240(bigUint);
            case 248: return new Uint248(bigUint);
            case 256: return new Uint256(bigUint);
            default:  throw new IllegalArgumentException(String.format("Could not parse ABIUint %d",
                    bitSize.intValue()));
        }
    }

    public static List<TypeReference<?>> toWeb3jTypeReference(List<ABIType> abiTypes) {
        return abiTypes.stream().map(abiType -> {
            if (abiType instanceof ABIBool) {
                return new TypeReference<Bool>() {};
            } else if (abiType instanceof ABIAddress) {
                return new TypeReference<Address>() {};
            } else if (abiType instanceof ABIString) {
                return new TypeReference<Utf8String>() {};
            } else if (abiType instanceof ABIDynamicBytes) {
                return new TypeReference<DynamicBytes>() {};
            } else if (abiType instanceof ABIBytes) {
                return toWeb3jBytesTypeReference((ABIBytes) abiType);
            } else if (abiType instanceof ABIInt) {
                return toWeb3jIntTypeReference((ABIInt) abiType);
            } else if (abiType instanceof ABIUint) {
                return toWeb3jUintTypeReference((ABIUint) abiType);
            }

            throw new IllegalArgumentException(String.format("Could not parse ABIType %s", abiType));
        }).collect(Collectors.toList());
    }

    private static TypeReference<?> toWeb3jBytesTypeReference(ABIBytes abiBytes) {
        Long byteSize = abiBytes.longValue;
        if (byteSize < 1 || byteSize > 32) {
            throw new IllegalArgumentException(String.format("Could not parse ABIBytes %d to Type Reference",
                    byteSize));
        }
        switch (byteSize.intValue()) {
            case 1:  return new TypeReference<Bytes1>() {};
            case 2:  return new TypeReference<Bytes2>() {};
            case 3:  return new TypeReference<Bytes3>() {};
            case 4:  return new TypeReference<Bytes4>() {};
            case 5:  return new TypeReference<Bytes5>() {};
            case 6:  return new TypeReference<Bytes6>() {};
            case 7:  return new TypeReference<Bytes7>() {};
            case 8:  return new TypeReference<Bytes8>() {};
            case 9:  return new TypeReference<Bytes9>() {};
            case 10: return new TypeReference<Bytes10>() {};
            case 11: return new TypeReference<Bytes11>() {};
            case 12: return new TypeReference<Bytes12>() {};
            case 13: return new TypeReference<Bytes13>() {};
            case 14: return new TypeReference<Bytes14>() {};
            case 15: return new TypeReference<Bytes15>() {};
            case 16: return new TypeReference<Bytes16>() {};
            case 17: return new TypeReference<Bytes17>() {};
            case 18: return new TypeReference<Bytes18>() {};
            case 19: return new TypeReference<Bytes19>() {};
            case 20: return new TypeReference<Bytes20>() {};
            case 21: return new TypeReference<Bytes21>() {};
            case 22: return new TypeReference<Bytes22>() {};
            case 23: return new TypeReference<Bytes23>() {};
            case 24: return new TypeReference<Bytes24>() {};
            case 25: return new TypeReference<Bytes25>() {};
            case 26: return new TypeReference<Bytes26>() {};
            case 27: return new TypeReference<Bytes27>() {};
            case 28: return new TypeReference<Bytes28>() {};
            case 29: return new TypeReference<Bytes29>() {};
            case 30: return new TypeReference<Bytes30>() {};
            case 31: return new TypeReference<Bytes31>() {};
            case 32: return new TypeReference<Bytes32>() {};
            default: throw new IllegalArgumentException(String.format("Could not parse ABIBytes %d to Type Reference",
                    byteSize.intValue()));
        }
    }

    private static TypeReference<?> toWeb3jIntTypeReference(ABIInt abiInt) {
        Long bitSize = abiInt.longValue;
        if (bitSize < 8 || bitSize > 256 || bitSize % 8 != 0) {
            throw new IllegalArgumentException(String.format("Could not parse ABIInt %d to Type Reference", bitSize));
        }
        switch (bitSize.intValue()) {
            case 8:   return new TypeReference<Int8>() {};
            case 16:  return new TypeReference<Int16>() {};
            case 24:  return new TypeReference<Int24>() {};
            case 32:  return new TypeReference<Int32>() {};
            case 40:  return new TypeReference<Int40>() {};
            case 48:  return new TypeReference<Int48>() {};
            case 56:  return new TypeReference<Int56>() {};
            case 64:  return new TypeReference<Int64>() {};
            case 72:  return new TypeReference<Int72>() {};
            case 80:  return new TypeReference<Int80>() {};
            case 88:  return new TypeReference<Int88>() {};
            case 96:  return new TypeReference<Int96>() {};
            case 104: return new TypeReference<Int104>() {};
            case 112: return new TypeReference<Int112>() {};
            case 120: return new TypeReference<Int120>() {};
            case 128: return new TypeReference<Int128>() {};
            case 136: return new TypeReference<Int136>() {};
            case 144: return new TypeReference<Int144>() {};
            case 152: return new TypeReference<Int152>() {};
            case 160: return new TypeReference<Int160>() {};
            case 168: return new TypeReference<Int168>() {};
            case 176: return new TypeReference<Int176>() {};
            case 184: return new TypeReference<Int184>() {};
            case 192: return new TypeReference<Int192>() {};
            case 200: return new TypeReference<Int200>() {};
            case 208: return new TypeReference<Int208>() {};
            case 216: return new TypeReference<Int216>() {};
            case 224: return new TypeReference<Int224>() {};
            case 232: return new TypeReference<Int232>() {};
            case 240: return new TypeReference<Int240>() {};
            case 248: return new TypeReference<Int248>() {};
            case 256: return new TypeReference<Int256>() {};
            default:  throw new IllegalArgumentException(String.format("Could not parse ABIInt %d to Type Reference",
                    bitSize.intValue()));
        }
    }

    private static TypeReference<?> toWeb3jUintTypeReference(ABIUint abiUint) {
        Long bitSize = abiUint.longValue;
        if (bitSize < 8 || bitSize > 256 || bitSize % 8 != 0) {
            throw new IllegalArgumentException(String.format("Could not parse ABIUint %d to Type Reference", bitSize));
        }
        switch (bitSize.intValue()) {
            case 8:   return new TypeReference<Uint8>() {};
            case 16:  return new TypeReference<Uint16>() {};
            case 24:  return new TypeReference<Uint24>() {};
            case 32:  return new TypeReference<Uint32>() {};
            case 40:  return new TypeReference<Uint40>() {};
            case 48:  return new TypeReference<Uint48>() {};
            case 56:  return new TypeReference<Uint56>() {};
            case 64:  return new TypeReference<Uint64>() {};
            case 72:  return new TypeReference<Uint72>() {};
            case 80:  return new TypeReference<Uint80>() {};
            case 88:  return new TypeReference<Uint88>() {};
            case 96:  return new TypeReference<Uint96>() {};
            case 104: return new TypeReference<Uint104>() {};
            case 112: return new TypeReference<Uint112>() {};
            case 120: return new TypeReference<Uint120>() {};
            case 128: return new TypeReference<Uint128>() {};
            case 136: return new TypeReference<Uint136>() {};
            case 144: return new TypeReference<Uint144>() {};
            case 152: return new TypeReference<Uint152>() {};
            case 160: return new TypeReference<Uint160>() {};
            case 168: return new TypeReference<Uint168>() {};
            case 176: return new TypeReference<Uint176>() {};
            case 184: return new TypeReference<Uint184>() {};
            case 192: return new TypeReference<Uint192>() {};
            case 200: return new TypeReference<Uint200>() {};
            case 208: return new TypeReference<Uint208>() {};
            case 216: return new TypeReference<Uint216>() {};
            case 224: return new TypeReference<Uint224>() {};
            case 232: return new TypeReference<Uint232>() {};
            case 240: return new TypeReference<Uint240>() {};
            case 248: return new TypeReference<Uint248>() {};
            case 256: return new TypeReference<Uint256>() {};
            default:  throw new IllegalArgumentException(String.format("Could not parse ABIUint %d to Type Reference",
                    bitSize.intValue()));
        }
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
                return new ABIValue(new ABIDynamicBytes(unit), Numeric.toHexString(((DynamicBytes)type).getValue()));
            } else if (type instanceof Bytes) {
                Bytes bytes = (Bytes)type;
                Long length = Integer.valueOf(bytes.getValue().length).longValue();
                return new ABIValue(new ABIBytes(length), Numeric.toHexString(bytes.getValue()));
            } else if (type instanceof Int) {
                Int integer = (Int)type;
                Long length = Long.valueOf(integer.getTypeAsString().split("int")[1]);
                return new ABIValue(new ABIInt(length), integer.getValue().toString());
            } else if (type instanceof Uint) {
                Uint uInteger = (Uint)type;
                Long length = Long.valueOf(uInteger.getTypeAsString().split("uint")[1]);
                return new ABIValue(new ABIUint(length), uInteger.getValue().toString());
            }

            throw new IllegalArgumentException(String.format("Could not parse web3j abi type %s", type));
        }).collect(Collectors.toList());
    }
}
