//Tyler Bauer, CSc 460, Assignment 1A, McCann, Lim, Nottingham, 9/5/18
//This program attempts to read in a file, correctly parse the information
//in it, sort it, and convert it into a binary I/O file. To solve this
//problem each line was parsed by separating each section by commas and
//adding spaces to strings that were too small. Then the line was added
//to an array list and sorted using merge sort. Then the arraylist was
//written to the output file.
//This program uses Java 1.8. The input file can be located anywhere as
//long as the program is given the location of the file
//There is a problem if a value is given where we expect it to be a 
//String. This is caused because I expect that a String will be passed
//with quotes around it. The time it takes to run the big file usually is about
//5-6 minutes.

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

public class prog1A {
	public static void main(String args[]) throws IOException{
	File             fileRef;             // used to create the file
        RandomAccessFile dataStream = null;   // specializes the file I/O

        //trys to read input from commandline
        System.out.println("Please insert file location: ");
        Scanner readConsole = new Scanner(System.in);
        String fileName = readConsole.nextLine();

        fileRef = new File(fileName); 
        
        //convert file to RAF
        try {
            dataStream = new RandomAccessFile(fileRef,"r");
        } catch (IOException e) {
            System.out.println("I/O ERROR: Something went wrong with the "
                             + "creation of the RandomAccessFile object.");
            System.exit(-1);
        }
        
        //goes to first part of the file
        try {
            dataStream.seek(0);
        } catch (IOException e) {
            System.out.println("I/O ERROR: Seems we can't reset the file "
                             + "pointer to the start of the file.");
            System.exit(-1);
        }
        
        //skips the first line of the file
        dataStream.readLine();
            
        String record;
        //creates the arraylist to store record strings
        ArrayList<String[]> sortedArray = new ArrayList<String[]>();
        //to keep track of the size
        int sortedListSize = 0;
        
        System.out.println("------Parsing data-----------");
        
        //parses the strings
        while((record = dataStream.readLine()) != null && record.contains(",")){   	
        	//parses the string info
        	String [] temp = parseString(record);
        		
        	//if the format given was wrong
        	if(temp != null){
        		//does the actual parsing of the line
        		sortedArray.add(parseString(record));
    
        		//keeps track of the sorted arrays size
        		sortedListSize++;
        	}
        }
        
        System.out.println("------Data parsed------------");
        
        System.out.println("------Sorting Data-----------");
        
        //merge Sort
        mergeSort(sortedArray, 0, sortedListSize - 1);
        
        System.out.println("------Data Sorted------------");
        
        System.out.println("------Writing to file--------");
    	
    	// Clean-up by closing the file 
        try {
            dataStream.close();
        } catch (IOException e) {
            System.out.println("VERY STRANGE I/O ERROR: Couldn't close "
                             + "the file!");
        }//end catch and try
        
        //converts the original file to a bin file
        String[] finalFileName = fileName.split("/");
        fileName = finalFileName[finalFileName.length-1];
        fileName = fileName.substring(0, fileName.length()-4);
    	
    	//create the output file
    	File outputFile = new File(fileName+".bin");
    	
    	//delete an old file then make a new one
    	outputFile.delete();
    	outputFile.createNewFile();
    	
    	RandomAccessFile outputDataStream = null;
    	
    	//convert new file to random access file
    	try {
            outputDataStream = new RandomAccessFile(outputFile,"rw");
        } catch (IOException e) {
            System.out.println("I/O ERROR: Something went wrong with the "
                             + "creation of the RandomAccessFile object.");
            System.exit(-1);
        }
        
        //goes to first part of the file
        try {
            outputDataStream.seek(0);
        } catch (IOException e) {
            System.out.println("I/O ERROR: Seems we can't reset the file "
                             + "pointer to the start of the file.");
            System.exit(-1);
        }
        
        //writes parsed reports to the binary file
        for(int i = 0; i< sortedListSize; i++){
        	writeToFile(sortedArray.get(i), outputDataStream);
        }
        
        //add the lengths
        addLengths(outputDataStream, sortedListSize);
        
        System.out.println("------Finished Writing-------");
        
        // Clean-up by closing the file 
        try {
            outputDataStream.close();
            readConsole.close();
        } catch (IOException e) {
            System.out.println("VERY STRANGE I/O ERROR: Couldn't close "
                             + "the file!");
        }//end catch and try
        
        System.out.println("End of Program.");  
    }//end main
	
