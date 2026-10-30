//This class includes both the original, postive & negative review words
//This does not need to be done this way - it is just a suggestion

import java.util.ArrayList;
import java.util.Random;

public class Words {
    String origWord;
    String editWord;
    String posWord;
    String negWord;
    double sentOrigValue;
    double sentPosValue;
    double sentNegValue;

    ArrayList<SentList> sentList;
    ArrayList<SentList> posList;
    ArrayList<SentList> negList;

    boolean posFlag = false;
    boolean negFlag = false;

    //PRE: Take original word (no puctuation or caps), sentList, posList, and negList.
    //POST: Set origWord to the o arg. Check if orig word is pos or neg.
    //      If pos, set posWord to origWord, else negWord to origword.
    //      Get and set editWord. Set negWord to editWord if origWord is pos else vice versa.
    //      Set sent values using sentList.
    //      If origWord is neither pos or neg. origWord, editWord, poswWord, and negWord are all equall;
    //      flags are all false; and values are all equall
    public Words(String o, ArrayList<SentList> sentList, ArrayList<SentList> posList, ArrayList<SentList> negList){
        origWord = o;
        this.sentList = sentList;
        this.posList = posList;
        this.negList = negList;
        editWord = determineEditedWord();
        SentList origSentObj = getSentListObj(origWord);

        if (origSentObj.getValue() > 1) {
            posWord = origWord;
            negWord = editWord; 
            posFlag = true;
        } else if (origSentObj.getValue() < -1) {
            posWord = editWord;
            negWord = origWord;
            negFlag = true;
        } else {
            posWord = origWord;
            negWord = origWord; 
        }

        SentList posSentObj = getSentListObj(posWord);
        SentList negSentObj = getSentListObj(negWord);
        sentOrigValue = origSentObj.getValue();
        sentPosValue = posSentObj.getValue();
        sentNegValue = negSentObj.getValue();
    }

    public boolean getPosFlag(){
        return posFlag;
    }
    public boolean getNegFlag(){
        return negFlag;
    }
    public String getOrigWord() {return origWord;}
    public String getEditWord() {return editWord;}
    public String getPosWord() {return posWord;}
    public String getNegWord() {return negWord;}
    public double getOrigValue() {return sentOrigValue;}
    public double getPosValue() {return sentPosValue;}
    public double getNegValue() {return sentNegValue;}

    //PRE: 
    //POST: Get Sentlist object matching origWord from sentList
    //      Determine if the word is positive or negative.
    //      If positive, return a random negative word from negList,
    //      else, vice versa.
    private String determineEditedWord() {
        SentList sentObj = getSentListObj(origWord);
        Random rand = new Random();
        String editWord = null;
        if (sentObj.getValue() > 1) {
            int randIdx = rand.nextInt(negList.size());
            SentList negSentObj = negList.get(randIdx);
            editWord = negSentObj.getWord();
        } else if (sentObj.getValue() < -1) {
            int randIdx = rand.nextInt(posList.size());
            SentList posSentObj = posList.get(randIdx);
            editWord = posSentObj.getWord();
        } else {
            return origWord;
        }
        
        return editWord;
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

}
