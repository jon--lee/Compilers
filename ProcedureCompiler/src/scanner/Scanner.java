package scanner;

import java.io.*;

/**
 * ScannerLabAdvanced
 * Scanner is a simple scanner for Compilers and Interpreters (2014-2015) lab exercise 1
 * @author Jonathan Lee
 *  	
 *  THIS ADVANCED SCANNER DOES NOT ACCOUNT FOR COMMENTED CODE
 *  
 * <Insert a comment that shows how to use this object>
 * This object is used by making a scanner object from the class. one can then call next token to
 * retrieve tokens from the given source code. if any tokens are invalid, it will return a scan error exception
 * tokens are returned in string form
 * this object also ignores line and block comments
 * 
 * LIST OF ALL REGULAR EXPRESSIONS USED (WILL BE UPDATED WITH EACH ITERATION):
 * 	- letters := [a-z A-Z]
 *  - digits := [1-9]
 *  - operators := [=, +, -, *, /, %, (, ), {, }, <, >, ., &&, ||, >=, <=, ;, !, ==, !=, :=, -=, +=, //, /*, \*\/]
 * 
 * PLEASE NOTE: THIS SCANNER CLASS WILL RECOGNIZE MULTIPLE CHARACTER OPERANDS WHICH WAS SPECIFIED IN THE LAB
 * DOCUMENT. THEREFORE, THIS CLASS TAKES A DIFFERENT, MORE COMPLICATED APPROACH TO RETURNING OPERAND TOKENS 
 * THAT INVOLVES STRINGS NOT CHARACTERS. SEE THE scanOperand METHOD DOCUMENTATION FOR MORE INFORMATION REGARDING
 * THIS
 * A simpler version of the scanner was also made that only returns operands as single characters in the ScannerLab Project
 * 
 * 
 * @version Aug 29 2014 - file created and all basic methods implemented
 * @version Sep 2 2014 - added tokens to operator RegEx, updated documentation
 */
public class Scanner
{
	
	
    private BufferedReader in;
    private char currentChar;
    private boolean eof;
    /**
     * Scanner constructor for construction of a scanner that 
     * uses an InputStream object for input.  
     * Usage:
     * FileInputStream inStream = new FileInputStream(new File(<file name>);
     * Scanner lex = new Scanner(inStream);
     * @postcondition: scanner object created, currentChar defined
     * @param inStream the input stream to scan
     */
    public Scanner(InputStream inStream)
    {
        setInput(inStream);
        eof = false;
        getNextChar();
    }
    /**
     * Method: Scanner
     * Usage: new Scanner(string)
     * Scanner constructor for constructing a scanner that 
     * scans a given input string.  It sets the end-of-file flag an then reads
     * the first character of the input string into the instance field currentChar.
     * Usage: Scanner lex = new Scanner(input_string);
     * @postcondition: scanner object created, currentChar defined
     * @param inString the string to scan
     */
    public Scanner(String inString)
    {
        setInput(inString);
        eof = false;
        getNextChar();
    }
    
    
    /**
     * method: setInput
     * usage: program.setInput(stream)
     * public helper method used to set the input
     * that the scanner reads to what is passed to the scanner
     * if the input is from a file, it will be an input stream
     * if it is from a string, it will be read as a string
     * input is set to the instance variable for use in the scanner
     * @param inStream	input stream from a file
     * postcondition: in set to new input stream
     */
    public void setInput(InputStream inStream)
    {
    	in = new BufferedReader(new InputStreamReader(inStream));
    }
    
    /**
     * method: setInput
     * usage: program.setInput(string)
     * see setInput method description
     * @param inString inString  general input stream
     * postcondition: in set to the new input stream of the string
     */
    public void setInput(String inString)
    {
    	in = new BufferedReader(new StringReader(inString));
    }
    
    /**
     * Method: getNextChar
     * calls the read method from in (the instance variable that is a buffered reader)
     * in returns an int (if this int is -1, the reader is at the end of the file so eof can be set to true)
     * else, currentChar is set to the character value of the give read output
     * Method does not take any parameters or return an output
     * @precondition: has a next character
     * @postcondition: sets the currentchar to the next char in the source
     * @throws IOException in the event that the reader cannot obtain the next character for some reason
     */
    private void getNextChar()
    {
    	int readChar = -1;		//base case, to avoid being marked as an error
    	
    	//try-catch-finally block intended to catch any error
    	//that would occur from in.read()
		try
		{
			readChar = in.read();
		}
		catch (IOException e)
		{
			System.exit(0);
		}
		finally
		{
			//do nothing (program should crash if there is an io error)
		}
		
		
    	if(readChar < 0)
    		eof = true;
    	currentChar = (char)readChar;
    }
    
    
    /**
     * Method: eat
     * moves the scanner along the file by calling getNextChar
     * however, before doing so, it will check to make sure its given char "expected"
     * matches the currentChar before moving on
     * @precondition: expected = currentChar
     * @postcondition: gets the next character in the source
     * @param expected - the expected character that the scanner should receive to match the current character (gives the green light to move on)
     * @throws ScanErrorException - thrown when expected != currentChar
     */
    private void eat(char expected) throws ScanErrorException
    {
        if(expected == currentChar)
        	getNextChar();
        else
        	throw new ScanErrorException("Illegal character - expected " + currentChar + " and found " + expected + ".");
    }
    /**
     * Method: hasNext
     * used to determine whether the scanner has a next character to read
     * the opposite of eof is returned because eof keeps track of whether or not the reader
     * is at the end of the file (i.e. if eof is true, there is no next character so return false)
     * @return !eof - the opposite of the boolean that tracks if the reader is at the end of the file
     */
    public boolean hasNext()
    {
    	return !eof;
    }
    