	static String[] parseString(String record){
		//used to separate fields
    	String[] splitString = record.split(",");
    	
    	//if flight_NUM has a non-numeral it doesn't do anything with the data 
    	splitString[4] = splitString[4].substring(1, splitString[4].length()-1);
    	if(!splitString[4].contains("[a-zA-Z]+")){
    			
        	//checks length of the date
        	while(splitString[0].length() < 10){
        		splitString[0]+= " ";
        	}
        	
        	//check length of unique carrier
        	splitString[1] = splitString[1].substring(1, splitString[1].length()-1);
        	while(splitString[1].length() < 2){
        		splitString[1]+=" ";
        	}
        	
        	//checks the Airline ID
        	if(splitString[2].length() == 0){
        		splitString[2] = "-1";
        	}
        	
        	//check the tail ID
        	splitString[3] = splitString[3].substring(1, splitString[3].length()-1);
        	while(splitString[3].length() < 6){
        		splitString[3]+=" ";
        	}
        	
        	//check departure
        	splitString[5] = splitString[5].substring(1, splitString[5].length()-1);
        	while(splitString[5].length() < 3){
        		splitString[5]+=" ";
        	}
        	
        	//check dest
        	splitString[6] = splitString[6].substring(1, splitString[6].length()-1);
        	while(splitString[6].length() < 3){
        		splitString[6]+=" ";
        	}
        	
        	//check deptime
        	splitString[7] = splitString[7].substring(1, splitString[7].length()-1);
        	while(splitString[7].length() < 4){
        		splitString[7]+=" ";
        	}
        	
        	//check dep_delay
        	if(splitString[8].length() == 0){
        		splitString[8]+="-1";
        	}
        	
        	//check taxi_out
        	if(splitString[9].length() == 0){
        		splitString[9]+="-1";
        	}
        	
        	//check wheels_off
        	splitString[10] = splitString[10].substring(1, splitString[10].length()-1);
        	while(splitString[10].length() < 4){
        		splitString[10]+=" ";
        	}
        	
        	//check wheels_on
        	splitString[11] = splitString[11].substring(1, splitString[11].length()-1);
        	while(splitString[11].length() < 4){
        		splitString[11]+=" ";
        	}
        	
        	//check taxi_in
        	if(splitString[12].length() == 0){
        		splitString[12]+="-1";
        	}
        	
        	//check arr_time
        	splitString[13] = splitString[13].substring(1, splitString[13].length()-1);
        	while(splitString[13].length() < 4){
        		splitString[13]+=" ";
        	}
        	
        	//check arr_delay
        	if(splitString[14].length() == 0){
        		splitString[14]+="-1";
        	}
        	
        	//check cancelled
        	if(splitString[15].length() == 0){
        		splitString[15]+="-1";
        	}
        
        	//check cancellation code
        	splitString[16] = splitString[16].substring(1, splitString[16].length()-1);
        	while(splitString[16].length() < 1){
        		splitString[16]+=" ";
        	}
        	
        	//check air_time
        	if(splitString[17].length() == 0){
        		splitString[17]+="-1";
        	}
        	
        	//check distance
        	if(splitString[18].length() == 0){
        		splitString[18]+="-1";
        	}
        	
        	return splitString;
    	}
    	else{
    		//if the format given is wrong we return null
    		return null;
    	}
	}//end parseString
	//This method is used so that the strings given to us are parsed in the
	//format that is correct for the binary file

	static void writeToFile(String[] sortedArray, RandomAccessFile outputDataStream){
		try{
			//add date
	    	outputDataStream.writeBytes(sortedArray[0]);
	    	//add carrier
	    	outputDataStream.writeBytes(sortedArray[1]);
	    	//add airline ID
	    	outputDataStream.writeInt(Integer.parseInt(sortedArray[2]));
	    	//add Tail ID
	    	outputDataStream.writeBytes(sortedArray[3]);
	    	//add flight number
	    	outputDataStream.writeInt(Integer.parseInt(sortedArray[4]));
	    	//add departure
	    	outputDataStream.writeBytes(sortedArray[5]);
	    	//add destination 
	    	outputDataStream.writeBytes(sortedArray[6]);
	    	//add depart time
	    	outputDataStream.writeBytes(sortedArray[7]);
	    	//add depart delay
	    	outputDataStream.writeDouble(Double.parseDouble(sortedArray[8]));
	    	//add taxi out
	    	outputDataStream.writeDouble(Double.parseDouble(sortedArray[9]));
	    	//add wheels off
	    	outputDataStream.writeBytes(sortedArray[10]);
	    	//add wheels on
	    	outputDataStream.writeBytes(sortedArray[11]);
	    	//add taxi in
	    	outputDataStream.writeDouble(Double.parseDouble(sortedArray[12]));
	    	//add arrival time
	    	outputDataStream.writeBytes(sortedArray[13]);
	    	//add arrival delay
	    	outputDataStream.writeDouble(Double.parseDouble(sortedArray[14]));
	    	//add cancelled
	    	outputDataStream.writeDouble(Double.parseDouble(sortedArray[15]));
	    	//add cancellation code
	    	outputDataStream.writeBytes(sortedArray[16]);
	    	//add air time
	    	outputDataStream.writeDouble(Double.parseDouble(sortedArray[17]));
	    	//add distance
	    	outputDataStream.writeDouble(Double.parseDouble(sortedArray[18]));
		}
		catch(IOException e){
			System.out.println("Sorry, couldn't write to the file");
			System.exit(-1);
		}
	}//end writeToFile
	//what this method does is that it writes each subfield to a binary
	//file once the records have been parsed.
	
