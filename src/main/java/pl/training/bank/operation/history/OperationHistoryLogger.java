package pl.training.bank.operation.history;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import pl.training.bank.account.Account;
import pl.training.bank.account.AccountNotFoundException;
import pl.training.bank.account.AccountRepository;
import pl.training.bank.disposition.Disposition;

import java.util.Date;

@Aspect
@RequiredArgsConstructor
public class OperationHistoryLogger {

    @NonNull
    private OperationHistoryRepository operationHistoryRepository;
    @NonNull
    private AccountRepository accountRepository;

    @AfterReturning("execution(void pl.training.bank.disposition.DispositionService.process(..)) && args(disposition)")
    public void logOperation(Disposition disposition) {
        Account account = getAccount(disposition.getAccountNumber());
        OperationHistoryEntry historyEntry = createOperationHistoryEntry(disposition, account);
        operationHistoryRepository.save(historyEntry);
    }

    private Account getAccount(String accountNumber) {
        return accountRepository.getByNumber(accountNumber)
                .orElseThrow(AccountNotFoundException::new);
    }

    private OperationHistoryEntry createOperationHistoryEntry(Disposition disposition, Account account) {
        OperationHistoryEntry historyEntry = new OperationHistoryEntry();
        historyEntry.setAccount(account);
        historyEntry.setDate(new Date());
        historyEntry.setFunds(disposition.getFunds());
        historyEntry.setType(disposition.getOperationName());
        return historyEntry;
    }

}