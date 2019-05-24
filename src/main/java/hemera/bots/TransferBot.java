package hemera.bots;

import com.daml.ledger.javaapi.data.Command;
import com.daml.ledger.javaapi.data.Identifier;
import com.daml.ledger.javaapi.data.Record;
import com.daml.ledger.rxjava.components.LedgerViewFlowable;
import com.daml.ledger.rxjava.components.helpers.CommandsAndPendingSet;
import hemera.Web3jProvider;
import hemera.model.ethereum.transfer.TransferRequest;
import hemera.utils.AddressUtils;
import hemera.utils.UnitUtils;
import io.reactivex.Flowable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class TransferBot extends AbstractBot {

    private final static Logger log = LoggerFactory.getLogger(TransferBot.class);
    private final static long defaultGasLimit = 50000;
    private final static BigInteger defaultGasPriceWei = new BigInteger("20000000000");

    public TransferBot(String appId, String ledgerId, String party) {
        super.appId = appId;
        super.ledgerId = ledgerId;
        super.party = party;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Flowable<CommandsAndPendingSet> process(LedgerViewFlowable.LedgerView<Record> ledgerView) {
        List<TransferRequest.Contract> transferRequests =
                (List<TransferRequest.Contract>) (List<?>) getContracts(ledgerView, TransferRequest.TEMPLATE_ID);

        if (transferRequests.isEmpty()) {
            return Flowable.empty();
        }

        log.info(String.format("Got %d TransferRequest(s)", transferRequests.size()));
        Map<Identifier, Set<String>> pending = new HashMap<>();
        pending.putIfAbsent(TransferRequest.TEMPLATE_ID, new HashSet<>());

        Map<String, BigInteger> nonces = AddressUtils.fetchAddressNonces(
                Web3jProvider.getInstance().web3j, transferRequests.stream()
                        .map(c -> c.data.from).collect(Collectors.toList()));

        List<Command> commandList = transferRequests.stream().map(contract -> {
            pending.get(TransferRequest.TEMPLATE_ID).add(contract.id.contractId);
            if (nonces.containsKey(contract.data.from)) {
                BigInteger nonce = nonces.get(contract.data.from);
                BigInteger gasPrice = defaultGasPriceWei;
                BigInteger gas = new BigInteger(String.valueOf(contract.data.optGas.orElse(defaultGasLimit)));
                BigInteger weiValue = UnitUtils.fromEtherUnitsToWei(contract.data.value);
                RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                        nonce,
                        gasPrice,
                        gas,
                        contract.data.to,
                        weiValue);
                byte[] encodedTransaction = TransactionEncoder.encode(rawTransaction);
                pending.get(TransferRequest.TEMPLATE_ID).add(contract.id.contractId);
                return contract.id.exerciseTransferRequest_Accept(
                        nonce.longValue(),
                        defaultGasLimit,
                        UnitUtils.fromWeiToEtherUnits(weiValue),
                        Numeric.toHexString(encodedTransaction)
                );
            } else {
                return contract.id.exerciseTransferRequest_Reject();
            }
        }).collect(Collectors.toList());

        if (!commandList.isEmpty()) {
            return toCommandsAndPendingSet(commandList, pending);
        } else {
            return Flowable.empty();
        }
    }
}
