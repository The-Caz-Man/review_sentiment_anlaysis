import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Review {
    String fileName;
    ArrayList<SentList> sentList;
    ArrayList<SentList> posList;
    ArrayList<SentList> negList;
    ArrayList<Words> wordList;
    ArrayList<String> lines;
    ArrayList<String> posLines;
    ArrayList<String> negLines;
    double sentValue;
    double posSentValue;
    double negSentValue;
    String formattedReview;
    String posFormatted;
    String negFormatted;
    boolean mostPositive = false;
    boolean mostNegative = false;
    String toPosData;
    String toNegData;
    boolean badFile = false;

    Review(String fileName, ArrayList<SentList> sentList, ArrayList<SentList> posList, ArrayList<SentList> negList) {
        this.fileName = fileName;
        this.sentList = sentList;
        this.posList = posList;
        this.negList = negList;
        this.lines = makeReviewLines();
        this.wordList = makeWordsList();
        this.posLines = makePosLines();
        this.negLines = makeNegLines();
        this.formattedReview = makeFormattedReview(lines);
        this.posFormatted = makeFormattedReview(posLines);
        this.negFormatted = makeFormattedReview(negLines);
        if (formattedReview.equals(posFormatted))
            mostPositive = true;
        if (formattedReview.equals(negFormatted))
            mostNegative = true;
        this.sentValue = makeSentValue(lines);
        this.posSentValue = makeSentValue(posLines);
        this.negSentValue = makeSentValue(negLines);
        this.toPosData = makeFormattedToPosData();
        this.toNegData = makeFormattedToNegData();
    }



    //PRE:
    //POST: Return SentList object matching String w
    private SentList getSentListObj(String w) {
        SentList sentObj = null;
        for (SentList e: sentList) {
            if (w.equals(e.getWord())) {
                sentObj = e;
                break;
            }
        }
        return sentObj;
    }

    //PRE:
    //POST: Loop through every line in lines, then through every word in lines, 
    //      adding relevant words to wordsList
    private ArrayList<Words> makeWordsList() {
        ArrayList<Words> toReturnList = new ArrayList<Words>();
        for (String line: lines) {
            //List of words separated by space.
            String[] tempWords = line.split("\\s+");
            for (String w: tempWords) {
                String noCapWord = w.toLowerCase();
                //Remove non letter chars from lowercased word.
                String onlyLettersWord = noCapWord.replaceAll("[^a-zA-Z]", "");
                if (wordInSentList(onlyLettersWord)) {
                    Words tempWord = new Words(onlyLettersWord, sentList, posList, negList);
                    toReturnList.add(tempWord);
                }
            }
        }
        return toReturnList;
    }

    
    //PRE: Take a word as w
    //POSRT: return true if w is amoung words in sentList, else return false.
    private boolean wordInSentList(String w) {

        for (SentList sentObj: sentList) {
            if (w.equals(sentObj.getWord())) {
                return true;
            }
        }
        return false;
    }

    //PRE:
    //POST: Try to open the file, add each line to tempLines list, then return list
    private ArrayList<String> makeReviewLines() {
        String line;
        ArrayList<String> tempLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            while ((line = br.readLine()) != null) {
                tempLines.add(line);
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Error reading the file: " + fileName);
            badFile = true;
        }
        return tempLines;
    }

    //PRE:
    //POST: Replace negFlagged words in Lines with pos words, return these lines
    private ArrayList<String> makePosLines() {
        ArrayList<String> tempLines = new ArrayList<>();
        for (String line: lines) {
            //tempLine is the same if no words are replaced.
            String tempLine = posLineRecusive(line, 0);
            tempLines.add(tempLine);
        }
        return tempLines;
    }

    //PRE: Take the string being adjusted and the current index of wordsList
    //POST: If i is beyond the final index return string, else replace current negative word with pos and
    //      and pass new string and incremented idx
    private String posLineRecusive(String l, int i) {
        if (i == wordList.size()) {
            return l;
        } else {
            //tempLine is the same if no words are replaced.
            String tempLine = l;
            Words tempWordsObj = wordList.get(i);
            if (tempWordsObj.getNegFlag()) {
                //Replace ignoring case
                tempLine = l.replaceAll(("(?i)" + tempWordsObj.getOrigWord()), tempWordsObj.getPosWord());
            }

            return posLineRecusive(tempLine, (i+1));
        }
    }

    //PRE:
    //POST: Replace posFlagged words in Lines with neg words, return these lines
    private ArrayList<String> makeNegLines() {
        ArrayList<String> tempLines = new ArrayList<>();
        for (String line: lines) {
            //tempLine is the same if no words are replaced.
            String tempLine = negLineRecusive(line, 0);
            tempLines.add(tempLine);
        }
        return tempLines;
    }

    //PRE: Take the string being adjusted and the current index of wordsList
    //POST: If i is beyond the final index return string, else replace current pos word with neg and
    //      and pass new string and incremented idx
    private String negLineRecusive(String l, int i) {
        if (i == wordList.size()) {
            return l;
        } else {
            //tempLine is the same if no words are replaced.
            String tempLine = l;
            Words tempWordsObj = wordList.get(i);
            if (tempWordsObj.getPosFlag()) {
                //Replace ignoring case
                tempLine = l.replaceAll(("(?i)" + tempWordsObj.getOrigWord()), tempWordsObj.getNegWord());
            }

            return negLineRecusive(tempLine, (i+1));
        }
    }

    //PRE: takes a String Array list that contains lines
    //POST: loop each line in lines. If line is longer than 80, split line at last space < 80.
    //      Combine all these lines joining them with "\n"
    //      retrun formatted string
    private String makeFormattedReview(ArrayList<String> lns) {
        ArrayList<String> tempLns = new ArrayList<>();
        for (String ln: lns) {
            splittAround80Recursive(ln, tempLns);
        }
        String out = String.join("\n", tempLns);
        return out;
    }

    //PRE: Takes a line string and a Array list lines are being added to
    //PRE: if line length <= 80 add ln to list, else split at space closest to 80, add first half to list,
    //      then pass second half to function.
    private void splittAround80Recursive (String l, ArrayList<String> lns) {
        if (l.length() <= 80) {
                lns.add(l);
            } else {
                //Get indx of space closest to the 80th char
                int splitIdx = l.lastIndexOf(" ", 79);
                String firstHalf = l.substring(0, splitIdx);
                lns.add(firstHalf);
                String secondHalf = l.substring((splitIdx + 1));
                splittAround80Recursive(secondHalf, lns);
            }

    }
    //PRE: Takes Array list as lns
    //POST: calculate sent value by adding sentValues of every word in line
    private double makeSentValue(ArrayList<String> lns) {
        double out = 0;
        for (String ln:lns) {
            String [] tempWords = ln.split("\\s+"); 
            for (String w: tempWords) {
                String noCapWord = w.toLowerCase();
                //Remove non letter chars from lowercased word.
                String onlyLettersWord = noCapWord.replaceAll("[^a-zA-Z]", "");
                if (wordInSentList(onlyLettersWord)) {
                    SentList tempObj = getSentListObj(onlyLettersWord);
                    out += tempObj.getValue();
                }
            }
        }
        return out;
    }

    //PRE:
    //POST: Return a formatted table as a String detailing changes to make review more positive.
    private String makeFormattedToPosData() {
        ArrayList<String> tempOrigWords = new ArrayList<>();
        ArrayList<String> tempEditWords = new ArrayList<>();
        ArrayList<Double> tempOrigValues = new ArrayList<>();
        ArrayList<Double> tempEditValues = new ArrayList<>();
        for (Words obj:wordList) {
            if (obj.getNegFlag()) {
                tempOrigWords.add(obj.getOrigWord());
                tempEditWords.add(obj.getPosWord());
                tempOrigValues.add(obj.getOrigValue());
                tempEditValues.add(obj.getPosValue());
            }    
        }
        ArrayList<String> lns = new ArrayList<>();
        for (int i = 0; i < tempOrigWords.size(); i++) {
            String origFormatted = String.format("%15s", tempOrigWords.get(i));
            String origValueFormatted = String.format("%10.2f", tempOrigValues.get(i));
            String editFormatted = String.format("%15s", tempEditWords.get(i));
            String editValueFormatted = String.format("%10.2f", tempEditValues.get(i));
            String tempLn = origFormatted + origValueFormatted + editFormatted + editValueFormatted;
            lns.add(tempLn);
        }
        String totalFormatted = String.format("%-15s", "Total:");
        String totalValueFormatted = String.format("%10.2f", sumDoubleList(tempOrigValues));
        String blank = "               ";
        String totalEditFormatted = String.format("%10.2f", sumDoubleList(tempEditValues));
        String tmpLn = totalFormatted + totalValueFormatted + blank + totalEditFormatted;
        lns.add(tmpLn);
        String out = String.join("\n", lns);
        return out;
    }

    //PRE:
    //POST: Return a formatted table as a String detailing changes to make review more negative.
    private String makeFormattedToNegData() {
        ArrayList<String> tempOrigWords = new ArrayList<>();
        ArrayList<String> tempEditWords = new ArrayList<>();
        ArrayList<Double> tempOrigValues = new ArrayList<>();
        ArrayList<Double> tempEditValues = new ArrayList<>();
        for (Words obj:wordList) {
            if (obj.getPosFlag()) {
                tempOrigWords.add(obj.getOrigWord());
                tempEditWords.add(obj.getNegWord());
                tempOrigValues.add(obj.getOrigValue());
                tempEditValues.add(obj.getNegValue());
            }    
        }
        ArrayList<String> lns = new ArrayList<>();
        for (int i = 0; i < tempOrigWords.size(); i++) {
            String origFormatted = String.format("%15s", tempOrigWords.get(i));
            String origValueFormatted = String.format("%10.2f", tempOrigValues.get(i));
            String editFormatted = String.format("%15s", tempEditWords.get(i));
            String editValueFormatted = String.format("%10.2f", tempEditValues.get(i));
            String tempLn = origFormatted + origValueFormatted + editFormatted + editValueFormatted;
            lns.add(tempLn);
        }
        String totalFormatted = String.format("%-15s", "Total:");
        String totalValueFormatted = String.format("%10.2f", sumDoubleList(tempOrigValues));
        String blank = "               ";
        String totalEditFormatted = String.format("%10.2f", sumDoubleList(tempEditValues));
        String tmpLn = totalFormatted + totalValueFormatted + blank + totalEditFormatted;
        lns.add(tmpLn);
        String out = String.join("\n", lns);
        return out;
    }

    //PRE: Takes a list of doubles
    //POST: return sum of doubles
    private double sumDoubleList(ArrayList<Double> l) {
        double out = 0;
        for (Double v:l) {
            out += v;
        }
        return out;
    }


    public String getFileName() {return fileName;}
    public ArrayList<String> getLines() {return lines;}
    public ArrayList<Words> getWordsList() {return wordList;}
    public String getFormattedReview() {return formattedReview;}
    public String getPosFormatted() {return posFormatted;}
    public String getNegFormatted() {return negFormatted;}
    public boolean getMostPos() {return mostPositive;}
    public boolean getMostNeg() {return mostNegative;}
    public double getSentValue() {return sentValue;}
    public double getPosSentValue() {return posSentValue;}
    public double getNegSentValue() {return negSentValue;}
    public String getToPosData() {return toPosData;}
    public String getToNegData() {return toNegData;}
    public boolean getBadFile() {return badFile;}
}