    /**
     * Method: isDigit
     * used to determine if the given character is a digit in the source
     * does this by checking it through the regular expression [0-9] (i.e. the digit is something between 0 and 9 inclusive)
     * returns true, if it is between (inclusive) those
     * returns false if it is not between (inclusive) those
     * RegEx
     * digits := [1-9]
     * @param c - the given character to test whether it is a digit or not
     * @return boolean that indicates whether it is a digit or not
     */
    public static boolean isDigit(char c)
    {
    	return (c >= '0' && c <= '9');
    }
    
    /**
     * Method: isLetter
     * used to determine if the given character is a letter in the source
     * does this by checking it through the regular expression [a-z A-Z] (ie all the letters of the english alphabet
     * including upper case letters)
     * returns true if it is a letter (of either case)
     * returns false if it is not a letter
     * RegEx
     * letters := [a-z A-Z]
     * @param c - the given character to test whether it is a letter or not
     * @return boolean that indicates whether it is a letter or not
     */
    public static boolean isLetter(char c)
    {
    	return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
    }
    
    /**
     * Method: isWhiteSpace
     * used to determine if the given character is a form of whitespace
     * does this by checking it through the regular expression [' ' '\r' '\n' '\t'] (i.e. all possible whitespace combinations)
     * returns true if it is a whitespace
     * returns false if it is not
     * RegEx
     * whitespace := [' ' '\r' '\n' '\t']
     * @param c - the given character to test whether it is whitespace or not
     * @return boolean that indicates whether it is a whitespace or not
     */
    public static boolean isWhiteSpace(char c)
    {
    	return (c == ' ' || c == '\n' || c == '\r' || c == '\t');
    }
    
    /**
     * Method: nextToken
     * used to get the next token from the source. (propagates the scan exception that could occur)
     * does this by looping through whitespace until it hits the start of a potential number, identifier, operand, etc
     * once found, it will determine if it is the start of a specific token, and then scans it with one of the scan methods
     * returns the token
     * if the token happens to be an comment operand, it will use its helper handler methods
     * to ignore the appropriate source that is considered a comment (even accounts for nested comment blocks)
     * if end of file is reached, it returns "END"
     * @precondition: currentChar is defined
     * @postcondition: currentChar is progressed (to end of file or end of token), token returned
     * @return token - the token that was constructed based on the next character after whitespace
     * @throws exception - ScanErrorException thrown when unidentified token combination/syntax is found in scan methods
     */
    public String nextToken() throws ScanErrorException
    {
    	String token = "END";
    	while(hasNext() && isWhiteSpace(currentChar))
    	{
    		eat(currentChar);
    	}
        if(hasNext())
        {
        	if(isDigit(currentChar))
        		token = scanNumber();
        	else if(isLetter(currentChar))
        		token = scanIdentifier();
        	else
        	{
        		token = scanOperand();
        		/**if(token.equals("//"))
        		{
        			handleLineComment();
        			token = nextToken();
        		}
        		else if(token.equals("/*"))
        		{
        			handleBlockComment();
        			token = nextToken();
        		}*/
        	}
        }
        return token;
    }    
    
    /**
     * Method: scanNumber
     * method called when the token is determined to be a number (ie starting with a digit)
     * this method will loop through the characters after it and appropriately append
     * them to a string that will result in the compiled number.
     * once it hits a token that would end the term (whitespace) the token is complete
     * and returned
     * if it runs into anything else (like a letter) in the middle of the number, it will throw an exception
     * number is only represented as (digit)(digit)*
     * invalid chars that can follow a number are: letters
     * @precondition: currentChar is not a letter or whitespace
     * @postcondition: currentChar is progressed, token returned
     * @return	token - the string comprised of the valid digits to make a number;
     * @throws ScanErrorException - thrown if the token is considered invalid (ie a token that starts with a number but has letters in it
     */
    public String scanNumber() throws ScanErrorException
    {
    	String token = "" + currentChar;
    	eat(currentChar);
    	while(hasNext() && isDigit(currentChar))
    	{
    		token += currentChar;
    		eat(currentChar);
    	}
    	if(hasNext() && isLetter(currentChar))		//if a letter is placed in a  number, it is immediately invalid, this is not an acceptable token
    		throw new ScanErrorException();
    	else
    		return token;
    }
    
