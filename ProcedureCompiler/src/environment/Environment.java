package environment;

import java.util.HashMap;

import ast.ProcedureDeclaration;
import ast.Statement;

/**
 * Environment class that represents the environment in which
 * procedures and variables are referenced. hashmaps are used
 * as the core of the environment
 * environments essentially represent scopes of the program.
 * for example local variables will not necessarily be
 * the same in the global scope.
 * each scope is independent of the other
 * 
 * @author Jonathan Lee
 * @version 3 Nov 2014
 * 
 * 
 * 
 */

public class Environment
{
	private HashMap<String, Integer> map;
	private HashMap<String, ProcedureDeclaration> proMap;
	private Environment parent;
	
	
	/**
	 * method: Environment
	 * constructor method for the class which just initializes
	 * the hashmap objects as one of string-integer and one of
	 * string-statement
	 * parent initially set to null (none given)
	 * postcondition: hashmaps initialized
	 */
	public Environment()
	{
		map = new HashMap<String, Integer>();
		proMap = new HashMap<String, ProcedureDeclaration>();
		parent = null;
	}
	
	/**
	 * method: Environment
	 * constructor method for the class which just initializes
	 * the hashmap objects as one of string-integer and one of
	 * string-statement
	 * modified environment constructor which takes a parent environment argument
	 * parent is the environment that takes this as its sub environment
	 * @param parent		parent environment that this would refer to
	 * postcondition: hashmaps initialized, parent set
	 */
	public Environment(Environment parent)
	{
		map = new HashMap<String, Integer>();
		proMap = new HashMap<String, ProcedureDeclaration>();
		this.parent = parent;
	}
	
	
	/**
	 * method: setProDec
	 * usage: program.setProDec(variable, stmt)
	 * puts the key and object values into the hashmap
	 * specifically for the statement references
	 * so this corresponds to a procedure
	 * @param variable		variable name for procedure declaration
	 * @param stmt			statement to be executed as procedure
	 * postcondition: procedure declaration is set to this or global
	 */
	public void setProDec(String variable, ProcedureDeclaration stmt)
	{
		if(parent != null)
			parent.setProDec(variable, stmt);
		else
			proMap.put(variable, stmt);
	}
	
	/**
	 * method: declareVariable
	 * usage: program.declareVariable(variable, value)
	 * puts the key and object values into the hasmp
	 * specifically for integer references
	 * so this corresponds to an integer value
	 * @param variable 		variable name for the integer
	 * @param value			the integer value of the variable
	 * postcondition: declares the variable by setting name and value correspondence
	 */
	public void declareVariable(String variable, int value)
	{
		map.put(variable, value);
	}
	
	/**
	 * method: setVariable
	 * usage: program.setVariable(variable, value)
	 * puts the key and object values into the hasmp
	 * specifically for integer references
	 * so this corresponds to an integer value
	 * if the variable is not declared in the local, it will set it to the
	 * global environment
	 * @param variable 		variable name for the integer
	 * @param value			the integer value of the variable
	 * postcondition: variable set to this or to global if this is not already defined
	 */
	public void setVariable(String variable, int value)
	{
		if(parent != null && map.get(variable) == null)
			parent.setVariable(variable, value);
		else
			map.put(variable, value);
	}
	
	/**
	 * method: getVariable
	 * usage: program.getVaariable(variable)
	 * getter method using a variable parameter
	 * to get the value from the environment
	 * that it references, for integers
	 * @param variable		variable name of the integer
	 * @return				the integer with given variable name
	 */
	public int getVariable(String variable)
	{
		
		if(parent != null && map.get(variable) == null)
			return parent.getVariable(variable);
		else
			return map.get(variable);
	}
	
	/**
	 * method: getStatement
	 * usage: program.getStatement(variable)
	 * see above but in the case that the fetched datum is
	 * a statement not an integer
	 * @param variable		variable name of statement
	 * @return				the statement with given variable name
	 */
	public ProcedureDeclaration getProDec(String variable)
	{
		if(parent != null)
			return parent.getProDec(variable);
		else
			return proMap.get(variable);
	}
}
