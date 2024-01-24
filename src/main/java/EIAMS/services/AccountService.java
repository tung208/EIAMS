package EIAMS.services;

import EIAMS.entities.Account;
import EIAMS.entities.Student;
import EIAMS.helper.Pagination;
import EIAMS.repositories.AccountRepository;
import EIAMS.services.interfaces.AccountServiceInterface;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountService implements AccountServiceInterface {
    private final AccountRepository accountRepository;
    private final Pagination pagination;
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Override
    public Page<Account> list(Integer page, Integer limit) {
        Pageable pageable = pagination.getPageable(page, limit);
        return accountRepository.findAll(pageable);
    }

    @Override
    public List<Account> list() {
        return accountRepository.findAll();
    }

    @Override
    public void exportListAccount(List<Account> accounts, String filePath) {
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath))) {
            // Writing header
            String[] header = {"ID", "Active", "Email", "Role", "Username"};
            csvWriter.writeNext(header);

            // Writing data
            for (Account account : accounts) {
                String[] data = {
                        String.valueOf(account.getId()),
                        String.valueOf(account.getActive()),
                        account.getEmail(),
                        account.getRole()
                };
                csvWriter.writeNext(data);
            }
            System.out.println("CSV file exported successfully!");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void importFileCSV(MultipartFile file) throws IOException {
        Map<Integer, Account> csvDataMap = new HashMap<>();
        List<Account> newAccount = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                Integer id = StringUtils.hasText(data[0]) ? Integer.parseInt(data[0]) : null;
                Integer active = StringUtils.hasText(data[1]) ? Integer.parseInt(data[1]) : null;
                String email = StringUtils.hasText(data[2]) ? data[2] : null;
                String role = StringUtils.hasText(data[3]) ? data[3] : null;

                if (id != null && active != null && email != null && role != null) {
                    Account account = new Account();
                    account.setId(id);
                    account.setActive(active);
                    account.setRole(role);
                    account.setEmail(email);
                    account.setUsername(getUserName(email));
                    csvDataMap.put(id, account);
                } else if (id == null && active != null && email != null && role != null) {
                    Account account = new Account();
                    account.setActive(active);
                    account.setRole(role);
                    account.setEmail(email);
                    account.setUsername(getUserName(email));
                    newAccount.add(account);
                } else {
                    // Handle the case where any required field is missing or invalid
                    System.out.println("Skipping invalid data: " + line);
                }
            }
        }
        List<Account> existingAccounts = accountRepository.findAll();
        for (Account existingAccount: existingAccounts){
            int id = existingAccount.getId();
            if (csvDataMap.containsKey(id)) {
                //TODO: update exist account and delete not exist
                Account accountUpdate = csvDataMap.get(id);
            }
        }

        accountRepository.saveAll(newAccount);

    }

    public String getUserName(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }
        int atIndex = email.indexOf('@');
        if (atIndex != -1) {
            return email.substring(0, atIndex);
        } else {
            return null;
        }
    }
}
