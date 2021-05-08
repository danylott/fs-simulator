import auxiliary.Pair;
import exceptions.*;
import file_system.FSConfig;
import file_system.FileSystem;
import file_system.OftEntry;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String[] commands = {"cr", "de", "op", "cl", "rd", "wr", "sk", "dr", "in", "sv", "exit"};

    private static String blue(String str){ return (char) 27 + "[34m" + str + (char) 27 + "[0m"; }
    private static String red(String str){ return (char) 27 + "[31m" + str + (char) 27 + "[0m"; }
    private static String yellow(String str){ return (char) 27 + "[33m" + str + (char) 27 + "[0m"; }

    private static final String ErrorEmptyCommand = red("Error: Empty command");
    private static final String ErrorNoSuchCommand = red("Error: No such command");
    private static final String ErrorMissedArgument = red("Error: Missed argument");
    private static final String ErrorIncorrectSyntax = red("Error: Incorrect syntax");

    private static final String WarningFileNotFound = yellow("Warning: File not found");

    private ArrayList<String> openedFiles;

    private static boolean checkArguments(String[] command, int required) {
        if (command.length < required) {
            System.out.println(ErrorMissedArgument);
            return false;
        }
        if (command.length != required) {
            String warning = "Warning: Extra argument(s) ";
            for (int i = required; i < command.length; i++){
                warning += "'" + command[i] + "' ";
            }
            System.out.println(yellow(warning));
        }
        return true;
    }

    public static void main(String[] args) throws FileAlreadyExistsException, NoFreeDescriptorException, TooLongFileNameException, TooManyFilesException, FileNotFoundException, FileIsOpenException, IncorrectRWParamsException, NotEnoughFreeBlocksException, FileSizeExceededException {
        FileSystem fs = new FileSystem();
        OftEntry oft = new OftEntry();

        boolean exit = false;
        Scanner in = new Scanner(System.in);
        String str = "";

        while (!exit) {
            str = in.nextLine();
            String[] command = str.split(" ", 6);
            if (!command[0].equals("")){
                if (Arrays.asList(commands).contains(command[0])) {
                    // Exit
                    if (command[0].equals("exit")){
                        if (checkArguments(command, 1)){
                            System.out.println(blue("closing system"));
                            exit = true;
                        }
                    }
                    // Create
                    else if (command[0].equals("cr")){
                        if (checkArguments(command, 2)){
                            String name = command[1];
                            if (name.length() > FSConfig.MAX_FILENAME_LEN - 1)
                                System.out.println(ErrorIncorrectSyntax);
                            else {
                                fs.create(name);
                                System.out.println(blue("file '" + name + "' created"));
                            }
                        }
                    // Destroy
                    } else if (command[0].equals("de")) {
                        if (checkArguments(command, 2)){
                            String name = command[1];
                            fs.destroy(name);
                            System.out.println(blue("file '" + name + "' destroyed"));
                        }
                    // Open
                    } else if (command[0].equals("op")) {
                        if (checkArguments(command, 2)){
                            String name = command[1];
                             int oft_index = fs.open(name);
                             if (oft_index != -1) {
                                 System.out.println(blue("file '" + name + "' opened, index = " + String.valueOf(oft_index)));
                             } else {
                                 System.out.println(WarningFileNotFound);
                             }
                        }
                    // Close
                    } else if (command[0].equals("cl")) {
                        if (checkArguments(command, 2)){
                            int oft_index = Integer.getInteger(command[1]);
                            int status = fs.close(oft_index);
                            if (status == 1)
                                System.out.println(blue("file '" + command[1] + "' closed"));
                            else
                                System.out.println(WarningFileNotFound);
                        }
                    // Read
                    } else if (command[0].equals("rd")) {
                        if (checkArguments(command, 3)){
                            int oftIndex = Integer.getInteger(command[1]);
                            int count = Integer.getInteger(command[2]);
                            byte[] read = fs.read(oftIndex, count);
                            if (read.length != 0){
                                System.out.print(blue(String.valueOf(count) + " bytes read: "));
                                for (byte item : read) {
                                    System.out.print(blue(String.valueOf(item)));
                                }
                                System.out.println();
                            }
                        }
                    // Write
                    } else if (command[0].equals("wr")) {
                        if (checkArguments(command, 4)){
                            int oft_index = Integer.getInteger(command[1]);
                            String strToWrite = command[2];
                            byte[] strToByte = strToWrite.getBytes(StandardCharsets.UTF_8);
                            int count = Integer.getInteger(command[3]);
                            byte[] byteToWrite = new byte[count];
                            for (int i = 0; i < count; i++) {
                                byteToWrite[i] = strToByte[i];
                            }
                            fs.write(oft_index, byteToWrite);
                            System.out.println(blue(String.valueOf(count) + " bytes written"));
                        }
                    // Seek
                    } else if (command[0].equals("sk")) {
                        if (checkArguments(command, 3)){
                            int oft_index = Integer.getInteger(command[1]);
                            int pos = Integer.getInteger(command[2]);
                            int status = fs.seek(oft_index, pos);
                            if (status == 1)
                                System.out.println(blue("current position is " + String.valueOf(pos)));
                            else
                                System.out.println(WarningFileNotFound);
                        }
                    // Directories
                    } else if (command[0].equals("dr")) {
                        if (checkArguments(command, 0)){
                            List<Pair<String, Integer>> files = fs.getAllFiles();
                            if (files.size() == 0)
                                System.out.println(blue("directory is empty"));
                            else {
                                for (int i = 0; i < files.size(); i++) {
                                    System.out.print(blue(files.get(i).first + " " + String.valueOf(files.get(i).second) + " "));
                                }
                                System.out.println();
                            }
                        }
                    // Initialize
                    } else if (command[0].equals("in")) {
                        if (checkArguments(command, 6)){
                            String filename = command[1];

                        }
                    // Save (-)
                    } else if (command[0].equals("sv")) {
                        if (checkArguments(command, 2)){
                            int discCount = Integer.getInteger(command[1]);
                        }
                    }
                }
                else {
                    System.out.println(ErrorNoSuchCommand);
                }
            }
            else {
                System.out.println(ErrorEmptyCommand);
            }
        }
    }
}
