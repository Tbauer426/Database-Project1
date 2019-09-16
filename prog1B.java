//Tyler Bauer, CSc 460, Assignment 1B, McCann, Lim, Nottingham, 9/5/18
//This program attempts to read in a bin file and search the file for
//flight numbers using inputs given by the user. This is done by reading
//in the .bin file and finding how many lines there are supposed to be
//in it by going to the end of the file and reading the int at the end.
//It then is able to find out where everything is by jumping from the file
//size is multiplied by the how big the line is supposed to be (105 bytes).
//Users able able to search for a flight number and the value is returned
//using an interpolation search. Once it finds the flight it checks if
//there are any numbers behind it that are the same number. Once it does 
//that it prints the starting number and loops through all points that
//share the same flight number. It ends when the user enters 0 as a number.
//This program uses Java 1.8. The input file can be located anywhere as
//long as the program is given the location of the file
//There are no problems that I could find with the search function

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class prog1B {
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
        //get list size from the file
        //this loops through the file until it reaches the end
        //while(dataStream.readLine() != null){
        //	dataStream.readLine();
        //}
        //gets the pointer for the end of the file
        int pointer = (int) dataStream.length();
        //subtract the value of the pointer by the size of an int so it points
        //back to an int
        pointer = pointer - 4;
        //jumps to the location of the last int
        dataStream.seek(pointer);
        //gets the size of the file which should be stored here
        int fileSize = dataStream.readInt();
        
        //Prints the start middle and end records
        System.out.println("FIRST FIVE RECORDS:");
        System.out.println(getRecords(0, dataStream));
        System.out.println(getRecords(1, dataStream));
        System.out.println(getRecords(2, dataStream));
        System.out.println(getRecords(3, dataStream));
        System.out.println(getRecords(4, dataStream));
        System.out.println("-----------------------------------");
        
        //check if we should print three or four of the middle records
        if(fileSize % 2 != 0){
	        System.out.println("MIDDLE FOUR RECORDS:");
	        System.out.println(getRecords((fileSize / 2) - 2, dataStream));
	        System.out.println(getRecords((fileSize / 2) - 1, dataStream));
	        System.out.println(getRecords((fileSize / 2), dataStream));
	        System.out.println(getRecords((fileSize / 2) + 1, dataStream));
        }
        else{
        	System.out.println("MIDDLE THREE RECORDS:");
	        System.out.println(getRecords((fileSize / 2) - 1, dataStream));
	        System.out.println(getRecords((fileSize / 2), dataStream));
	        System.out.println(getRecords((fileSize / 2) + 1, dataStream));
        }
        System.out.println("-----------------------------------");
        
        System.out.println("LAST FIVE RECORDS:");
        System.out.println(getRecords(fileSize-5, dataStream));
        System.out.println(getRecords(fileSize-4, dataStream));
        System.out.println(getRecords(fileSize-3, dataStream));
        System.out.println(getRecords(fileSize-2, dataStream));
        System.out.println(getRecords(fileSize-1, dataStream));
        System.out.println("-----------------------------------");
        
        System.out.println("There are "+fileSize+" record(s) in the file.");
        System.out.println("Enter a flight number (FL_NUM) that you would like to search i.e. 2817. Enter zero(0) to end your search.");
        
        //have scanner look for inputs
        String scannerLine;
        while(!(scannerLine = readConsole.nextLine()).equals("0")){	
        	//check if they gave an int
        	try{
        		//does interpolation search
	        	int index = interpolationSearch(0, fileSize-1, Integer.parseInt(scannerLine), dataStream);
	        	
	        	//check if it's in the records
	        	if(index != -1){
	        		//go back on the list to look for records also with the same fl_num
	        		while(index - 1 > -1 && getFlightNum(index-1, dataStream) == Integer.parseInt(scannerLine)){
	        			index--;
	        		}
	        		//print all of the fl_num records
	        		while(index != fileSize && getFlightNum(index, dataStream) == Integer.parseInt(scannerLine)){
		        		System.out.println(getRecords(index, dataStream));
		        		index++;
	        		}
	        	}
	        	else{
	        		System.out.println("No result(s) found");
	        	}
	        }//end try
        	catch(NumberFormatException e){
        		System.out.println("Please input a number.");
        	}//end catch
        	System.out.println("Enter a flight number (FL_NUM) that you would like to search i.e. 2817. Enter zero(0) to end your search.");
        }//end while  
        System.out.println("End of search.");
        
        // Clean-up by closing the file 
        try {
        	dataStream.close();
            readConsole.close();
        } catch (IOException e) {
            System.out.println("VERY STRANGE I/O ERROR: Couldn't close "
                             + "the file!");
        }//end catch and try
        
        System.out.println("End of Program.");  
    }//end main
	
	static int interpolationSearch(int left, int right, int target, RandomAccessFile dataStream){
		double lowIndex = left;
		double highIndex = right;	
		double lowKey = getFlightNum((int)lowIndex, dataStream);
		double highKey = getFlightNum((int)highIndex, dataStream);
		
		if(right >= left){
		
			//this is the formula for interpolation search
			double temp = lowIndex + ( ( (target - lowKey) / (highKey - lowKey) ) * (highIndex - lowIndex));
			
			//returns the index if it is the correct one
			if(getFlightNum((int)temp, dataStream) == target){
				return (int)temp;
			}
			
			//if the index is greater than the target we look at the top half
			if(getFlightNum((int)temp, dataStream) > target){
				return interpolationSearch(left, ((int)temp)-1, target, dataStream);
			}
			
			//if the index is lesser than the target we look at the bottom half
			return interpolationSearch(((int)temp)+1, right, target, dataStream);
		}
		//if it can't find the index it'll just exit
		return -1;
	}//end interpolation search
	//What this method does is an interpolation sort. It calculates where
	//a number may be and jumps to that point. It then looks to see if
	//the target value is above it's location or below it and recalculates
	//where to jump to until it either finds it's index or doesn't.
	
	static int getFlightNum(int index, RandomAccessFile inputFile){
		int finalInt = -1;
		
		//we have 105 bytes per line, so we get the index by multiplying
		//the given index by 105
		index = index * 105;
		
		try {
			//Tests to see if we can jump to this position in the file
			//We add 22 to this because flight num is 22 bytes away from
			//the start of the line
			inputFile.seek(index + 22);
			
			//calls read int method
			finalInt = readInt(4, inputFile);
		} catch (IOException e) {}	
		
		return finalInt;
	}//end getFlightNum
	//What this method does is it looks for the flight number of a given
	//record index. It returns an int to help with the logic of the program
	
	static String getRecords(int index, RandomAccessFile inputFile){
		//prints out [0]:  or some other index
		String finalString = "["+index+"]: ";
		
		//we have 105 bytes per line, so we get the index by multiplying
		//the given index by 105
		index = index * 105;
		
		//gets airline
		try {
			//tests to see if we can jump to this position in the file
			inputFile.seek(index + 10);
		} catch (IOException e) {
			System.out.println("I/O ERROR: Seems we can't reset the file "
                    + "pointer to the start of the file.");
			System.exit(-1);
		}
		//adds string to output string
		finalString += readString(2, inputFile) + ", ";
		
		//gets fl_num
		try {
			//tests to see if we can jump to this position in the file
			inputFile.seek(index + 22);
		} catch (IOException e) {
			System.out.println("I/O ERROR: Seems we can't reset the file "
                    + "pointer to the start of the file.");
			System.exit(-1);
		}
		//adds int to output string
		finalString += readInt(4, inputFile) + ", ";
		
		//get departure
		try {
			//tests to see if we can jump to this position in the file
			inputFile.seek(index + 26);
		} catch (IOException e) {
			System.out.println("I/O ERROR: Seems we can't reset the file "
                    + "pointer to the start of the file.");
			System.exit(-1);
		}
		//adds string to output string
		finalString += readString(3, inputFile) + ", ";
		
		//get arrival
		try {
			//tests to see if we can jump to this position in the file
			inputFile.seek(index + 29);
		} catch (IOException e) {
			System.out.println("I/O ERROR: Seems we can't reset the file "
		               + "pointer to the start of the file.");
			System.exit(-1);
		}
		//adds string to output string
		finalString += readString(3, inputFile);

		return finalString;
	}//end getRecords
	//what this method does is it reads a the airline string, fl_num int, 
	//departure string and arrival string and converts it into the string
	//that is required to be printed by the program
	
	static int readInt(int length, RandomAccessFile inputFile){
		int finalInt = -1;
		//sees if we can read from the file
		try {
			finalInt = inputFile.readInt();
		} catch (IOException e) {}
		return finalInt;
	}//end readInt
	//what this method does it reads an int from the file
	
	static String readString(int length, RandomAccessFile inputFile){
		String finalString = "";
		//the for loops gets a byte, converts it to a char, then adds it
		//a string. It does it for the length given.
		for(int i=0; i < length; i++){
			//checks if we can read from the file
			try {
				finalString += (char)inputFile.read();
			} catch (IOException e) {}
		}	
		return finalString;
	}//end readString
	//What this method does is reads a string of a given length from the
	//binary file.
}//end class prog1B
//This class keeps the program and method all in one file. Since we submit
//only one file it makes Object Oriented Design a bit harder.
