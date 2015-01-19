package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * class that represents an if statement (meaning that
 * it extends the statement abstract class
 * this class takes in a condition and then 
 * a statement to execute if that condition is true
 * when an if statement is executed, it tests
 * for the value of the condition and then 
 * determines whether or not to execute the given stmt
 * @author Jonathan Lee
 * @version 13 Oct 2014
 * 
 *
 */
public class If extends Statement
{
	private Condition cond;
	private Statement stmt;
	
	
	/**
	 * method: If
	 * constructor method used to set the values
	 * of the instance fields cond and stmt
	 * @param cond	condition that the if statement tests before executing statement
	 * @param stmt	statement that the if statement executes if cond is true
	 * postcondition: instance field set
	 */
	public If(Condition cond, Statement stmt)
	{
		this.cond = cond;
		this.stmt = stmt;
	}
	
	/**
	 * method: exec
	 * usage: program.exec(env)
	 * method that executes the if statement
	 * by evaluating the condition and determining whether
	 * it is true or false. if true, it executes its given statement
	 * if false it does nothing
	 * @param env	environment used for variable reference
	 * postcondition: cond evaluated, maybe stmt executed
	 */
	public void exec(Environment env)
	{
		if(cond.eval(env))
			stmt.exec(env);
	}

	@Override
	/**
	 * method: compile
	 * usage: program.compile
	 * compiles the pascal code to mips code in the asm
	 * file. if statements account for a condition which it also compiles
	 * if statements must set up labels to control redirects by conditions
	 * @param e		emitter to asm file
	 */
	public void compile(Emitter e)
	{
		String label = "endif" + e.nextLabelID();
		cond.compile(e, label);
		stmt.compile(e);
		e.emit(label + ":");
	}
}
