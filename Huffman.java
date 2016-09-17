import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by Chelsea on 4/2/2016.
 */
public class Huffman {

    char[] ALPHABET = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    HuffmanTree tree;
    HashMap<Character, String> encodingMap = new HashMap();
    public Huffman(ArrayList<Integer> frequencies){
        tree = constructTree(constructNodes(frequencies));

    }

    public void printTree(){
        tree.printTree();
    }

    public float encode(Scanner input, int k){
        return tree.encode(input, k);
    }

    public void decode(String filename){
        try {
            tree.decode(new Scanner(new File(filename)));
        }
        catch(FileNotFoundException e){
            System.out.println("Error reading testText.enc1");
        }
    }

    private ArrayList<HuffmanNode> constructNodes(ArrayList<Integer> frequencies){
        int index = 0;
        ArrayList<HuffmanNode> nodes = new ArrayList();
        for(Integer i : frequencies){
            nodes.add(new HuffmanNode(ALPHABET[index], i));
            index++;
        }
        sortNodes(nodes);
        return nodes;
    }

    private void sortNodes(ArrayList<HuffmanNode> nodes){
        for(int i = 1; i < nodes.size(); i++){
            if (nodes.get(i - 1).getFrequency() > nodes.get(i).getFrequency()){
                //find correct spot for nodes[i]
                for(int j = 0; j < i; j++){
                    if(nodes.get(j).getValue() > nodes.get(i).getValue()){
                        HuffmanNode temp = nodes.get(i);
                        nodes.remove(i);
                        nodes.add(j, temp);
                        break;
                    }
                    else
                        continue;
                }
            }
            else
                continue;
        }

    }
    private HuffmanTree constructTree(ArrayList<HuffmanNode> nodes){
        while(nodes.size() != 1){
            //combine 2 lowest indexed nodes and then re-add
            HuffmanNode left = nodes.get(0);
            HuffmanNode right = nodes.get(1);
            nodes.add(new HuffmanNode('*', left.getFrequency() + right.getFrequency(), left, right));
            nodes.remove(left);
            nodes.remove(right);
            sortNodes(nodes);
        }

        return new HuffmanTree(nodes.get(0));

    }

    private class HuffmanNode {
        private char value;
        private int frequency;
        private HuffmanNode left;
        private HuffmanNode right;

        public HuffmanNode(char val, int freq){
            value = val;
            frequency = freq;
            left = null;
            right = null;
        }

        public HuffmanNode(char val, int freq, HuffmanNode l, HuffmanNode r){
            value = val;
            frequency = freq;
            left = l;
            right = r;
        }

        public int getFrequency(){
            return frequency;
        }

        public int getValue(){
            return value;
        }

        public boolean isLeaf(){
            return left == null && right == null;
        }

        public boolean hasLeft(){
            return left != null;
        }

        public boolean hasRight(){
            return right != null;
        }

        public HuffmanNode getLeft(){
            return left;
        }

        public HuffmanNode getRight(){
            return right;
        }

    }

    private class HuffmanTree{
        private HuffmanNode root;

        public HuffmanTree(HuffmanNode root){
            this.root = root;
        }

        public void decode(Scanner inputFile){
            inputFile.useDelimiter("");
            HuffmanNode current = root;
            File outputFile = new File("testText.dec1");
            FileOutputStream out;
            try {
                out = new FileOutputStream(outputFile);
            }
            catch(FileNotFoundException e){
                System.out.println("Problem creating testText.dec1");
                return;
            }
            while(inputFile.hasNext()){
                int i = Integer.parseInt(inputFile.next());
                if(i == 1){
                    current = current.getRight();
                }
                if(i == 0){
                    current = current.getLeft();
                }
                if(current.isLeaf()){
                    try {
                        out.write((char) current.getValue());
                    }
                    catch(IOException e){
                        System.out.println("Error writing to testText.dec1");
                    }
                    current = root;
                }


            }

        }

        public float encode(Scanner inputFile, int k){
            inputFile.useDelimiter("");
            FileOutputStream out;
            try{
                out = new FileOutputStream(new File("testText.enc1"));
            }
            catch(FileNotFoundException e){
                System.out.println("Error creating testText.enc1");
                return -1;
            }
            int totalBits = 0;
            while(inputFile.hasNext()){
                char c = inputFile.next().charAt(0);
                String encoding = encodingMap.get(c);
                totalBits += encoding.length();
                for(int i = 0; i < encoding.length(); i++) {
                    try {
                        out.write(encoding.charAt(i));
                    } catch (IOException e) {

                    }
                }

            }

            System.out.println("Actual entropy from encoding: " + (float)totalBits/(float)k);
            return (float)totalBits/(float)k;


        }

        public void printTree(){
            printNode(root, "");
        }

        private void printNode(HuffmanNode node, String encoding){
            if(node.isLeaf()){
                encodingMap.put((char)node.getValue(), encoding);
                System.out.println("Character: " + (char) node.getValue() + ", Encoding: " + encoding);
            }
            else{
                if(node.hasLeft())
                    printNode(node.getLeft(), encoding.concat("0"));
                if(node.hasRight())
                    printNode(node.getRight(), encoding.concat("1"));
            }

        }
    }
}
