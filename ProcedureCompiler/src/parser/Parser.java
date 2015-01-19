package parser;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import environment.Environment;

import scanner.Scanner;
import ast.Assignment;
import ast.BinOp;
import ast.Block;
import ast.Condition;
import ast.Expression;
import ast.Number;
import ast.ProcedureCall;
import ast.ProcedureDeclaration;
import ast.Program;
import ast.Statement;
import ast.Variable;
import ast.While;
import ast.Writeln;
import ast.If;

/**
 * Parser class designed to recognize arrangement and grammar of expressions delivered by the scanner
 * in the form of tokens
 * 
 * Statements are executed based on their keywords.
 * values of expressions are found based on their recursively defined terms and factors
 * This leads to a system of downward increasing precedence. As the parse tree is created through
 * factoring rather than descent, the lower branches of the tree have a higher precedence in the order
 * of operations.
 * input is examined until a "." is reached indicating the end of input
 * 
 * the program statement is then executed after the tree is constructed rather 
 * than as it is constructed.
 * 
 * only Parser and eat methods modify token
 * @author Jonathan Lee
 * @version 3 Nov 2014
 * 
 * 
 * 
 * 
 * 
 * 
 * stmt -> WRITELN(expr); | BEGIN stmts END; | id := expr; | IF cond THEN expr | WHILE cond DO stmt
 * stmts -> stmts stmt | e
 * expr -> expr + term | expr - term | term
 * term -> term * factor | term / factor | factor
 * factor -> (exp)
 * factor -> -factor
 * factor -> num
 * 
 * so updated, organized grammar for left factor
 * program -> PROCEDURE(maybeparms); stmt program | stmt .
 * maybevars -> VAR vars | e
 * vars -> id, vars | id;
 * maybeparms -> parms | e
 * parms -> parms id | id
 * stmt -> WRITELN(expr); | BEGIN stmts END; | id := expr ; | IF cond THEN stmt | WHILE cond DO stmt
 * stmts -> stmts stmt | e
 * exp -> term whileexp
 * whileexp -> + term whileexp | - term whileexp | e
 * term -> factor whileterm
 * whileTerm -> * factor whileterm | / factor whileterm | e
 * factor -> (exp) | -factor | num | id(maybeargs) | id
 * args -> args | e
 * arg -> args , exp | exp
 * cond -> exp relop exp
 * relop -> = | <> | < | > | <= | >=
 */
public class Parser {
	
	/**
	 * method: main
	 * main method called at runtime
	 * creates a new parser method thus setting
	 * the program up
	 * @param args		//arguments from cl
	 */
	public static void main(String[] args)
	{
		Parser parser = new Parser();
	}
	
	
	/**
	 * var: token
	 * current token that the parser is examining from scanner
	 */
	private String token;
	
	/**
	 * var: scanner
	 * scanner object that returns tokens in sequence from input
	 */
	private Scanner scanner;
	
	/**
	 * var: map
	 * map that contains the variables and values for sotring identifiers
	 
	private HashMap<String, Integer> map;
	*/
	
