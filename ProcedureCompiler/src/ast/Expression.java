package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * Abstract expression class extended by sub classes
 * in order to evaluate various statements in different ways
 * @author Jonathan Lee
 * @version 9 Oct 2014
 *
 */
public abstract class Expression
{
	/**
	 * evaluations that can be implemented in different ways depending on object
	 * @param env	environment used for variable definitions
	 */
	public abstract int eval(Environment env);
	
	/**
	 * abstract method responsible for compiling the code via an
	 * emitter based on the component of the grammar.
	 * @param e		emitter used for output of file
	 */
	public abstract void compile(Emitter e);
}
