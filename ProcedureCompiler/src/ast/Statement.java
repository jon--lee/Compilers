package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * Abstract statement class extended by sub classes
 * in order to execute various statements in different ways
 * @author Jonathan Lee
 * @version 9 Oct 2014
 *
 */
public abstract class Statement
{
	/**
	 * executions that can be implemented in different ways depending on object
	 * @param env	environment used for variable definitions
	 */
	public abstract void exec(Environment env);
	
	/**
	 * abstract method responsible for compiling the code via an
	 * emitter based on the component of the grammar.
	 * @param e		emitter used for output of file
	 */
	public abstract void compile(Emitter e);
}