	/**
	 * method: Parser
	 * usage: new Parser()
	 * creates new hashmap. gets file for scanner
	 * this method parses the input until it reaches a . indicating
	 * the end of input. Exceptions can be thrown from this, so it
	 * is surrounded in a try-catch block to catch any
	 * one of the two methods that modifies token
	 * the parsed statement is then executed by the parser once its ast is created
	 * precondition: file exists
	 * postcondition: input parsed using left factoring parse methods
	 */
	public Parser()
	{
		//map = new HashMap<String, Integer>();
		File file = new File("src/files/compiler.txt");
		try
		{
			System.out.println("file name: " + file.getName());
			scanner = new Scanner(new FileInputStream(file));
			token = scanner.nextToken();
			Environment env = new Environment();
			Program program = parseProgram();
			program.compile("src/files/compiled.asm");
			//parseProgram().exec(env);
		}
		catch(Exception e)		//exception would be a file not found exception or an exception in nextToken, in that case, shut down
		{
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * method: eat
	 * usage: program.eat(string)
	 * "eats" the current token by reassigning token to the next
	 * token returned by the scanner. takes an expected token to match with current
	 * token to make sure that they are consistent (thus synchronized for the next token)
	 * exception thrown if they are not (program quits because there is nothing you can do)
	 * one of the two methods that modify token
	 * @param expected		the expected token to be received
	 * postcondition: token set to the next token given by the scanner
	 * @throws IllegalArgumentException		if expected and current are not matching
	 */
	private void eat(String expected) throws Exception
	{
		if(expected.equals(token))
			token = scanner.nextToken();
		else
			throw new IllegalArgumentException("Expected token: '" + expected + "'. Received token: '" + token + "'.");
	}
	
	/**
	 * method: parseProgram
	 * usage: program.parseProgram
	 * method parses a given program by comparing the leading token
	 * with the potential program definitions that could be formed
	 * variable declarations happen at the very start of the program with keyword VAR
	 * and then the list of variables.
	 * either starting with a variable declaration procedure or a statement, parses the state
	 * ment if leading token is not PROCEDURE
	 * this method will also recognize parameters for the procedure in the
	 * specified format
	 * program -> PROCEDURE id(); stmt program | stmt .
	 * so essentially it will look like a list of procedures followed by a lone statement and a dot
	 * postcondition: program parsed with given procedures and program statement
	 */
	public Program parseProgram() throws Exception
	{
		Program program;
		List<String> varDecs = new ArrayList<String>();
		if(token.equals("VAR"))
		{
			varDecs = parseDeclaredVariables();
		}
		
		List<ProcedureDeclaration> decs = new ArrayList<ProcedureDeclaration>();
		
		while(token.equals("PROCEDURE"))			//begin parsing the procedure by taking the
		{											//id as a string and the statement via parseStatement
			eat("PROCEDURE");
			String id = token;
			eat(id);
			eat("(");
			List<String> parms = new ArrayList<String>();
			while(!token.equals(")"))
			{
				parms.add(token);
				eat(token);
				if(token.equals(","))		//there may not be a following common (end of list of params)
					eat(",");
			}
			eat(")");
			eat(";");
			List<String> localVars = new ArrayList<String>();
			if(token.equals("VAR"))
			{
				localVars = parseDeclaredVariables();
			}
			Statement proStmt = parseStatement();
			decs.add(new ProcedureDeclaration(id,parms, proStmt, localVars));
		}
		Statement stmt = parseStatement();
		eat(".");									//must encounter a period to end
		program = new Program(stmt, decs, varDecs);			//program parsed with stmt and procedures list
		return program;
	}
	
	
	/**
	 * method: praseDeclaredVariables
	 * usage: program.parseDeclaredVariables()
	 * parses declared variables by adding them one
	 * by one to the list and then returning the list
	 * @precondition: token is "VAR" there is at least one declared var, separated by commas, semi-colon at end
	 * @return returns arraylist of full of all the declared vars
	 */
	public List<String> parseDeclaredVariables() throws Exception
	{
		ArrayList<String> list = new ArrayList<String>();
		eat("VAR");
		list.add(token);
		eat(token);
		while(token.equals(","))
		{
			eat(",");
			list.add(token);
			eat(token);
		}
		eat(";");
		return list;
	}
	
	/**
	 * method: parseStatement
	 * usage: program.parseStatement()
	 * method parses a given statement by comparing the leading token wiht
	 * potential statements that could be formed such as writeln or begin
	 * if the given token does not match any provided in the method's grammar,
	 * an exception is thrown. statement is abstract those these different kinds of statements
	 * can be executed in unique ways.
	 *	stmt -> WRITELN ( expr ) ; | BEGIN stmts END ; | id := expr ; | IF cond THEN expr | WHILE cond DO stmt
	 *	stmts -> stmts stmt | e
	 *	expr -> expr + term | expr - term | term
	 *	term -> term * factor | term / factor | factor
	 *	factor -> ( expr ) | - factor | num | id
	 * postcondition: advanced down input using eat method, returned statement object, or exception thrown
	 * @return statement created by parsing in specific ways the registered statements according to the grammar
	 * @throws Exception described in eat()
	 */
	public Statement parseStatement() throws Exception
	{
		Statement stmt;
		if(token.equals("WRITELN"))
		{
			eat("WRITELN");
			eat("(");
			Expression exp = parseExpression();
			eat(")");
			eat(";");
			stmt = new Writeln(exp);
		}
		else if(token.equals("BEGIN"))
		{
			eat("BEGIN");
			List<Statement> block = new ArrayList<Statement>();
			while(!token.equals("END"))
				block.add(parseStatement());
			eat("END");
			eat(";");
			stmt = new Block(block);
		}
		else if(token.equals("IF"))
		{
			eat("IF");
			Condition cond = parseCondition();
			eat("THEN");
			Statement consequence = parseStatement();
			
			stmt = new If(cond, consequence);
			
		}
		else if(token.equals("WHILE"))
		{
			eat("WHILE");
			Condition cond = parseCondition();
			eat("DO");
			Statement doStmt = parseStatement();
			stmt = new While(cond, doStmt);
		}
		else if(Scanner.isLetter(token.charAt(0)))		//testing to see if it is an identifier
		{
			String var = token;							//HEADS UP: THIS COULD BE USED JUST AS A PARSE VARIABLE CALL
			eat(var);									//AS SOON AS THAT METHOD IS UNDERSTOOD, THIS SHOULD BE CHANGED
			eat(":=");
			Expression exp = parseExpression();
			eat(";");
			stmt = new Assignment(var, exp);
		}
		else
		{
			throw new Exception("Invalid Statement");
		}
		return stmt;
	}
	
	
	/**
	 * method parseExpression
	 * usage: program.parseExpression()
	 * left recursive
	 * this method parses specific expressions in a general way
	 * expressions are exp joined with terms with either a + or a -
	 * they can also be lone terms
	 * thus this is constructed as a while method (defined in class documentation)
	 * either constructs a single term or a binop expression to be returned.
	 * postcondition: advanced down input, expressoin constructed
	 * @return	single term expression or binop expression using parseTerm
	 * @throws Exception described in eat()
	 */
	public Expression parseExpression() throws Exception
	{
		//assuming that token is a term
		Expression exp1 = parseTerm();
		while(token.equals("+") || token.equals("-"))
		{
			String op = token;
			eat(op);
			Expression exp2 = parseTerm();
			exp1 = new BinOp(op, exp1, exp2);
		}
		return exp1;
	}
	
	/**
	 * method parseTerm
	 * usage: program.parseTerm()
	 * left recursive
	 * this method parses specific terms in a general way
	 * terms are term joined with factors with either a * or a /
	 * they can also be lone factors
	 * thus this is constructed as a while method (defined in class documentation)
	 * either constructs a single factor or a binop expression to be returned.
	 * postcondition: advanced down input, expressoin constructed
	 * @return	single factor expression or binop expression using parseFactor
	 * @throws Exception described in eat()
	 */
	public Expression parseTerm() throws Exception
	{
		Expression exp1 = parseFactor();
		while(token.equals("*") || token.equals("/"))
		{
			String op = token;
			eat(op);
			Expression exp2 = parseFactor();
			exp1 = new BinOp(op, exp1, exp2);
		}
		return exp1;
	}
	
	/**
	 * method: parseFactor()
	 * usage: program.parseFactor()
	 * this method parses specific factors in the form of expressions
	 * does this by identifying the various types of factors
	 * such as those contained in parentheses, preceded by a -, variables
	 * or numbers
	 * parses each accordingly as described in grammar in class definition
	 * then returns a constructed expression
	 * tokens that could be variables or function calls are parsed as references.	 
	 * * postcondition: advanced down input, expression constructed
	 * @return	constructed expression based on type of factor passed from input
	 * @throws Exception described in eat()
	 */
	public Expression parseFactor() throws Exception
	{
		Expression exp;
		if(token.equals("("))
		{
			eat("(");
			exp = parseExpression();
			eat(")");
		}
		else if(token.equals("-"))
		{
			eat("-");
			exp = new BinOp("*", (Expression)(new Number(-1)), parseFactor());
		}
		else if(Scanner.isLetter(token.charAt(0)))
			exp = parseReference();
		else
			exp = parseNumber();
		
		return exp;
	}
	
	/**
	 * method: parseReference
	 * usage: program.parseReference
	 * method that parses a particular reference. the reason
	 * for this method is that a factor that starts with a
	 * letter could be a variable or a procedure call.
	 * procedure calls could contain arguments with expressions
	 * in between the parentheses and separated by commas.
	 * this method will determine which it is and then return
	 * the appropriate object (either ProcedureCall or Variable)
	 * @return	expression that is either a procedurecall or variable based on
	 * 			the ()
	 * @throws Exception described in eat()
	 * postcondition: advanced down input
	 */
	public Expression parseReference() throws Exception
	{
		String id = token;
		eat(id);
		List<Expression> args = new ArrayList<Expression>();
		if(token.equals("("))					//we know this is now a procedure call
		{
			eat("(");
			while(!token.equals(")"))
			{
				args.add(parseExpression());
				if(token.equals(","))
					eat(",");
			}
			eat(")");
			return new ProcedureCall(id, args);
		}
		else									//it is a variable reference
		{
			return new Variable(id);
		}
		
	}
	
	/**
	 * method: parseVariable
	 * usage: program.parseVariable()
	 * parses the variable by constructing it as an expression
	 * with the given identifier as the token
	 * postcondition: advances down input
	 * @return	returns the constructed variable with the given name
	 * @throws Exception described in eat()
	 */
	public Expression parseVariable() throws Exception
	{
		Variable var = new Variable(token);
		eat(token);
		return var;
	}
	
	/**
	 * method: parseNumber
	 * usage: program.parseNumber()
	 * parses the number by constructing a number object
	 * with the given integer as its value using
	 * the parse integer value to get the int value from the string token
	 * postcondition: advances down input
	 * @return the constructed number with its value
	 * @throws Exception described in eat()
	 */
	public Expression parseNumber() throws Exception
	{
		return new Number(parseInteger());
	}
	
	
	
	/**
	 * method: parseInteger
	 * usage: program.parseInteger()
	 * this method parses the given number from the token string form to an integer form
	 * basically asks as a format converter
	 * calls the eat method to advance down input
	 * precondition: token is a number
	 * postcondition: number token has been eaten
	 * @return	integer value of the current token
	 * @throws Exception described in eat()
	 */
	private int parseInteger() throws Exception
	{
		int num = Integer.parseInt(token);
		eat(token); //this could throw an exception
		return num;
	}
	
	
	/**
	 * method: parseCondition
	 * usage: program.parseCondition
	 * parses the condition by separating it into expressions
	 * and an relative operator, given by the grammar in the class
	 * definition
	 * returns this compiled condition
	 * postcondition: advanced along input by eating
	 * @return	compiled condition from parsing expression, then relop, then expression
	 * @throws Exception	expression described in eat() for unexpected tokens or invalid relative operator
	 */
	public Condition parseCondition() throws Exception
	{
		Expression exp1 = parseExpression();
		if(token.equals("=") 		|| 
				token.equals("<>") 	|| 
				token.equals(">") 	|| 
				token.equals("<") 	|| 
				token.equals("<=") 	|| 
				token.equals(">="))
		{
			String relop = token;
			eat(relop);
			Expression exp2 = parseExpression();
			return new Condition(relop, exp1, exp2);
		}
		else
		{
			throw new Exception("Invalid relative operator");
		}
	}
	
	
	
	
	
	
	/**
	 * method: parseNumber
	 * usage: program.parseNumber()
	 * this method parses the given number from the token string form to an integer form
	 * basically asks as a format converter
	 * calls the eat method to advance down input
	 * precondition: token is a number
	 * postcondition: number token has been eaten
	 * @return	integer value of the current token
	 *
	private int parseNumber() throws Exception
	{
		int num = Integer.parseInt(token);
		eat(token); //this could throw an exception
		return num;
	}
	
	/**
	 * method: parseStatement
	 * usage: program.parseStatement
	 * 
	 * method examines current token to determine the type of statement
	 * if it is a writeline statement, it follows a specific sequence of tokens that follow
	 * the command and prints out the expression within
	 * if it is a begin statement, it executes the statements in between th begin command and
	 * the end command
	 * if it is an identifier, it sets the id equal to the value on the right hand side of the expression
	 * using the map instance variable
	 * otherwise the token is an invalid token and exception is thrown
	 * if the tokens are not followed by appropriate tokens, exception is thrown
	 * 
	 * grammar defined in the class definition
	 * 
	 * so organized grammar for left factor
	 * 
	 * stmt -> WRITELN(expr); | BEGIN whilebegin | id := expr ;
	 * whilebegin -> END; | stmt whilebegin
	 * exp -> term whileexp
	 * whileexp -> + term whileexp | - term whileexp | e
	 * term -> factor whileterm
	 * whileTerm -> * factor whileterm | / factor whileterm | e
	 * factor -> (exp) | -factor | num | id
	 * 
	 * @throws Exception	exception thrown when tokens are out of order or the wrong format
	 * precondition: token is WRITELN command
	 * postcondition: handles a statement and its commands. (print, begin, set)
	 *
	public void parseStatement() throws Exception
	{
		if(token.equals("WRITELN"))			//sequence should always be WRITELN(...);
		{
			//next token must be open parenthesis, otherwise exception
			eat(token);
			if(!token.equals("("))
				throw new IllegalArgumentException("Expected token '('. Received token '" + token + "'.");
			//next token must be construed as a factor
			eat(token);
			System.out.println(parseExp());
			//next token must be close parenthesis, parseNumber already calls the eat method
			if(!token.equals(")"))
				throw new IllegalArgumentException("Expected token ')'. Received token '" + token + "'.");
			eat(token);
			if(!token.equals(";"))
				throw new IllegalArgumentException("Expected token ';'. Received token '" + token + "'.");
			eat(token);		//continue moving since done with this ; token
		}
		else if(token.equals("BEGIN"))			//sequence should always be BEGIN ... END;
		{
			eat(token);
			while(!token.equals("END") && !token.equals("."))
			{
				parseStatement();
			}
			if(!token.equals("END"))		//throw an error because expecting an end to the begin
				throw new IllegalArgumentException("Expected token 'END'. Received token '" + token + "'.");
			eat(token);
			if(!token.equals(";"))
				throw new IllegalArgumentException("Expected token ';'. Received token '" + token + "'.");
			eat(token);		//token exampled, so continue to advance along input
		}
		else if(Scanner.isLetter(token.charAt(0)))			//makes sure that it is now analyzing an identifier type
		{
			String id = token;
			eat(token);
			if(!token.equals(":="))
				throw new IllegalArgumentException("Expected token ':='. Received token '" + token + "'.");
			eat(token);
			int val = parseExp();
			map.put(id, val);
			eat(token);		//continue to progress
		}
		else
		{
			throw new IllegalArgumentException("Unexpected token: '" + token + "'.");
		}
		
	}
	
	/**
	 * method: parseFactor
	 * usage: program.parseFactor()
	 * method parses the given factor
	 * factor is always broken down to it's numerical value by using parseNumber method
	 * if the number is preceded by a -, the negative of it parseFactor is returned
	 * method is recursive to handle instances when there is an expr within the factor,
	 * or a factor within the factor
	 * see class definition for grammar
	 * parse factor will automatically eat beyond its scope
	 * @return val - value of the recursively found factor
	 * postcondition: returns the value of the factor defined in the grammar, advances along token input to end of factor
	 *
	public int parseFactor() throws Exception
	{
		int val = 0;				//in the case of an error, set val to 0, so it's useless
		if(token.equals("("))	//starts with open paren
		{
			//handle factor within parenthesis
			eat(token);
			val = parseExp();
			if(!token.equals(")"))			//what starts with open paren must end in close paren or throw exception
				throw new IllegalArgumentException("Expected token ')'. Received token '" + token + "'.");
			else
				eat(token);
		}
		else if(token.equals("-"))		//negative sign in front, factor follows
		{
			eat(token);
			val = parseFactor() * -1;		//set to opposite sign
		}
		else if(Scanner.isLetter(token.charAt(0)))
		{
			val = map.get(token);
			eat(token);		//does not automatically eat
		}
		else	//must be a number (or an error), parse it send it, let parser eat, send it back
		{
			val = parseNumber();		//automatically eats to next
		}
		return val;
		
		
		
	}

	/**
	 * method: parseTerm
	 * usage: program.parseTerm
	 * 
	 * parse term uses a left factoring method of determining
	 * the value of the term. first parses and factor and then
	 * continuously looks for specific operators * and /
	 * and determines the combined value of hte original factor
	 * with each new factor that is appended to it from the input
	 * final value returned
	 * 
	 * so organized grammar
	 * term -> factor whileterm
	 * whileTerm -> * factor whileterm | / factor whileterm | e
	 * factor -> (term) | -factor | num
	 * @return	val - value of the term which is a * or / expression
	 * @throws Exception	thrown in the event that consuming input throws an exception
	 * postcondition: advances along token input, returns value of parsed term.
	 *
	public int parseTerm() throws Exception
	{
		int val = 0;
		//first piece is determined to be a factor no matter what
		val = parseFactor();
		while(token.equals("*") || token.equals("/"))	//empty token (or token that is not / or *, will break the while loop
		{
			
			if(token.equals("*"))
			{
				eat(token);
				val = val * parseFactor();
			}
			else if(token.equals("/"))
			{
				eat(token);
				val = val / parseFactor();
			}
		}
		
		return val;
		
	}

	/**
	 * method: parseExp
	 * usage: program.parseExp()
	 * method that parses expressions involving + and -
	 * first parses the first term that may include * or  /
	 * and then precedes to look for appended + or - operators
	 * in this case, it continuously adds or subtracts the values of following
	 * terms to achieve increasing precedence in the same way that parseTerm does 
	 * @return	value of the parsed expression through left factoring
	 * @throws Exception	exception thrown when eating or receiving a misplaced token
	 * postcondition: advances along token input, returns value of parsed expression
	 *
	public int parseExp() throws Exception
	{
		int val = 0;
		//first piece is determined to be a factor no matter what
		val = parseTerm();
		while(token.equals("+") || token.equals("-"))	//empty token (or token that is not / or *, will break the while loop
		{
			
			if(token.equals("+"))
			{
				eat(token);
				val = val + parseTerm();
			}
			else if(token.equals("-"))
			{
				eat(token);
				val = val - parseTerm();
			}
		}
		
		return val;
	}

	
	*/
}




