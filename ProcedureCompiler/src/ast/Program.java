package ast;

import java.util.List;

import emitter.Emitter;
import environment.Environment;

/**
 * Program class which represents the entire program
 * this class will have to harbor a list of procedure declarations
 * which come before the actual running statement of the program
 * those procedure declarations are held in a list
 * lastly the statement of the program is independent and
 * is responsible for the actually running program
 * this class unlike many of the others is not considered
 * a statement. it is completely independent
 * its execution involves the execution of the procedure declarations
 * and the given statement
 * @author Jonathan Lee
 * @version 3 Nov 2014
 *
 */
public class Program
{
	private List<String> varDecs;
	private List<ProcedureDeclaration> decs;
	private Statement stmt;
	
	/**
	 * method: Program
	 * constructor method for the program
	 * which acts as a setter method for the statement
	 * and potential other program.
	 * all programs at least need a statement but not all
	 * require another program
	 * @param stmt	the statement that the program executes as either a 
	 */
	public Program(Statement stmt, List<ProcedureDeclaration> decs, List<String> varDecs)
	{
		this.decs = decs;
		this.stmt = stmt;
		this.varDecs = varDecs;
	}
	
	/**
	 * method: exec
	 * usage: program.exec(env)
	 * executes the given program which uses its given
	 * environment to initiate the entire process of the program including declaraing
	 * the procedures and executing the program statement
	 * @param env
	 * postcondition: program executed by executing sub statements
	 */
	public void exec(Environment env)
	{
		for (int i = 0; i < decs.size(); i++)
			decs.get(i).exec(env);
		stmt.exec(env);
	}
	
	public void compile(String fileName)
	{
		Emitter e = new Emitter(fileName);
		
		//text for statements/executions/evaluations
		e.emit(".text");
		e.emit(".globl main");
		e.emit("main: #QTSPIM will automatically look for main");
		//future code here
		stmt.compile(e);
		e.emit("li $v0 10");
		e.emit("syscall # halt");
		for (Statement dec : decs)
		{
			dec.compile(e);
		}
		//data for variable declarations
		e.emit(".data");
		for (String varName : varDecs)
		{
			e.emit("var" + varName + ":");
			e.emit(".word 0");
		}
		e.emit("newline:");
		e.emit(".asciiz \"\\n\"");
		
	}
}