    /**
     * Method: scanIdentifier
     * method called when token is determined to b a identifier (ie starting with a letter)
     * this method will loop through the characteers after it and appropriately append them 
     * to a string that will result in a compiled identifier
     * once it hits a token that would end the term (whitespace) the token is compete and returned
     * invalid chars that can follow an identifier are: 
     * @precondition: currentChar is not a digit or whitespace
     * @postcondition: currentChar is progressed, token returned
     * @return token - the string comprised of all the valid digits/letters to make an identifier;
     * @throws ScanErrorException - under conditions when following chars are not valid, this would be thrown (not as of now since any type of char can follow at the moment)
     */
    private String scanIdentifier() throws ScanErrorException
    {
    	String token = "" + currentChar;
    	eat(currentChar);
    	while(hasNext() && isDigit(currentChar) || isLetter(currentChar))
    	{
    		token += currentChar;
    		eat(currentChar);
    	}
    	//any other type of char can follow (including operands and whitespace, it just ends the token)
    	return token;
    }
    
    /**
     * Method: scanOperand
     * method called when token is determined to be an operand (ie unidentified)
     * since operands are just single characters, no looping is needed as of now
     * since the operands can only be represented in an array, it searches throw this array for a matching operand
     * if none, given, it throws an exception
     * at the end, it must move to the next char so it eats
     * An exception will be thrown at any invalid character (i.e. not ones in the RegEx)
     * THIS METHOD WILL ONLY EAT (I.E. MOVE THE SCANNER ALONG) WHEN IT CONFIRMS THAT currentChar MAKES A VALID TOKEN.
     * Otherwise it will return the token without skipping characters.
     * By doing this, it will not overlook any characters if the operand fails at a certain length.
     * 
     * RegEx
     * operators := [=, +, -, *, /, %, (, ), {, }, <, >, ., &&, ||, >=, <=, ;, !, ==, !=, :=, +=, -=, //, /*, \*\*]
     * @precondition currentChar is not a letter, digit or whitespace
     * @postcondition: currentChar is progressed, token returned
     * @return token - the string comprised of the operand
     * @throws ScanErrorException - under conditions when a character is not valid, exception is thrown
     */
    private String scanOperand() throws ScanErrorException
    {
    	//char[] operand=  {'=', '+', '-', '*', '/', '%', '(', ')', ';', '!', ':'};				//array of valid operands
    	//String[] compoundOperators = {"==", "===", ":=", "!=", "//", "/*", "*/"};
    	String[] operators = {"=", "+", "-", "*", "/", "%", "(", ")", "{", "}", "<", ">", ".", ",", "&&", "||", "<>", ">=", "<=", ";", "!", "==", "!=", ":=", "//", "/*", "*/", "+=", "-="};
    	String token = "";
    	int length = 1;
    	String temp = "" + currentChar;
    	
    	int i = 0;
    	while (i < operators.length 
    			&& hasNext() 
    			&& !isWhiteSpace(currentChar) 
    			&& !isLetter(currentChar) 
    			&& !isDigit(currentChar))
    	{
    		if((operators[i].length() >= length) && (operators[i].substring(0, length).equals(temp)))		//before testing, the operator's length has to be at least the length of currentChar, then test.
    		{
    			if(length == operators[i].length())
    				token = temp;
    			eat(currentChar);
    			temp += currentChar;
    			length++;
    		}
    		else
    		{
    			i++;		//don't increment if it is a match, because this term will have to be checked again
    		}
    	}
    	
    	if(token.length() > 0)
    		return token;
    	else
    		throw new ScanErrorException("token: " + token);
    }
    
    
    /**
     * Method: handleLineComment
     * eats through all characters until it runs into a whitespace that is specifically "\n" because
     * that is the only character that will end the comment line, everything else is ignored
     * nothing is taken as a parameter and nothing is returned
     * @precondition: currentChar is immediately after the "//"
     * @postcondition: ate through everything on current line until "\n"
     */
    public void handleLineComment() throws ScanErrorException
    {
    	while(currentChar != '\n' && hasNext())
    		eat(currentChar);
    }
    
    /**
     * Method: handleLineComment
     * eats through all the characters until it runs into the next operator that would close
     * the comment which is the \*\/ character. everything within the block will be ignored.
     * @precondition: currentChar is immediately after a /* operand
     * @postcondition: ate through everything until close comment line
     */
    public void handleBlockComment() throws ScanErrorException
    {
    	String operator = "";
    	while (!operator.equals("*/") && hasNext())
    	{
    		if(!isWhiteSpace(currentChar) 
    				&& !isLetter(currentChar) 
    				&& !isDigit(currentChar))
    		{
    			operator = scanOperand();
    		}
    		eat(currentChar);
    	}
    }
    
    
}
