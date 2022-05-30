package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            Utils.exitWithError("Please enter a command.");
        }
        //create a repository instance variable
        Repository repository = new Repository();;
        switch(args[0]) {
            case "init":
                validateNumArgs("init", args, 1);
                //create a new Gitlet version-control system in the current directory.
                repository.initialCommit();
                break;
            case "add":
                validateNumArgs("add", args, 2);
                checkInitialization(repository);
                String fileName = args[1];
                repository.addAFile(fileName);
                break;
            case "commit":
                validateNumArgs("commit", args, 2);
                checkInitialization(repository);
                repository.commit(args[1]);
                break;
            case "checkout":
                checkInitialization(repository);
                switch (args.length) {
                    case 2:
                        repository.checkoutABranch(args[1]);
                        break;
                    case 3:
                        repository.checkoutAFile(args[2]);
                        break;
                    case 4:
                        repository.checkoutAFile(args[1], args[3]);
                        break;
                    default:
                        throw new RuntimeException(
                                String.format("Invalid number of arguments for: checkout"));
                }
                break;
            case "log":
                validateNumArgs("log", args, 1);
                checkInitialization(repository);
                repository.log();
                break;
            default:
                Utils.exitWithError("No command with that name exists.");
        }
    }

    /** Check whether the .gitlet Repo exists in the CWD. */
    public static void checkInitialization(Repository repo) {
        if (!repo.gitletExist()) {
            Utils.exitWithError("Not in an initialized Gitlet directory.");
        }
    }

    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            throw new RuntimeException(
                    String.format("Invalid number of arguments for: %s.", cmd));
        }
    }
}
