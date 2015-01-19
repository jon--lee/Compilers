package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * BinOp class which represents an expression
 * involving multiple expressions joined by an operator
 * for a specific mathematical operation
 * @author Jonathan Lee
 * @version 9 Oct2014
 *
 */
public class BinOp extends Expression
{
	private String op;
	private Expression exp1;
	private Expression exp2;
	
	/**
	 * method: BinOp
	 * constructor method that takes in two expressions
	 * and one operator to construct the overall expression
	 * represented by individual instance field
	 * @param op	operator that determines the operation
	 * @param exp1	first part of the expression operation
	 * @param exp2	second part of the expression operation
	 */
	public BinOp(String op, Expression exp1, Expression exp2)
	{
		this.op = op;
		this.exp1 = exp1;
		this.exp2 = exp2;
	}

	@Override
	/**
	 * method: eval
	 * usage: program.eval(env)
	 * method that evaluates the expression by determining
	 * the type of operator which is then used to carry out 
	 * the operation using the two expressions to join them
	 * returns -1 if no operator recognized.
	 * @param env	environment used for variable definitions
	 * @return	the value of the expression
	 */
	public int eval(Environment env)
	{
		if(op.equals("+"))
			return exp1.eval(env) + exp2.eval(env);
		else if(op.equals("-"))
			return exp1.eval(env) - exp2.eval(env);
		else if(op.equals("*"))
			return exp1.eval(env) * exp2.eval(env);
		else if(op.equals("/"))
			return exp1.eval(env) - exp2.eval(env);
		else
			return -1;
	}
	
	/**
	 * method: compile
	 * usage: program.compile(e)
	 * compile method that converts the ast
	 * binary operator into an arithmetic function between
	 * two expressions into readable mips code by compiling each
	 * expression and then adding an operation between the two
	 * this is done via the stack so that values are not lost in
	 * the branching of the ast. an explicated example is given for the first
	 * operation
	 * does not compile if operator is not one of the four listed operators
	 * @param	e		emitter used to write to file for compiled code.
	 * postcondition: 	mips code compiled to emitter file for operation
	 * 					stack pointer shifted
	 */
	public void compile(Emitter e)
	{
		if(op.equals("+"))
		{
			exp1.compile(e);
			e.push("$v0");
			exp2.compile(e);
			e.pop("$t0");
			e.emit("addu $v0 $t0 $v0");
		}
		else if(op.equals("-"))
		{
			exp1.compile(e);
			e.push("$v0");
			exp2.compile(e);
			e.pop("$t0");
			e.emit("subu $v0 $t0 $v0");
		}
		else if(op.equals("*"))
		{
			exp1.compile(e);
			e.push("$v0");
			exp2.compile(e);
			e.pop("$t0");
			e.emit("mult $t0 $v0");
			e.emit("mflo $v0");
		}
		else if(op.equals("/"))
		{
			exp1.compile(e);
			exp1.compile(e);
			e.push("$v0");
			exp2.compile(e);
			e.pop("$t0");
			e.emit("div $t0 $v0");
			e.emit("mflo $v0");
		}
	}
}
