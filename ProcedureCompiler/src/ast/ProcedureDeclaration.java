package ast;

import java.util.ArrayList;
import java.util.List;

import emitter.Emitter;
import environment.Environment;

/**
 * procedure declaration class which represents
 * a statement that occurs when a procedure is called
 * this class only contains a stmt which it returns
 * when it is asked for it
 * 
 * no other functions in this method other than getter and constructor
 * setter
 * 
 * @author Jonathan Lee
 * @version 3 Nov 2014
 * 
 * 
 */
public class ProcedureDeclaration extends Statement
{
	private String id;
	private List<String> parms;
	private Statement stmt;
	private List<String> localVars;
	/**
	 * method: ProcedureDeclaration
	 * constructor method that acts a the setter
	 * for the procedure declaration, (proDec is only
	 * set in this method as an instance field)
	 * @param stmt	stmt value that the field is set to
	 * @param id	id name of the declaration
	 * @param parms	list of parameters for the procedure (could be 0)
	 * postcondition: stmt instance field set to parameter
	 */
	public ProcedureDeclaration(String id, List<String> parms, Statement stmt)
	{
		this.id = id;
		this.parms = parms;
		this.stmt = stmt;
		this.localVars = new ArrayList<String>();
	}
	
	/**
	 * method: ProcedureDeclaration
	 * constructor method that acts a the setter
	 * for the procedure declaration, (proDec is only
	 * set in this method as an instance field)
	 * alternative constructor that takes local variables as well
	 * @param stmt	stmt value that the field is set to
	 * @param id	id name of the declaration
	 * @param parms	list of parameters for the procedure (could be 0)
	 * postcondition: stmt instance field set to parameter
	 */
	public ProcedureDeclaration(String id, List<String> parms, Statement stmt, List<String> localVars)
	{
		this.id = id;
		this.parms = parms;
		this.stmt = stmt;
		this.localVars = localVars;
	}
	
	
	/**
	 * method: exec
	 * usage: program.exec()
	 * method is inherited from super class as a statement
	 * the purpose of this statement is to assign itself a value
	 * in the environment for the specific procedure to be
	 * referenced in the procedure call. see the assignment class
	 * for a more straight forward anology.
	 * @param env		local/global environment for reference
	 * postcondition: reference put into given environment
	 */
	public void exec(Environment env)
	{
		env.setProDec(id, this);
	}
	
	/**
	 * method: getStmt
	 * usage: program.getStmt()
	 * returns the stmt instance field
	 * just a simple getter method
	 * @return stmt		the statement instance field that represents the actions of the procedure
	 * postcondition: stmt returned
	 */
	public Statement getStmt()
	{
		return stmt;
	}
	
	/**
	 * method: getParms
	 * usage: program.getParms
	 * simple getter method for the declarations paraemeters
	 * that are needed by the procedure call to cross check and assigng with arguments
	 * @return parms	list of procedure parameters
	 */
	public List<String> getParms()
	{
		return parms;
	}

	/**
	 * method: getLocalVars
	 * usage: program.getLocalVars()
	 * simple getter method for the declaration's local
	 * variables
	 * @return localVars	the list of this specific procedures local vars
	 */
	public List<String> getLocalVars()
	{
		return localVars;
	}
	/**
	 * method: getId
	 * usage: program.getId()
	 * method that returns the id of the procedure
	 * simple getter method
	 * @return id		the idea of the procedure
	 */
	public String getId()
	{
		return id;
	}
	
	@Override
	/**
	 * method: compile
	 * usage: program.compile(e)
	 * compiles the program into mips code
	 * on the asm file from the pascal code
	 * does this by simply listing the
	 * procedure's statement which includes
	 * its function under its identifier to be jumped to
	 * @param e		emitter used to write to asm file
	 */
	public void compile(Emitter e)
	{
		e.emit("proc" + id + ":");
		
		if(localVars != null)
			for (int i = 0; i < localVars.size(); i++)		//setting the local variables to 0 values before excess stack height is accounted for
			{
				e.emit("li $v0 0");							//for each local var, load a 0 value
				e.push("$v0");								//push that value so that offset is still in place
			}
		e.emit("li $v0 0");					//must also push the name of the function
		e.push("$v0");						//as if it is a parameter to be used as a return value following the call
		
		e.setProcedureContext(this);
		
		stmt.compile(e);
		
		if(localVars != null)
			for (int i = 0; i < localVars.size(); i++)		//pop off all of the local vars and later the arguments (no need beyond procedure)
				e.pop("$t0");
		
		e.clearProcedureContext();
		e.pop("$v0");											//pop the procedure name value into v0

		e.emit("jr $ra");								//jump back to before procedure call
		
	}
}
