package hemera.bots;

import com.daml.ledger.javaapi.data.*;
import com.daml.ledger.rxjava.components.LedgerViewFlowable;
import com.daml.ledger.rxjava.components.helpers.CommandsAndPendingSet;
import com.daml.ledger.rxjava.components.helpers.CreatedContract;
import hemera.TemplateDecoder;
import io.reactivex.Flowable;
import org.pcollections.HashTreePMap;
import org.pcollections.HashTreePSet;
import org.pcollections.PMap;
import org.pcollections.PSet;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
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
        BiFunction<String, Record, Contract> fromIdAndRecord = TemplateDecoder.getDecoder(templateId)
                .orElseThrow(() -> new IllegalArgumentException("No template found for identifier " + templateId));
        return ledgerView.getContracts(templateId).entrySet().stream()
                .map(kv -> fromIdAndRecord.apply(kv.getKey(), kv.getValue())).collect(Collectors.toList());
    }

    private PMap<Identifier, PSet<String>> toPMapPSet(Map<Identifier, Set<String>> pending) {
        Map<Identifier, PSet<String>> pPending = new HashMap<>();
        for (Map.Entry<Identifier, Set<String>> entry : pending.entrySet()) {
            pPending.put(entry.getKey(), HashTreePSet.from(entry.getValue()));
        }

        return HashTreePMap.from(pPending);
    }
}
