package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * Writeln class which is a statement that
 * when executed prints out the value of the expression that it contains
 * it holds this expression as an instance field, accepting it when
 * it is initialized.
 * @author Jonathan Lee
 * @version 9 Oct2014
 *
 */
public class Writeln extends Statement
{
	private Expression exp;
	
	
	/**
	 * method: Writeln
	 * used purely to set the instance field to the give
	 * expression
	 * @param exp	expression passed to have written
	 * postcondition: exp set
	 */
	public Writeln(Expression exp)
	{
		this.exp = exp;
	}

	
	@Override
	/**
	 * method: exec
	 * usage: program.exec(env);
	 * executes the statement causing the object
	 * to print out the value of exp using the
	 * given environment
	 * @param env	environment used for variable definitions
	 * postcondition: printed out expression value
	 */
	public void exec(Environment env)
	{
		System.out.println(exp.eval(env));
	}
	
	/**
	 * method: compile
	 * usage: program.compile(e)
	 * compile method that converts the ast
	 * statement writeln in to an executable
	 * mips statement that outputs the given expression
	 * (expression is first compiled);
	 */
	public void compile(Emitter e)
	{
		exp.compile(e);
		e.emit("move $a0 $v0");
		e.emit("li $v0 1");
		e.emit("syscall");
		
		e.emit("li $v0 4");
		e.emit("la $a0 newline");
		e.emit("syscall");
	}
}
