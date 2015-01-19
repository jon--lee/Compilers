package ast;

import java.util.List;

import emitter.Emitter;
import environment.Environment;

/**
 * @author Jonathan Lee
 * @version 3 Nov 2014
 * 
 * ProcedureCall class is a subclass of Expression that
 * is the result of parsing a call the a procedure listed elsewhere
 * in the program. when this object is created, it receives
 * a identification of a procedure which has a statement that
 * it executes in its own execution.
 * procedure calls are made when a variable is assigned to it
 * declarations of such procedures are obtained through the environment
 * procedure then returns the return value of itself
 */

public class ProcedureCall extends Expression
{
	private String id;
	private List<Expression> args;
	/**
	 * method: ProcedureCall
	 * constructor method for the class which takes in 
	 * an id that refers to a procedure declaration that
	 * contains a statement
	 * arguments refer to  the procedure declaration parameters
	 * @param id	id of the procedure declaration referenced from env
	 * @param args	arguments that the procedure may take, refer to parameters in method declaration
	 * postcondition: instance variables set
	 */
	public ProcedureCall(String id, List<Expression> args)
	{
		this.id = id;
		this.args = args;
	}
	
	/**
	 * method: exec
	 * usage: program.exec(env)
	 * execution method from the superclass
	 * which just simply gets the procedure's action statement
	 * with its given id
	 * assigns all the argument to the given variable names from the parameters
	 * from the environment and executes it.
	 * execution of the procedure is similar to that of a general statement
	 * also in this, the name of the procedure is stored as a variable for the value 0 initially
	 * because in this language, the evaluation of a procedure call returns the valueo of the 
	 * variable (local) with the name of the procedure
	 * @param env	environment for variable/procedure reference
	 * @return id	returns the value of the variable with the name of the procedure
	 * postcondition: evaluates the expression of the procedure with id returning the return value of the procedure
	 */
	public int eval(Environment env)
	{
		//first assign all the parameters to the global environment (will be changed)
		ProcedureDeclaration proDec = env.getProDec(id);
		List<String> parms = proDec.getParms();
		Environment localEnv = new Environment(env);
		new Assignment(id, new Number(0)).exec(localEnv);		//number is just 0 because assignment takes only expressions
		for (int i = 0;i < parms.size(); i++)				//if there are fewer arguments than params, it will throw an error
		{
			new Assignment(parms.get(i), args.get(i)).exec(localEnv);
		}
		env.getProDec(id).getStmt().exec(localEnv);
		return localEnv.getVariable(id);
	}

	@Override
	/**
	 * method: compile
	 * usage: program.compile(e)
	 * compiles the program by converting the code
	 * to mips code in an asm file. procedures are called
	 * by jumping to the label that describes there action
	 * while retaining the return address.
	 * @param e		emitter used to write to asm file
	 */
	public void compile(Emitter e)
	{
		e.push("$ra");										//pushing the old return address to stack (new one will be used)
															//could even be a meaningless value ie there was no old ra
		
		for (int i = 0; i < args.size(); i++)				//pushing all the arguments to the stack
		{
			Expression arg = args.get(i);
			arg.compile(e);
			e.push("$v0");
		}
		
		
		String label = "proc" + id;
		e.emit("jal " + label);				//execute
		
		//e.pop("$v0");											//pop the procedure name value into v0

		for (int i = 0; i < args.size(); i++)					//pop off all of the arguments after procedure (no use)
			e.pop("$t0");
		e.pop("$ra");
	}
	
	
}
