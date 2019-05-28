package hemera.bots;

import com.daml.ledger.javaapi.data.Command;
import com.daml.ledger.javaapi.data.Identifier;
import com.daml.ledger.javaapi.data.Record;
import com.daml.ledger.rxjava.components.LedgerViewFlowable;
import com.daml.ledger.rxjava.components.helpers.CommandsAndPendingSet;
import hemera.model.ethereum.smartcontract.UnsignedNewContractTransaction;
import hemera.model.ethereum.transaction.UnsignedTransaction;
import hemera.model.ethereum.transfer.UnsignedTrasferTransaction;
import io.reactivex.Flowable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionDecoder;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;

import java.util.*;
import java.util.stream.Collectors;

public class SignBot extends AbstractBot {

    private final static Logger log = LoggerFactory.getLogger(SignBot.class);

    public SignBot(String appId, String ledgerId, String party) {
        super.appId = appId;
        super.ledgerId = ledgerId;
        super.party = party;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Flowable<CommandsAndPendingSet> process(LedgerViewFlowable.LedgerView<Record> ledgerView) {
        List<UnsignedNewContractTransaction.Contract> unsignedNewContractTransactions =
                (List<UnsignedNewContractTransaction.Contract>)(List<?>)
                        getContracts(ledgerView, UnsignedNewContractTransaction.TEMPLATE_ID);
        List<UnsignedTransaction.Contract> unsignedTransactions =
                (List<UnsignedTransaction.Contract>)(List<?>)
                        getContracts(ledgerView, UnsignedTransaction.TEMPLATE_ID);
        List<UnsignedTrasferTransaction.Contract> unsignedTransferTransactions =
                (List<UnsignedTrasferTransaction.Contract>)(List<?>)
                        getContracts(ledgerView, UnsignedTrasferTransaction.TEMPLATE_ID);

        if (unsignedNewContractTransactions.isEmpty()
                && unsignedTransactions.isEmpty()
                && unsignedTransferTransactions.isEmpty()) {
            return Flowable.empty();
        }

        log.info(String.format("Got %d Transactions to Sign", unsignedNewContractTransactions.size()
                + unsignedTransactions.size()
                + unsignedTransferTransactions.size()));

        Map<Identifier, Set<String>> pending = new HashMap<>();
        pending.putIfAbsent(UnsignedNewContractTransaction.TEMPLATE_ID, new HashSet<>());
        pending.putIfAbsent(UnsignedTransaction.TEMPLATE_ID, new HashSet<>());
        pending.putIfAbsent(UnsignedTrasferTransaction.TEMPLATE_ID, new HashSet<>());

        Credentials credentials = Credentials.create("0x" + hemera.ClientMain.PRIVATE_KEY);

        List<Command> commandList = unsignedNewContractTransactions.stream().map(contract -> {
            String txToSign = contract.data.txToSign;
            RawTransaction rawTx = TransactionDecoder.decode(txToSign);
            byte[] signedTx = TransactionEncoder.signMessage(rawTx, credentials);
            String signedTxHex = Numeric.toHexString(signedTx);
            pending.get(UnsignedNewContractTransaction.TEMPLATE_ID).add(contract.id.contractId);
            return contract.id.exerciseUnsignedNewContractTransaction_Sign(signedTxHex);
        }).collect(Collectors.toList());

        List<Command> transactionCommandList = unsignedTransactions.stream().map(contract -> {
            String txToSign = contract.data.txToSign;
            RawTransaction rawTx = TransactionDecoder.decode(txToSign);
            byte[] signedTx = TransactionEncoder.signMessage(rawTx, credentials);
            String signedTxHex = Numeric.toHexString(signedTx);
            pending.get(UnsignedTransaction.TEMPLATE_ID).add(contract.id.contractId);
            return contract.id.exerciseUnsignedTransaction_Sign(signedTxHex);
        }).collect(Collectors.toList());

        List<Command> transferCommandList = unsignedTransferTransactions.stream().map(contract -> {
            String txToSign = contract.data.txToSign;
            RawTransaction rawTx = TransactionDecoder.decode(txToSign);
            byte[] signedTx = TransactionEncoder.signMessage(rawTx, credentials);
            String signedTxHex = Numeric.toHexString(signedTx);
            pending.get(UnsignedTrasferTransaction.TEMPLATE_ID).add(contract.id.contractId);
            return contract.id.exerciseUnsignedTrasferTransaction_Sign(signedTxHex);
        }).collect(Collectors.toList());

        commandList.addAll(transactionCommandList);
        commandList.addAll(transferCommandList);

        if (!commandList.isEmpty()) {
            return toCommandsAndPendingSet(commandList, pending);
        } else {
            return Flowable.empty();
        }
    }
}
