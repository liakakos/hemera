package hemera.bots;

import com.daml.ledger.javaapi.data.Command;
import com.daml.ledger.javaapi.data.Identifier;
import com.daml.ledger.javaapi.data.Record;
import com.daml.ledger.rxjava.components.LedgerViewFlowable;
import com.daml.ledger.rxjava.components.helpers.CommandsAndPendingSet;
import hemera.OperatorMain;
import hemera.Web3jProvider;
import hemera.model.ethereum.transaction.TransactionRequest;
import hemera.model.ethereum.utils.etherunits.Wei;
import hemera.utils.ABIUtils;
import hemera.utils.AddressUtils;
import hemera.utils.UnitUtils;
import io.reactivex.Flowable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class TransactionBot extends AbstractBot {

    private final static Logger log = LoggerFactory.getLogger(TransactionBot.class);

    public TransactionBot(String appId, String ledgerId, String party) {
        super.appId = appId;
        super.ledgerId = ledgerId;
        super.party = party;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Flowable<CommandsAndPendingSet> process(LedgerViewFlowable.LedgerView<Record> ledgerView) {
        List<TransactionRequest.Contract> transactionRequests =
                (List<TransactionRequest.Contract>)(List<?>)getContracts(ledgerView, TransactionRequest.TEMPLATE_ID);

        if (transactionRequests.isEmpty()) {
            return Flowable.empty();
        }

        log.info(String.format("Got %d TransactionRequest(s)", transactionRequests.size()));
        Map<Identifier, Set<String>> pending = new HashMap<>();
        pending.putIfAbsent(TransactionRequest.TEMPLATE_ID, new HashSet<>());

        Map<String, BigInteger> nonces = AddressUtils.fetchAddressNonces(
                Web3jProvider.getInstance().web3j, transactionRequests.stream()
                        .map(c -> c.data.from).collect(Collectors.toList()));

        List<Command> commandList = transactionRequests.stream().map(contract -> {
            pending.get(TransactionRequest.TEMPLATE_ID).add(contract.id.contractId);
            if (nonces.containsKey(contract.data.from)) {
                BigInteger nonce = nonces.get(contract.data.from);
                BigInteger gasPrice = OperatorMain.DEFAULT_GAS_PRICE_WEI;
                BigInteger gas = BigInteger.valueOf(contract.data.optGas.orElse(Long.MAX_VALUE));
                BigInteger weiValue = UnitUtils.fromEtherUnitsToWei(
                        contract.data.optValue.orElse(new Wei(BigDecimal.ZERO)));
                List<Type> functionArgs = contract.data.args.stream()
                        .map(ABIUtils::toWeb3jType).collect(Collectors.toList());
                List<TypeReference<?>> returns = ABIUtils.toWeb3jTypeReference(contract.data.returns);
                Function function = new Function(contract.data.name, functionArgs, returns);
                String encodedFunction = FunctionEncoder.encode(function);
                try {
                    EthEstimateGas ethEstimateGas = Web3jProvider.getInstance().web3j.ethEstimateGas(
                            Transaction.createEthCallTransaction(
                                    contract.data.from,
                                    contract.data.to,
                                    encodedFunction)).send();
                    Thread.sleep(1000);
                    gas = gas.min(ethEstimateGas.getAmountUsed());
                } catch (Exception e) {
                    log.warn(String.format("Could not estimate gas for transaction %s. Will use a gas limit of %s. " +
                                    "Exception: %s", contract.data.name, gas.toString(), e.getMessage()));
                }
                RawTransaction rawTransaction = RawTransaction.createTransaction(
                        nonce,
                        gasPrice,
                        gas,
                        contract.data.to,
                        weiValue,
                        encodedFunction);
                byte[] encodedTransaction = TransactionEncoder.encode(rawTransaction);
                return contract.id.exerciseTransactionRequest_Accept(
                        nonce.longValue(),
                        gas.longValue(),
                        UnitUtils.fromWeiToEtherUnits(gasPrice),
                        Numeric.toHexString(encodedTransaction));
            } else {
                return contract.id.exerciseTransactionRequest_Reject();
            }
        }).collect(Collectors.toList());

        if (!commandList.isEmpty()) {
            return toCommandsAndPendingSet(commandList, pending);
        } else {
            return Flowable.empty();
        }
    }
}
