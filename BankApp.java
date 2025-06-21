import java.util.*;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

class InvalidAmountException extends Exception {
    public InvalidAmountException(String message) {
        super(message);
    }
}

class AccountNotFoundException extends Exception {
    public AccountNotFoundException(String message) {
        super(message);
    }
}

class BalanceLimitException extends Exception {
    public BalanceLimitException(String message) {
        super(message);
    }
}

class Account {
    private long accountId;
    private String accountHolder;
    private double balance;
    private static final double MIN_BALANCE = 0.0;
    private static final double MAX_BALANCE = 999999.99;
    private static final double MIN_DEPOSIT = 0.01;
    private static final double MAX_DEPOSIT = 10000.0;
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("^\\d+(\\.\\d{1,2})?$");

    public Account(long accountId, String accountHolder, double initialBalance)
            throws InvalidAmountException, BalanceLimitException {
        validateAmount(initialBalance, "Initial balance");
        validateBalanceLimit(initialBalance);

        this.accountId = accountId;
        this.accountHolder = accountHolder;
        this.balance = initialBalance;
    }

    public long getAccountId() {
        return accountId;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public double getBalance() {
        return balance;
    }

    public void setAccountHolder(String accountHolder) {
        if (accountHolder != null && !accountHolder.trim().isEmpty()) {
            this.accountHolder = accountHolder;
        } else {
            throw new IllegalArgumentException("Account holder name cannot be empty");
        }
    }

    private void validateAmount(double amount, String operation) throws InvalidAmountException {
        if (amount < 0) {
            throw new InvalidAmountException(operation + " amount cannot be negative");
        }

        String amountStr = String.valueOf(amount);
        if (amountStr.contains(".")) {
            String[] parts = amountStr.split("\\.");
            if (parts.length > 1 && parts[1].length() > 2) {
                throw new InvalidAmountException(operation + " amount can have maximum 2 decimal places");
            }
        }
    }

    private void validateBalanceLimit(double newBalance) throws BalanceLimitException {
        if (newBalance < MIN_BALANCE) {
            throw new BalanceLimitException("Balance cannot be less than $" +
                    String.format("%.2f", MIN_BALANCE));
        }
        if (newBalance > MAX_BALANCE) {
            throw new BalanceLimitException("Balance cannot exceed $" +
                    String.format("%.2f", MAX_BALANCE));
        }
    }

    private void validateDepositRange(double amount) throws InvalidAmountException {
        if (amount < MIN_DEPOSIT || amount > MAX_DEPOSIT) {
            throw new InvalidAmountException("Deposit amount must be between $" +
                    String.format("%.2f", MIN_DEPOSIT) + " and $" + String.format("%.2f", MAX_DEPOSIT));
        }
    }

    // Deposit method
    public void deposit(double amount) throws InvalidAmountException, BalanceLimitException {
        validateAmount(amount, "Deposit");
        validateDepositRange(amount);

        double newBalance = balance + amount;
        validateBalanceLimit(newBalance);

        balance = newBalance;
    }

    public void withdraw(double amount)
            throws InvalidAmountException, InsufficientFundsException, BalanceLimitException {
        validateAmount(amount, "Withdrawal");

        if (amount > balance) {
            throw new InsufficientFundsException("Insufficient funds. Available balance: $" +
                    String.format("%.2f", balance));
        }

        double newBalance = balance - amount;
        validateBalanceLimit(newBalance);

        balance = newBalance;
    }

    protected void transfer(double amount, Account toAccount)
            throws InvalidAmountException, InsufficientFundsException, BalanceLimitException {
        validateAmount(amount, "Transfer");

        if (amount > balance) {
            throw new InsufficientFundsException("Insufficient funds for transfer. Available balance: $" +
                    String.format("%.2f", balance));
        }

        double newFromBalance = balance - amount;
        double newToBalance = toAccount.balance + amount;

        validateBalanceLimit(newFromBalance);
        validateBalanceLimit(newToBalance);

        balance = newFromBalance;
        toAccount.balance = newToBalance;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#0.00");
        return String.format("ACC_ID: %010d\nACC_HOLDER: %s\nACC_BALANCE: %s$",
                accountId, accountHolder, df.format(balance));
    }
}

class BankManagement {
    private List<Account> accounts;
    private long nextAccountId;
    private Scanner scanner;

    public BankManagement() {
        accounts = new ArrayList<>();
        nextAccountId = 111;
        scanner = new Scanner(System.in);

        initializeSampleAccounts();
    }

    private void initializeSampleAccounts() {
        try {
            accounts.add(new Account(111, "Sitha", 403.34));
            accounts.add(new Account(222, "Rith", 803.34));
            accounts.add(new Account(333, "Heng", 603.34));
            accounts.add(new Account(4444, "Lin", 203.34));
            nextAccountId = 10001;
        } catch (Exception e) {
            System.out.println("Error initializing sample accounts: " + e.getMessage());
        }
    }

    public void displayMenu() {
        System.out.println(":::: The Bank ::::");
        System.out.println("1.  Account List");
        System.out.println("2.  Create an account");
        System.out.println("3.  Deposit to an account");
        System.out.println("4.  Withdraw from an account");
        System.out.println("5.  Transfer to another account");
        System.out.println("6.  Quit");
        System.out.print("Choose an opt: ");
    }

    public void displayAccountList() {
        System.out.println(":::: Account List ::::");
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }

