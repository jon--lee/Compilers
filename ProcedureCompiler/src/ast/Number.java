package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * Number class that represents a basic number
 * in the form of an integer
 * when evaluated it just returns the integer value.
 * @author Jonathan Lee
 * @version 9 Oct2014
 *
 */
public class Number extends Expression
{
	private int value;
	
	/**
	 * method: Number
	 * constructor method that just
	 * sets the value of the number
	 * @param value	value to be set
	 * postcondition: value set
	 */
	public Number(int value)
	{
		this.value = value;
	}

	@Override
	/**
	 * method: eval
	 * usage: program.eval();
	 * evalues the number by returning its integer value
	 * as the isntance field
	 * @param env	environment used for variable definitions
	 * postcondition: number evaluated by returning integer value
	 */
	public int eval(Environment env)
	{
		return value;
	}
	
	/**
	 * method: compile
	 * usage: program.compile(e)
	 * compile method that converts the ast
	 * number into something readable by mips
	 * by loading it into the variable $v0
	 * @param e		emitter used to write to output file
	 * postcondition: write to mips file for number value in $v0
	 */
	public void compile(Emitter e)
	{
		e.emit("li $v0 " + value);
	}
}
