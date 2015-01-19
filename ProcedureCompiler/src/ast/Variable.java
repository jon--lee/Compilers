package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * Variable class the represents a variable
 * which corresponds to a value in the environment
 * just contains the reference as a string object
 * for the name
 * @author Jonathan Lee
 * @version 9 Oct2014
 *
 */
public class Variable extends Expression
{
	private String name;
	
	/**
	 * method: Variable
	 * constructor method that takes in a name
	 * to set for the instance field
	 * @param name	name that will be set for reference
	 * postcondition: name set
	 */
	public Variable(String name)
	{
		this.name = name;
	}

	@Override
	/**
	 * method: eval
	 * usage: program.eval(env)
	 * returns the value of the variable by getting it
	 * from the given env
	 * @param env	environment used for variable definitions
	 * @return value of the variable from env
	 */
	public int eval(Environment env)
	{
		return env.getVariable(name);
	}

	@Override
	/**
	 * method: compile
	 * usage: program.compile(e)
	 * compiles the variable object by
	 * converting the given pascal
	 * to mips code in an asm file.
	 * for a variable, this action is done
	 * by loading the variable form the .data
	 * section into the $v0 object
	 * similar to using an environment. specific to global variables.
	 * postcondition: variable value loaded from .data to $v0
	 */
	public void compile(Emitter e)
	{
		if(e.isLocalVariable(name))
		{
			System.out.println(name + " is a local variable");
			int offset = e.getOffset(name);
			e.emit("addu $t0 $sp " + offset);
			e.emit("lw $v0 ($t0)");
		}
		else
		{
			e.emit("la $t0 " + "var" + name);
			e.emit("lw $v0 ($t0)");
		}
	}
	
	
}
