import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by Chelsea on 3/30/2016.
 */
public class Encoder {

    static char[] ALPHABET = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    public static void main(String args[]){
        String frequenciesFilename = args[0];
        int k = Integer.parseInt(args[1]);

        Scanner readFile;
        File frequenciesFile = new File(frequenciesFilename);
        try {
            readFile = new Scanner(frequenciesFile);
        }
        catch(FileNotFoundException e){
            System.out.println("Frequencies file not found!");
            return;
        }
        ArrayList<Integer> frequencyList = getFrequencies(readFile);
        int totalFrequency = 0;
        for(Integer i : frequencyList){
            totalFrequency += i;
        }


        float entropy = getEntropy(frequencyList, totalFrequency);
        System.out.println("Entropy computed from given frequencies: " + entropy);

        generateCharacters(k, frequencyList, totalFrequency);
        Huffman huffmaker = new Huffman(frequencyList);
        System.out.println("Now printing Huffman encodings: ");
        huffmaker.printTree();
        float calculatedEntropy = 1;
        try {
            calculatedEntropy = huffmaker.encode(new Scanner(new File("testText")), k);
        }
        catch(FileNotFoundException e){
            System.out.println("testText not found.");
        }
        System.out.println("Difference ratio between entropies: " + calculatedEntropy/entropy);
        System.out.println("Now decoding testText.");
        huffmaker.decode("testText.enc1");
    }

    public static ArrayList<Integer> getFrequencies(Scanner file){
        ArrayList<Integer> frequencies = new ArrayList();
        while(file.hasNextLine()){
            frequencies.add(Integer.parseInt(file.nextLine()));
        }
        return frequencies;
    }

    public static float getEntropy(ArrayList<Integer> frequencies, int totalFreq){
        float entropy = 0;
        for(int i : frequencies){
            //double check this
            entropy += Math.abs((float) i / (float) totalFreq * (Math.log((float) i / (float) totalFreq) / Math.log(2)));
        }
        return entropy;
    }

    public static File generateCharacters(int k, ArrayList<Integer> frequencies, int totalFreq){
        File outputFile = new File("testText");
        FileOutputStream out;
        try {
            out = new FileOutputStream(outputFile);
        }
        catch(FileNotFoundException e){
            System.out.println("Error creating testText.");
            return null;
        }

        Random random = new Random(new Date().getTime());
        for(int i = 0; i < k; i++){
            //generate random int
            int r = (Math.abs(random.nextInt()) % totalFreq) + 1;
            for(int j = 0; j < frequencies.size(); j++){
                r = r - frequencies.get(j);
                if(r <= 0){
                    //write char j
                    try {
                        out.write(ALPHABET[j]);
                    }
                    catch(IOException e){
                        System.out.println("Error writing to testText.");
                        return null;
                    }
                    break;

                }
                else
                    continue;
            }
        }

        return outputFile;
    }
}
