package fileparser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *TEST
 * @author A.Smith
 */
public class FileParser {

    public static void main(String[] args) {
        //Read text file to list the bus stops
        //LOAD INVENTORY MASS FILE MUTUAL
        //loadInventory();
        //loadAR();
        //loadDebbieAccounts();
        loadCards();

    }

    public static void loadCards() {
        try {
            BufferedReader in = new BufferedReader(new FileReader("C:\\Users\\A.Smith\\Documents\\pos\\cards.csv"));
            String line;
            String actualLine = "";
            int cntr = 0;
            while ((line = in.readLine()) != null) {
                String[] tokens = line.split(":");

                if (line != null && !line.isEmpty()) {

                    line = line.trim();
                    String upc = line.substring(line.indexOf(',') + 1);
                    String price = upc.substring(upc.indexOf(',') + 1);
                    String cost = price.substring(price.indexOf(',') + 1);
                    price = price.substring(price.indexOf('$') + 1, price.indexOf(',') - 1);
                    cost = cost.substring(cost.indexOf('$') + 1);
                    upc = 0+upc.substring(0, upc.indexOf(","));
                    upc = upc.trim();
                    upc= upc.substring(0, upc.length()-1);
                    String id = "C"+upc.substring(6, 11);

//068981076964
        
                    double actualPrice = Double.parseDouble(price);
                    double actualCost = Double.parseDouble(cost);
                   // System.out.println("DELETE FROM inventory where category = 856;");
                     System.out.println("INSERT INTO `inventory` (`pid`,`mutID`,`upc`,`name`,`price`,`cost`,`taxable`,`category`) VALUES (NULL, '" + id + "','" + upc + "','" + id + "'," + actualPrice + "," + actualCost + ",true," + 856 + ");");
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("The file could not be found or opened");
        } catch (IOException e) {
            System.out.println("Error reading the file");
        }

    }

    public static void loadDebbieAccounts() {
        try {
            BufferedReader in = new BufferedReader(new FileReader("C:\\Users\\A.Smith\\Documents\\pos\\patients.txt"));
            String line;
            String actualLine = "";
            int cntr = 0;
            while ((line = in.readLine()) != null) {
                String[] tokens = line.split(":");

                if (line != null && !line.isEmpty()) {

                    line = line.trim();
                    if (line != null && !line.isEmpty()) {
                        line = line.trim();
                        if (!line.contains("Work") && !line.contains("Telephone") && !line.contains("Page") && !line.contentEquals("PAN") && !line.contains("Patient") && !line.contains("Birthday") && !line.contains("Home") && line.charAt(0) != ' ' && line.charAt(0) != '(' && !line.contains("12/04/2017")) {
                            // System.out.println(line);
                            if (cntr == 0) {//PAN
                                actualLine = line;
                                cntr++;
                                //  System.out.println("PAN : "+line);
                            } else if (cntr == 1) {//NAME
                                //if(line.contains(",")){
                                String temp = line.substring(0, line.indexOf(','));
                                temp += ":" + line.substring(line.indexOf(',') + 2);
                                actualLine += ":" + temp;
                                cntr++;
                                // }
                                //   System.out.println("NAME : "+line);
                            } else if (cntr == 2) {//DOB
                                actualLine += ":" + line + ":";
                                cntr++;
                                // System.out.println("DOB : "+line);
                                //System.out.println(actualLine);

                                String accountName = actualLine.substring(0, actualLine.indexOf(":"));
                                actualLine = actualLine.substring(actualLine.indexOf(":") + 1);
                                String lastName = actualLine.substring(0, actualLine.indexOf(":"));
                                actualLine = actualLine.substring(actualLine.indexOf(":") + 1);
                                String firstName = actualLine.substring(0, actualLine.indexOf(":"));
                                actualLine = actualLine.substring(actualLine.indexOf(":") + 1);
                                String dob = actualLine.substring(0, actualLine.indexOf(":"));;
                                dob = dob.substring(0, 2) + dob.substring(3, 5) + dob.substring(8);
                                System.out.println("INSERT INTO `dmeaccounts` (`pid`,`pan`,`firstname`,`lastname`,`dob`) VALUES (NULL, '" + accountName + "','" + lastName + "','" + firstName + "','" + dob + "');");

                                actualLine = "";
                                cntr = 0;

                            } else if (cntr == 3) {//SKIP TELE

                            }
                        }
                    }
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("The file could not be found or opened");
        } catch (IOException e) {
            System.out.println("Error reading the file");
        }

    }

    public static void loadInventory() {
        try {

            BufferedReader in = new BufferedReader(new FileReader("C:\\Users\\A.Smith\\Documents\\pos\\files.pos"));

            String line;
            while ((line = in.readLine()) != null) {
                String[] tokens = line.split(":");
                for (String s : tokens) {

                    //System.out.println(s);
                }
                //WORKING UPC
                String upc = line.substring(0, 11);
                //System.out.println("UPC: "+upc);

                //WORKING NAME
                String name = line.substring(26, 71);
                name = name.replace("'", " ");
                //System.out.println("NAME: "+name);

                //WORKING PRICE
                String price = line.substring(107, 113);
                Double d = Double.parseDouble(price);
                d = d / 100;
                //System.out.println("PRICE: "+d);

                //WORKING MUTUAL ID
                String mutID = line.substring(114, 117);
                mutID += line.substring(118, 121);
                //System.out.println("mutual ID: "+mutID);

                //COST
                String costTemp = line.substring(90, 99);
                Double cost = Double.parseDouble(costTemp);
                cost = cost / 1000;
                //System.out.println("COST: "+cost);

                //ITEM CATEGORY CODE
                String code = line.substring(76, 79);
                int actualCode = Integer.parseInt(code);

                if (line.substring(80, 82).contentEquals("CS") && !name.contains("CS")) {
                    if (line.contains("(") && line.contains(")")) {
                        String tempNumber = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
                        if (isAllDigits(tempNumber)) {
                            int number = Integer.parseInt(tempNumber);
                            cost /= number;
                            cost = round(cost);
                        }
                    }
                } else if (name.contains("CS/")) {
                    String temp = line.substring(line.indexOf('/') + 1);
                    temp = temp.substring(0, temp.indexOf(" "));
                    if (isAllDigits(temp)) {
                        int number = Integer.parseInt(temp);
                        cost /= number;
                        cost = round(cost);
                    }
                }
                //System.out.println("CODE: "+code);
                // System.out.println("UPC: "+upc+" Item: "+name+" Cost: "+cost+" Price: "+d+" Mututal ID: "+mutID);
                if (actualCode == 11 || actualCode == 12 || actualCode == 31 || actualCode == 32 || actualCode == 151 || actualCode == 152 || actualCode == 153 || actualCode == 154 || actualCode == 252 || actualCode == 371 || actualCode == 372 || actualCode == 471 || actualCode == 651) {
                    System.out.println("INSERT INTO `inventory` (`pid`,`mutID`,`upc`,`name`,`price`,`cost`,`taxable`,`category`) VALUES (NULL, '" + mutID + "','" + upc + "','" + name + "'," + d + "," + cost + ",false," + actualCode + ");");
                } else {
                    System.out.println("INSERT INTO `inventory` (`pid`,`mutID`,`upc`,`name`,`price`,`cost`,`taxable`,`category`) VALUES (NULL, '" + mutID + "','" + upc + "','" + name + "'," + d + "," + cost + ",true," + actualCode + ");");
                }
            }//end while

        } catch (FileNotFoundException e) {
            System.out.println("The file could not be found or opened");
        } catch (IOException e) {
            System.out.println("Error reading the file");
        }

    }

    public static boolean isAllDigits(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static void loadAR() {
        try {

            BufferedReader in = new BufferedReader(new FileReader("C:\\Users\\A.Smith\\Downloads\\Accounts.csv"));

            int cntr = 1;
            String line, accntName = "", lastName = "", firstName = "", dob = "";
            while ((line = in.readLine()) != null) {
                String[] tokens = line.split(",");
                /// System.out.println(tokens[0]);
                //System.out.println(tokens[1]);
                //System.out.println(tokens[2]);
                //System.out.println(tokens[3].replaceAll("/", ""));
                System.out.println("INSERT INTO `chargeaccounts` (`pid`,`accntname`,`lastname`,`firstname`,`dob`) VALUES (NULL, '" + tokens[0] + "','" + tokens[1] + "','" + tokens[2] + "','" + tokens[3].replaceAll("/", "") + "');");
                // for (String s : tokens) {

                //System.out.println(s);
                // }
            }

            //System.out.println("CODE: "+code);
            // System.out.println("UPC: "+upc+" Item: "+name+" Cost: "+cost+" Price: "+d+" Mututal ID: "+mutID);
            // if(actualCode==11||actualCode==12||actualCode==31||actualCode==32||actualCode==151||actualCode==152||actualCode==153||actualCode==154||actualCode==252||actualCode==371||actualCode==372||actualCode==471||actualCode==651){
            // System.out.println("INSERT INTO `inventory` (`pid`,`mutID`,`upc`,`name`,`price`,`cost`,`taxable`,`category`) VALUES (NULL, "+mutID+","+upc+",'"+name+"',"+d+","+cost+",false,"+actualCode+");");
            // }else{
            // System.out.println("INSERT INTO `inventory` (`pid`,`mutID`,`upc`,`name`,`price`,`cost`,`taxable`,`category`) VALUES (NULL, "+mutID+","+upc+",'"+name+"',"+d+","+cost+",true,"+actualCode+");");
            // }
//}//end while
        } catch (FileNotFoundException e) {
            System.out.println("The file could not be found or opened");
        } catch (IOException e) {
            System.out.println("Error reading the file");
        }

    }

    private static double round(double num) {//rounds to 2 decimal places.
        num = Math.round(num * 100.0) / 100.0;
        return num;
    }//end round
}
