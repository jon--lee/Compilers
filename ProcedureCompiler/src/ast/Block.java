package ast;

import java.util.List;

import emitter.Emitter;
import environment.Environment;

/**
 * Block class which is a statement
 * that represents multiple statements
 * in an arraylist
 * all these statements are then executeed
 * when the execute method is called
 * @author Jonathan Lee
 * @version 9 Oct2014
 *
 */
public class Block extends Statement
{
	private List<Statement> stmts;
	
	/**
	 * method: Block
	 * constructor method that takes a alist
	 * object which is then used to set the instnace
	 * field
	 * @param stmts	variable to be set for stmts
	 * postcondition: stmts set
	 */
	public Block(List<Statement> stmts)
	{
		this.stmts = stmts;
	}

	@Override
	/**
	 * method exec
	 * usage: program.exec(env)
	 * executes the statement which involves
	 * executing all the statements contained in the stmts
	 * instance field list
	 * @param env	environment used for variable definitions
	 * postcondition: executed
	 */
	public void exec(Environment env)
	{
		for (int i = 0; i < stmts.size(); i++)
			stmts.get(i).exec(env);
	}
	
	/**
	 * method: compile
	 * usage: program.compile(env);
	 * compile method that converts the ast
	 * block statement into a series of statements that can be
	 * interpreted as mips. does this by compiling all the
	 * statements in the list of statements individually
	 * @param env		environment for varaible references.
	 */
	public void compile(Emitter e)
	{
		for (int i = 0; i < stmts.size(); i++)
			stmts.get(i).compile(e);
	}
}
