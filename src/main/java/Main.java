import auxiliary.Pair;
import exceptions.*;
import file_system.FSConfig;
import file_system.FileSystem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String[] commands = {"cr", "de", "op", "cl", "rd", "wr", "sk", "dr", "in", "sv", "exit"};

    private static String blue(String str){ return (char) 27 + "[34m" + str + (char) 27 + "[0m"; }
    private static String red(String str){ return (char) 27 + "[31m" + str + (char) 27 + "[0m"; }
    private static String yellow(String str){ return (char) 27 + "[33m" + str + (char) 27 + "[0m"; }

    private static final String ErrorEmptyCommand = red("error: Empty command");
    private static final String ErrorNoSuchCommand = red("error: No such command");
    private static final String ErrorMissedArgument = red("error: Missed argument");
    private static final String ErrorIncorrectSyntax = red("error: Incorrect syntax");
    private static final String ErrorUninitializedFileSystem = red("error: File system is not initialized");
    private static final String GenericError = red("error");

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

    private static String getCharForNumber(byte i) {
        return String.valueOf((char) (int) i);
    }

    public static void main(String[] args) {
        //in 4 2 8 64 disk0.txt
        //cr foo
        //op foo
        //wr 1 x 60
        //wr 1 y 10
        //sk 1 55
        //rd 1 10
        //dr
        //sv disk0.txt
        //in 4 2 8 64 disk0.txt
        //op foo
        //rd 1 3
        //cr foo
        //cl 1
        //dr
        FileSystem fs = new FileSystem();

        boolean exit = false;
        boolean initialized = false;
        boolean debug_mode = false;
        Scanner in = new Scanner(System.in);
        String str;

        while (!exit) {
            try {
                str = in.nextLine();
                String[] command = str.split(" ", 6);
                if (!command[0].equals("")) {
                    if (Arrays.asList(commands).contains(command[0])) {
                        // Exit
                        if (command[0].equals("exit")) {
                            if (checkArguments(command, 1)) {
                                System.out.println(blue("closing system"));
                                exit = true;
                            }
                            // Initialize
                        } else if (command[0].equals("in")) {
                            if (checkArguments(command, 6)) {
                                int cylNum = Integer.parseInt(command[1]);
                                int surfNum = Integer.parseInt(command[2]);
                                int sectNum = Integer.parseInt(command[3]);
                                int sectLen = Integer.parseInt(command[4]);
                                String filename = command[5];
                                int status = fs.init(cylNum, surfNum, sectNum, sectLen, filename);
                                if (status == 1)
                                    System.out.println(blue("disk restored"));
                                else if (status == 2)
                                    System.out.println(blue("disk initialized"));
                                initialized = true;
                            }
                            //Check if initialized
                        } else if (!initialized) {
                            System.out.println(ErrorUninitializedFileSystem);
                            // Create
                        } else if (command[0].equals("cr")) {
                            if (checkArguments(command, 2)) {
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
                            if (checkArguments(command, 2)) {
                                String name = command[1];
                                fs.destroy(name);
                                System.out.println(blue("file '" + name + "' destroyed"));
                            }
                            // Open
                        } else if (command[0].equals("op")) {
                            if (checkArguments(command, 2)) {
                                String name = command[1];
                                int oft_index = fs.open(name);
                                System.out.println(blue("file '" + name + "' opened, index = " + String.valueOf(oft_index)));
                            }
                            // Close
                        } else if (command[0].equals("cl")) {
                            if (checkArguments(command, 2)) {
                                int oft_index = Integer.parseInt(command[1]);
                                fs.close(oft_index);
                                System.out.println(blue("file '" + command[1] + "' closed"));

                            }
                            // Read
                        } else if (command[0].equals("rd")) {
                            if (checkArguments(command, 3)) {
                                int oftIndex = Integer.parseInt(command[1]);
                                int count = Integer.parseInt(command[2]);
                                byte[] read = fs.read(oftIndex, count);
                                if (read.length != 0) {
                                    System.out.print(blue(String.valueOf(read.length) + " bytes read: "));
                                    for (byte item : read) {
                                        System.out.print(blue(getCharForNumber(item)));
                                    }
                                    System.out.println();
                                }
                            }
                            // Write
                        } else if (command[0].equals("wr")) {
                            if (checkArguments(command, 4)) {
                                int oft_index = Integer.parseInt(command[1]);
                                String strToWrite = command[2];
                                byte[] strToByte = strToWrite.getBytes(StandardCharsets.UTF_8);
                                int count = Integer.parseInt(command[3]);
                                if (count > 0){
                                    byte[] byteToWrite = new byte[count];
                                    for (int i = 0; i < count; i++) {
                                        byteToWrite[i] = strToByte[0];
                                    }
                                    int res = fs.write(oft_index, byteToWrite);
                                    System.out.println(blue(String.valueOf(res) + " bytes written"));
                                } else {
                                    System.out.println(blue("nothing to write"));
                                }
                            }
                            // Seek
                        } else if (command[0].equals("sk")) {
                            if (checkArguments(command, 3)) {
                                int oft_index = Integer.parseInt(command[1]);
                                int pos = Integer.parseInt(command[2]);
                                int status = fs.seek(oft_index, pos);
                                if (status == 1)
                                    System.out.println(blue("current position is " + String.valueOf(pos)));
                                else
                                    System.out.println(GenericError);
                            }
                            // Directories
                        } else if (command[0].equals("dr")) {
                            if (checkArguments(command, 1)) {
                                List<Pair<String, Integer>> files = fs.getAllFiles();
                                if (files.size() == 0)
                                    System.out.println(blue("directory is empty"));
                                else {
                                    for (int i = 0; i < files.size() - 1; i++) {
                                        System.out.print(blue(files.get(i).first + " " + String.valueOf(files.get(i).second) + ", "));
                                    }
                                    System.out.println(blue(files.get(files.size() - 1).first + " " + String.valueOf(files.get(files.size() - 1).second)));
                                }
                            }
                            // Save
                        } else if (command[0].equals("sv")) {
                            if (checkArguments(command, 2)) {
                                String filename = command[1];
                                fs.save(filename);
                                System.out.println(blue("disk saved"));
                            }
                        }
                    } else {
                        System.out.println(ErrorNoSuchCommand);
                    }
                } else {
                    System.out.println(ErrorEmptyCommand);
                }
            } catch (IOException | OFTException | ReadWriteException | AllocationException | FSException e) {
                System.out.println(GenericError);
                if (debug_mode) {
                    e.printStackTrace();
                }
            }
        }
    }
}
