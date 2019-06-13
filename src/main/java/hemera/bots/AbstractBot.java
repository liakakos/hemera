package hemera.bots;

import com.daml.ledger.javaapi.data.*;
import com.daml.ledger.rxjava.components.LedgerViewFlowable;
import com.daml.ledger.rxjava.components.helpers.CommandsAndPendingSet;
import com.daml.ledger.rxjava.components.helpers.CreatedContract;
import hemera.model.ethereum.call.CallRequest;
import hemera.model.ethereum.call.CallResponse;
import hemera.model.ethereum.erc20contract.ERC20Contract;
import hemera.model.ethereum.onboarding.Operator;
import hemera.model.ethereum.onboarding.User;
import hemera.model.ethereum.onboarding.UserInvitation;
import hemera.model.ethereum.smartcontract.NewSmartContractRequest;
import hemera.model.ethereum.smartcontract.SignedNewContractTransaction;
import hemera.model.ethereum.smartcontract.UnsignedNewContractTransaction;
import hemera.model.ethereum.transaction.SignedTransaction;
import hemera.model.ethereum.transaction.TransactionRequest;
import hemera.model.ethereum.transaction.UnsignedTransaction;
import hemera.model.ethereum.transfer.SignedTransferTransaction;
import hemera.model.ethereum.transfer.TransferRequest;
import hemera.model.ethereum.transfer.UnsignedTransferTransaction;
import io.reactivex.Flowable;
import io.reactivex.functions.Function3;
import org.pcollections.HashTreePMap;
import org.pcollections.HashTreePSet;
import org.pcollections.PMap;
import org.pcollections.PSet;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

public abstract class AbstractBot {
    protected String appId;
    protected String party;
    protected String ledgerId;

    public abstract Flowable<CommandsAndPendingSet> process(LedgerViewFlowable.LedgerView<Record> ledgerView);
    public Record getRecordFromContract(CreatedContract contract) {
        return contract.getCreateArguments();
    }

    protected Flowable<CommandsAndPendingSet> toCommandsAndPendingSet(List<Command> commandList, Map<Identifier, Set<String>> pending) {
        SubmitCommandsRequest commandsRequest = new SubmitCommandsRequest(
                UUID.randomUUID().toString(),
                appId,
                UUID.randomUUID().toString(),
                party,
                Instant.EPOCH,
                Instant.EPOCH.plusSeconds(10),
                commandList
        );

        CommandsAndPendingSet commandsAndPendingSet = new CommandsAndPendingSet(commandsRequest, toPMapPSet(pending));
        return Flowable.fromIterable(Stream.of(commandsAndPendingSet)::iterator);
    }

    protected List<Contract> getContracts(LedgerViewFlowable.LedgerView<Record> ledgerView, Identifier templateId) {
        Function3<String, Record, Optional<String>, Contract> decoder = Optional.ofNullable(decoders.get(templateId))
                .orElseThrow(() -> new IllegalArgumentException("No template found for identifier " + templateId));

        List<Contract> contractList = new ArrayList<>();
        ledgerView.getContracts(templateId).forEach((key, value) -> {
            try {
                Contract contract = decoder.apply(key, value, Optional.empty());
                contractList.add(contract);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return contractList;
    }

    private PMap<Identifier, PSet<String>> toPMapPSet(Map<Identifier, Set<String>> pending) {
        Map<Identifier, PSet<String>> pPending = new HashMap<>();
        for (Map.Entry<Identifier, Set<String>> entry : pending.entrySet()) {
            pPending.put(entry.getKey(), HashTreePSet.from(entry.getValue()));
        }

        return HashTreePMap.from(pPending);
    }

    private static HashMap<Identifier, Function3<String, Record, Optional<String>, Contract>> decoders;

    static {
        decoders = new HashMap<>();
        decoders.put(SignedTransaction.TEMPLATE_ID, SignedTransaction.Contract::fromIdAndRecord);
        decoders.put(TransactionRequest.TEMPLATE_ID, TransactionRequest.Contract::fromIdAndRecord);
        decoders.put(UnsignedTransaction.TEMPLATE_ID, UnsignedTransaction.Contract::fromIdAndRecord);
        decoders.put(ERC20Contract.TEMPLATE_ID, ERC20Contract.Contract::fromIdAndRecord);
        decoders.put(SignedNewContractTransaction.TEMPLATE_ID, SignedNewContractTransaction.Contract::fromIdAndRecord);
        decoders.put(NewSmartContractRequest.TEMPLATE_ID, NewSmartContractRequest.Contract::fromIdAndRecord);
        decoders.put(UnsignedNewContractTransaction.TEMPLATE_ID, UnsignedNewContractTransaction.Contract::fromIdAndRecord);
        decoders.put(Operator.TEMPLATE_ID, Operator.Contract::fromIdAndRecord);
        decoders.put(UserInvitation.TEMPLATE_ID, UserInvitation.Contract::fromIdAndRecord);
        decoders.put(User.TEMPLATE_ID, User.Contract::fromIdAndRecord);
        decoders.put(SignedTransferTransaction.TEMPLATE_ID, SignedTransferTransaction.Contract::fromIdAndRecord);
        decoders.put(UnsignedTransferTransaction.TEMPLATE_ID, UnsignedTransferTransaction.Contract::fromIdAndRecord);
        decoders.put(TransferRequest.TEMPLATE_ID, TransferRequest.Contract::fromIdAndRecord);
        decoders.put(CallResponse.TEMPLATE_ID, CallResponse.Contract::fromIdAndRecord);
        decoders.put(CallRequest.TEMPLATE_ID, CallRequest.Contract::fromIdAndRecord);
    }
}
