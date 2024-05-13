import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class Expense {
    public static List<Expense> expensesList = new ArrayList<>();
    
    private String purpose;
    private double amount;
    private Date date;
    private String category;
    private int transactionID;

    //Constructor for friend...
    public Expense(String purpose, double amount, Date date, String category, int transactionID) {
        this.purpose = purpose;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.transactionID = transactionID;
        expensesList.add(this);
    }
    //Getters and setters...
    public String getpurpose() {
        return purpose;
    }

    public void setpurpose(String purpose) {
        this.purpose = purpose;
    }
    public double getamount() {
        return amount;
    }
    public void setamount(double amount) {
        this.amount = amount;
    }

    public Date getdate() {
        return date;
    }

    public void setdate(Date date) {
        this.date = date;
    }

    public String getcategory() {
        return category;
    }

    public void setcategory(String category) {
        this.category = category;
    }

    public int gettransactionID() {
        return transactionID;
    }

    public void settransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public void writeToFile(String fileName) throws IOException {
        FileWriter fw = new FileWriter(fileName, true);
        BufferedWriter bw = new BufferedWriter(fw);
        // Format the date as "yyyy-MM-dd"
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(date);
        
        bw.write(purpose + ", " + amount + ", " + category + ", " + formattedDate + "," + transactionID + ";\n"); 
        bw.close();
    }
    
    @Override
    public String toString() {
        return purpose;
    }
}