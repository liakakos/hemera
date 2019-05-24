package hemera.bots;

import com.daml.ledger.javaapi.data.Command;
import com.daml.ledger.javaapi.data.Identifier;
import com.daml.ledger.javaapi.data.Record;
import com.daml.ledger.rxjava.components.LedgerViewFlowable;
import com.daml.ledger.rxjava.components.helpers.CommandsAndPendingSet;
import hemera.Web3jProvider;
import hemera.model.ethereum.smartcontract.NewSmartContractRequest;
import hemera.model.ethereum.utils.etherunits.Wei;
import hemera.utils.ABIUtils;
import hemera.utils.AddressUtils;
import hemera.utils.UnitUtils;
import io.reactivex.Flowable;
import org.ethereum.solidity.compiler.CompilationResult;
import org.ethereum.solidity.compiler.SolidityCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;


import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class SmartContractBot extends AbstractBot {

    private final static Logger log = LoggerFactory.getLogger(SmartContractBot.class);
    private final static long defaultGasLimit = 50000;
    private final static BigInteger defaultGasPriceWei = new BigInteger("20000000000");

    public SmartContractBot(String appId, String ledgerId, String party) {
        super.appId = appId;
        super.ledgerId = ledgerId;
        super.party = party;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Flowable<CommandsAndPendingSet> process(LedgerViewFlowable.LedgerView<Record> ledgerView) {
        List<NewSmartContractRequest.Contract> smartContractRequests = (List<NewSmartContractRequest.Contract>)(List<?>)
                getContracts(ledgerView, NewSmartContractRequest.TEMPLATE_ID);

        if (smartContractRequests.isEmpty()) {
            return Flowable.empty();
        }

        log.info(String.format("Got %d TransactionRequest(s)", smartContractRequests.size()));
        Map<Identifier, Set<String>> pending = new HashMap<>();
        pending.putIfAbsent(NewSmartContractRequest.TEMPLATE_ID, new HashSet<>());

        Map<String, BigInteger> nonces = AddressUtils.fetchAddressNonces(
                Web3jProvider.getInstance().web3j, smartContractRequests.stream()
                        .map(c -> c.data.from).collect(Collectors.toList()));

        Map<String, CompilationResult.ContractMetadata> metadataMap = new HashMap<>();

        smartContractRequests.forEach(contract -> {
            if (contract.data.optSolidity.isPresent()) {
                String solSource = contract.data.optSolidity.get();
                log.info(String.format("Solidity is: %s", solSource));

                try {
                    SolidityCompiler.Result res = SolidityCompiler.compile(
                            solSource.getBytes(),
                            true,
                            SolidityCompiler.Options.ABI,
                            SolidityCompiler.Options.BIN,
                            SolidityCompiler.Options.INTERFACE,
                            SolidityCompiler.Options.METADATA);
                    log.info(String.format("Out: %s", res.output));
                    log.info(String.format("Err: %s", res.errors));
                    if (res.isFailed()) {
                        log.error("Failed to compile solidity!");
                    } else {
                        CompilationResult result = CompilationResult.parse(res.output);
                        CompilationResult.ContractMetadata metadata = result.getContract(contract.data.name);
                        log.info(String.format("Binary is %s", metadata.bin));
                        log.info(String.format("ABI is %s", metadata.abi));
                        metadataMap.put(contract.id.contractId, metadata);
                    }
                } catch (UnsupportedOperationException | IOException e) {
                    log.error(e.toString());
                }
            }
        });

        List<Command> commandList = smartContractRequests.stream().map(contract -> {
            pending.get(NewSmartContractRequest.TEMPLATE_ID).add(contract.id.contractId);
            if (metadataMap.containsKey(contract.id.contractId) && nonces.containsKey(contract.data.from)) {
                BigInteger nonce = nonces.get(contract.data.from);
                BigInteger gasPrice = defaultGasPriceWei;
                BigInteger gas = new BigInteger(String.valueOf(contract.data.optGas.orElse(defaultGasLimit)));
                BigInteger weiValue = UnitUtils.fromEtherUnitsToWei(
                        contract.data.optValue.orElse(new Wei(BigDecimal.ZERO)));
                List<Type> constructorArgs = contract.data.ctorArgs.stream()
                        .map(ABIUtils::toWeb3jType).collect(Collectors.toList());
                String bin = "0x" + metadataMap.get(contract.id.contractId).bin;
                String encodedConstructor = "";
                if (!contract.data.ctorArgs.isEmpty()) {
                    encodedConstructor = FunctionEncoder.encodeConstructor(constructorArgs);
                }
                RawTransaction rawTransaction = RawTransaction.createContractTransaction(
                        nonce,
                        gasPrice,
                        gas,
                        weiValue,
                        bin + encodedConstructor);
                byte[] encodedTransaction = TransactionEncoder.encode(rawTransaction);
                return contract.id.exerciseNewSmartContractRequest_Accept(
                        metadataMap.get(contract.id.contractId).abi,
                        bin,
                        nonce.longValue(),
                        gas.longValue(),
                        UnitUtils.fromWeiToEtherUnits(gasPrice),
                        Numeric.toHexString(encodedTransaction));
            }

            return contract.id.exerciseNewSmartContractRequest_Reject();
        }).collect(Collectors.toList());

        if (!commandList.isEmpty()) {
            return toCommandsAndPendingSet(commandList, pending);
        } else {
            return Flowable.empty();
        }
    }
}