        for (int i = 0; i < accounts.size(); i++) {
            System.out.println("No #" + (i + 1));
            System.out.println(accounts.get(i));
            System.out.println();
        }
    }

    public void createAccount() {
        try {
            System.out.print("Enter account holder name: ");
            String name = scanner.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("Error: Account holder name cannot be empty.");
                return;
            }

            System.out.print("Enter initial balance: $");
            String balanceInput = scanner.nextLine().trim();

            double initialBalance = parseAmount(balanceInput);

            Account newAccount = new Account(nextAccountId++, name, initialBalance);
            accounts.add(newAccount);

            System.out.println("Account created successfully!");
            System.out.println("Account ID: " + newAccount.getAccountId());

        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid amount format. Please enter a valid number.");
        } catch (InvalidAmountException | BalanceLimitException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error creating account: " + e.getMessage());
        }
    }

    public void depositToAccount() {
        try {
            System.out.print("Enter account ID: ");
            long accountId = Long.parseLong(scanner.nextLine().trim());

            Account account = findAccount(accountId);

            System.out.print("Enter deposit amount: $");
            String amountInput = scanner.nextLine().trim();
            double amount = parseAmount(amountInput);

            account.deposit(amount);
            System.out.println("Deposit successful!");
            System.out.println("New balance: $" + String.format("%.2f", account.getBalance()));

        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid input format.");
        } catch (AccountNotFoundException | InvalidAmountException | BalanceLimitException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error processing deposit: " + e.getMessage());
        }
    }

    public void withdrawFromAccount() {
        try {
            System.out.print("Enter account ID: ");
            long accountId = Long.parseLong(scanner.nextLine().trim());

            Account account = findAccount(accountId);

            System.out.print("Enter withdrawal amount: $");
            String amountInput = scanner.nextLine().trim();
            double amount = parseAmount(amountInput);

            account.withdraw(amount);
            System.out.println("Withdrawal successful!");
            System.out.println("New balance: $" + String.format("%.2f", account.getBalance()));

        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid input format.");
        } catch (AccountNotFoundException | InvalidAmountException | InsufficientFundsException
                | BalanceLimitException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error processing withdrawal: " + e.getMessage());
        }
    }

    public void transferBetweenAccounts() {
        try {
            System.out.print("Enter source account ID: ");
            long fromAccountId = Long.parseLong(scanner.nextLine().trim());

            System.out.print("Enter destination account ID: ");
            long toAccountId = Long.parseLong(scanner.nextLine().trim());

            if (fromAccountId == toAccountId) {
                System.out.println("Error: Cannot transfer to the same account.");
                return;
            }

            Account fromAccount = findAccount(fromAccountId);
            Account toAccount = findAccount(toAccountId);

            System.out.print("Enter transfer amount: $");
            String amountInput = scanner.nextLine().trim();
            double amount = parseAmount(amountInput);

            fromAccount.transfer(amount, toAccount);

            System.out.println("Transfer successful!");
            System.out.println("From Account Balance: $" + String.format("%.2f", fromAccount.getBalance()));
            System.out.println("To Account Balance: $" + String.format("%.2f", toAccount.getBalance()));

        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid input format.");
        } catch (AccountNotFoundException | InvalidAmountException | InsufficientFundsException
                | BalanceLimitException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error processing transfer: " + e.getMessage());
        }
    }

    private Account findAccount(long accountId) throws AccountNotFoundException {
        for (Account account : accounts) {
            if (account.getAccountId() == accountId) {
                return account;
            }
        }
        throw new AccountNotFoundException("Account with ID " + accountId + " not found.");
    }

    private double parseAmount(String amountStr) throws InvalidAmountException {
        if (amountStr == null || amountStr.trim().isEmpty()) {
            throw new InvalidAmountException("Amount cannot be empty.");
        }

        amountStr = amountStr.replace("$", "").trim();

        if (!amountStr.matches("^\\d+(\\.\\d{1,2})?$")) {
            throw new InvalidAmountException("Invalid amount format. Use format like: 100.50");
        }

        try {
            double amount = Double.parseDouble(amountStr);

            if (amountStr.contains(".")) {
                String[] parts = amountStr.split("\\.");
                if (parts.length > 1 && parts[1].length() > 2) {
                    throw new InvalidAmountException("Amount cannot have more than 2 decimal places.");
                }
            }

            return amount;
        } catch (NumberFormatException e) {
            throw new InvalidAmountException("Invalid amount format.");
        }
    }

    public void run() {
        System.out.println("Welcome to the Bank Management System!");

        while (true) {
            try {
                System.out.println();
                displayMenu();

                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        System.out.println();
                        System.out.println("Option: 1");
                        displayAccountList();
                        break;
                    case "2":
                        System.out.println();
                        System.out.println("Option: 2");
                        createAccount();
                        break;
                    case "3":
                        System.out.println();
                        System.out.println("Option: 3");
                        depositToAccount();
                        break;
                    case "4":
                        System.out.println();
                        System.out.println("Option: 4");
                        withdrawFromAccount();
                        break;
                    case "5":
                        System.out.println();
                        System.out.println("Option: 5");
                        transferBetweenAccounts();
                        break;
                    case "6":
                        System.out.println("Thank you for using the Bank Management System!");
                        return;
                    default:
                        System.out.println("Invalid option. Please choose 1-6.");
                }
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }
    }
}

public class BankApp {
    public static void main(String[] args) {
        BankManagement bank = new BankManagement();
        bank.run();
    }
}