package ast;

import emitter.Emitter;
import environment.Environment;
/**
 * condition class that represents a condition
 * given by a relative operator and two expressions
 * in specific order
 * @author Jonathan Lee
 * @version 13 Oct 2014
 *
 */
public class Condition
{
	private String relop;
	private Expression exp1;
	private Expression exp2;
	/**
	 * method: Condition
	 * constructor method for the condition class whic
	 * takes in two expression and then
	 * a relative operator to set for instance fields
	 * Warning: order of expressions matters
	 * a > b may not be the same as b > a
	 * @param relop relative operator that is used for comparison fo two expressions
	 * @param exp1  first expression to be compared
	 * @param exp2 	second expression to be compared
	 * postcondition: instance fields set
	 */
	public Condition(String relop, Expression exp1, Expression exp2)
	{
		this.relop = relop;
		this.exp1 = exp1;
		this.exp2 = exp2;
	}
	
	/**
	 * method: eval
	 * usage: program.eval(env)
	 * method evaluates the condition by comparing the two expressions
	 * using the given relative operator for the comparison.
	 * returns a boolean value as a result true/false whether 
	 * the condition meets what is dictated by the relop
	 * relop -> = | <> | < | > | <= | >=
	 * @param env environment use for variable reference
	 * @return	reutrns true/false depending on what is determined
	 * 			by the given relop and the expressions
	 * @throws Exception  in case of invalid relative operator
	 */
	public boolean eval(Environment env)
	{
		if(relop.equals("="))
			return (exp1.eval(env) == exp2.eval(env));
		else if(relop.equals("<>"))
			return (exp1.eval(env) != exp2.eval(env));
		else if(relop.equals("<"))
			return (exp1.eval(env) < exp2.eval(env));
		else if(relop.equals(">"))
			return (exp1.eval(env) > exp2.eval(env));
		else if(relop.equals("<="))
			return (exp1.eval(env) <= exp2.eval(env));
		else if(relop.equals(">="))
			return (exp1.eval(env) >= exp2.eval(env));
		else
			return false;
	}
	
	/**
	 * method: compile
	 * usage: program.compile(e, label)
	 * compile method that compiles the the pascal code
	 * into mips code in the asm file
	 * does this by determining the relative operator
	 * and the function to be performed and then going to the correct label for that function
	 * first load value of exp1 to $t0
	 * then load value of exp2 to $v0
	 * 
	 * @param e		emitter to emit to asm file
	 * @param label to go to depending on result of condition
	 */
	public void compile(Emitter e, String label)
	{
		exp1.compile(e);
		e.emit("move $a0 $v0");
		exp2.compile(e);
		if(relop.equals("="))
			e.emit("bne $a0 $v0 " + label);
		else if(relop.equals("<>"))
			e.emit("beq $a0 $v0 " + label);
		else if(relop.equals("<"))
			e.emit("bge $a0 $v0 " + label);
		else if(relop.equals(">"))
			e.emit("ble $a0 $v0 " + label);
		else if(relop.equals("<="))
			e.emit("bgt $a0 $v0 " + label);
		else if(relop.equals(">="))
			e.emit("blt $a0 $v0 " + label);
	}
	
}
