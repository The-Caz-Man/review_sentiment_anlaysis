import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
//NAME: Caz Cherepak
//ASSIGNMENT: Program 3
//LAB SECTION: Tuesday 8am
//LECTURE SECTION: Tuesday Thursday 11:30am

public class Main{

    public static void main(String[] args) throws FileNotFoundException {
        ArrayList<SentList> sentList = new ArrayList<SentList>();
        ArrayList<SentList> posList = new ArrayList<SentList>();
        ArrayList<SentList> negList = new ArrayList<SentList>();
        ArrayList<Review> reviewList = new ArrayList<Review>();

        //load sentiment, positive words and negative words arraylists
        readSentimentList(sentList, posList, negList);
        
        //Load reviewList
        for (int i = 1; i <= 8; i++) {
            String tempFileName = "review" + i + ".txt";
            Review tempReview = new Review(tempFileName, sentList, posList, negList);
            reviewList.add(tempReview);
        }
       

        String outFileName = "reviews.txt";
        PrintWriter outFile = new PrintWriter(outFileName);

        for (Review r: reviewList) {
            if (!r.getBadFile()) {
                outFile.printf("Original review for file: %s\n\n", r.getFileName());
                outFile.printf("%s\n\n", r.getFormattedReview());
                outFile.printf("Original sentiment: %.2f\n\n", r.getSentValue());
                outFile.printf("Positive review for file: %s\n", r.getFileName());
                if (r.getMostPos()) {
                    outFile.println("Review cannot be more positive.\n");
                } else {
                    outFile.printf("%s\n", r.getPosFormatted());
                    outFile.printf("%s\n\n", r.getToPosData());
                    outFile.printf("Positive sentiment: %.2f\n\n", r.getPosSentValue());
                }
                outFile.printf("Negative review for file: %s\n", r.getFileName());
                if (r.getMostNeg()) {
                    outFile.println("Review cannot be more negative.\n");
                } else {
                    outFile.printf("%s\n", r.getNegFormatted());
                    outFile.printf("%s\n\n", r.getToNegData());
                    outFile.printf("Negative sentiment: %.2f\n\n", r.getNegSentValue());
                }
            } else {
                continue;
            }
        }
        

        outFile.close();
    }

    //PRE:  accept the empty ArrayLists created in main
    //POST: the arrays are loaded with the proper words and information
    public static void readSentimentList(ArrayList<SentList> sentList,
                                         ArrayList<SentList> posList,
                                         ArrayList<SentList> negList){
        String csvFilePath = "sentiment.txt";
        String line;
        double tempValue;
        String tempWord;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            while ((line = br.readLine()) != null) {
                try {
                    String [] lineElements = line.split(",");

                    //Assign the word and float to temporary variables to use in SentList instance.
                    
                    tempValue = Double.parseDouble(lineElements[1]);
                    tempWord = lineElements[0].trim();
                
            

                    //Create object SentList & add to sentList arraylist
                    SentList tempSent = new SentList(tempWord, tempValue);
                    sentList.add(tempSent);
    
                    //if word values are pos add to posList
                    if (tempValue > 1.25)
                        posList.add(tempSent);
                    
                    //if word values are neg, add to neglist
                    if (tempValue < -1.25)
                        negList.add(tempSent);
                }
                catch (Exception e) {
                    System.out.printf("This line couldn't be processed: %s\n", line);
                    continue;
                }
            }
        } 
        catch (IOException e) {
            System.out.println("Error reading the file.");
            e.printStackTrace();
        }
    }

}