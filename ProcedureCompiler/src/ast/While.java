package ast;
/**
 * @author Jonathan Lee
 * @version 3 Nov 2014
 * subclass of statement as it does not return a value or have an evaluation
 * While class which represents the action of a while loop.
 * while loops contian a condition that whether true or false
 * will determine the execution of hte given statement.
 * the execution of a while loop is simply to 
 * execute the given statement until the continuously evaluated
 * condition is false (which could also be on the first run)
 */
import emitter.Emitter;
import environment.Environment;

public class While extends Statement
{
	private Condition cond;
	private Statement doStmt;
	
	/**
	 * method: While
	 * constructor method for the while loop class
	 * which acts as a setter method for the instance
	 * variables
	 * @param cond		the given condition that determines the running of the statement
	 * @param doStmt	the statement to be executed under the condition
	 * postcondition: instance fields set
	 */
	public While(Condition cond, Statement doStmt)
	{
		this.cond = cond;
		this.doStmt = doStmt;
	}

	@Override
	/**
	 * method: exec
	 * usage: program.exec(env)
	 * executes the while loop by continuously executing the 
	 * statement and then reevaluating the condition to see if
	 * it is true, as long as it is true then it will continue to execute
	 * the given statement
	 * @param env	the environment for id reference
	 * postcondition: doStmt executed continuously as long as condition is true
	 */
	public void exec(Environment env)
	{
		while(cond.eval(env))
			doStmt.exec(env);
	}

	@Override
	/**
	 * method: compile
	 * usage: program.compile(e)
	 * compiles the program from pascal code
	 * to mips code which is done by creating
	 * two labels, one which is passed to the condition
	 * where the program will go to that label if the 
	 * condition is not true or it will go back to the other
	 * label if it is true to redo the loop
	 * @param e		emitter to asm file
	 */
	public void compile(Emitter e)
	{
		String loopLabel = "loop" + e.nextLabelID();
		String endLabel = "endloop" + e.nextLabelID();
		e.emit(loopLabel + ":");
		cond.compile(e, endLabel);
		doStmt.compile(e);
		e.emit("j " + loopLabel);
		e.emit(endLabel + ":");
	}
	
	
}
