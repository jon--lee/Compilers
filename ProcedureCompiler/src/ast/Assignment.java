package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * assignemtn class which represents
 * a statement that assigns a variable to 
 * an expression when evaluated
 * @author Jonathan Lee
 * @version 9 Oct2014
 *
 */
public class Assignment extends Statement
{
	private String var;
	private Expression exp;
	
	/**
	 * method: assignment
	 * constructor method that takes in the varaible name
	 * and the expression that it will be assigned to
	 * @param var		variable name
	 * @param exp		expression that variable is associated with
	 * postcondition: instance field set
	 */
	public Assignment(String var, Expression exp)
	{
		this.var = var;
		this.exp = exp;
	}

	@Override
	/**
	 * method: exec
	 * usage: program.exec()
	 * executes the statement by setting hte variable
	 * name to the expression value in the env
	 * variable for future reference.
	 * declares variable
	 * @param env	environment used for variable definitions
	 * postcondition: env set with new key-value
	 */
	public void exec(Environment env)
	{
		env.declareVariable(var, exp.eval(env));
	}

	@Override
	/**
	 * method: compile
	 * usage: program.compile(e)
	 * compiles the code from pascal form
	 * to mips code in the asm file
	 * does this by compiling the value of the expression
	 * and then loading that value into the reference for
	 * the given label.
	 * if global, will set to the address of the given lablel with "var"
	 * if local, then it will search the current procedure declarations
	 * parameters and reassign to map to given expression value at $v0
	 * postcondition: compiled code for assignment operation
	 */
	public void compile(Emitter e) 
	{
		exp.compile(e);		//assuming that expression value is loaded into $v0 as a base variable
		if(e.isLocalVariable(var))
		{
			int offset = e.getOffset(var);
			e.emit("addu $t0 $sp " + offset);
			e.emit("sw $v0 ($t0)");
		}
		else
		{
			//so now load the value of $v0 into the given variable label which is a string
			e.emit("la $t0 var" + var);
			e.emit("sw $v0 ($t0)");
		}
	}
}