	static void addLengths(RandomAccessFile outputDataStream, int length){
		try{
			//add date
	        outputDataStream.writeInt(10);
	        //add carrier
	        outputDataStream.writeInt(2);
	        //add airline ID
	        outputDataStream.writeInt(4);
	        //add tail ID
	        outputDataStream.writeInt(6);
	        //add flight number
	        outputDataStream.writeInt(4);
	        //add departure
	        outputDataStream.writeInt(3);
	        //add destination 
	        outputDataStream.writeInt(3);
	        //add depart time
	        outputDataStream.writeInt(4);
	        //add depart delay
	        outputDataStream.writeInt(8);
	        //add taxi out
	        outputDataStream.writeInt(8);
	        //add wheels off
	        outputDataStream.writeInt(4);
	        //add wheels on
	        outputDataStream.writeInt(4);
	        //add taxi in
	        outputDataStream.writeInt(8);
	        //add arrival time
	    	outputDataStream.writeInt(4);
	    	//add arrival delay
	    	outputDataStream.writeInt(8);
	    	//add cancelled
	    	outputDataStream.writeInt(8);
	    	//add cancellation code
	    	outputDataStream.writeInt(1);
	    	//add air time
	    	outputDataStream.writeInt(8);
	    	//add distance
	    	outputDataStream.writeInt(8);
	    	
	    	//add recordSize
	    	outputDataStream.writeInt(length);
		}
		catch(IOException e){
			System.out.println("Sorry, couldn't write to the file");
			System.exit(-1);
		}

	}//end addLengths
	//This method adds the length of each record subsection to the bottom
	//of the binary file

	static void mergeSort(ArrayList<String[]> sortedArray, int left, int right){
		//check if the left index is lower than the right one
		if(left < right){
			//sets the middle
			int middle = (left + right)/2;
			//calls the left side of the split
			mergeSort(sortedArray, left, middle);
			//calls the right side of the split
			mergeSort(sortedArray, middle + 1, right);
			//does the actual sorting
			merge(sortedArray, left, middle, right);
		}
	}//end mergeSort
	//This method serves as a type of hub for recursive calls for the merge
	//sort
	
	static void merge(ArrayList<String[]> sortedArray, int left, int middle, int right){
		//find sizes of the subarrays to be merged
		int n1 = middle - left + 1;
		int n2 = right - middle;
		
		//create temp arrays
		ArrayList<String[]> tempLeft = new ArrayList<String[]>(); 
		ArrayList<String[]> tempRight = new ArrayList<String[]>(); 
		
		//copy data to temp arrays
		for(int i=0; i < n1; i++){
			tempLeft.add(sortedArray.get(left + i));
		}
		for(int j=0; j<n2; j++){
			tempRight.add(sortedArray.get(middle + 1 + j));
		}
		
		//indexes for arrays
		int i = 0, j = 0;
		
		//first index of merges subarrays
		int k = left;	
		while (i < n1 && j < n2){
			if(Integer.parseInt(tempLeft.get(i)[4]) <= Integer.parseInt(tempRight.get(j)[4])){
				sortedArray.set(k, tempLeft.get(i));
				i++;
			}
			else{
				sortedArray.set(k, tempRight.get(j));
				j++;
			}
			k++;
		}
		
		//copy remaining elements of the left array
		while(i < n1){
			sortedArray.set(k, tempLeft.get(i));
			i++;
			k++;
		}
		
		//copy remaining elements of the right array
		while(j < n2){
			sortedArray.set(k, tempRight.get(j));
			j++;
			k++;
		}
	}//end merge
	//This method does the actual sorting for the merge sort
}//end class prog1A
