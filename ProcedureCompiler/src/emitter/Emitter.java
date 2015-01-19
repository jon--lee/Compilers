package emitter;
import java.io.*;
import java.util.List;

import ast.ProcedureDeclaration;

public class Emitter
{
	private PrintWriter out;
	private int id = 0;
	private ProcedureDeclaration context;
	private int excessStackHeight = 0;
	
	//creates an emitter for writing to a new file with given name
	public Emitter(String outputFileName)
	{
		try
		{
			out = new PrintWriter(new FileWriter(outputFileName), true);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	//prints one line of code to file (with non-labels indented)
	public void emit(String code)
	{
		if (!code.endsWith(":"))
			code = "\t" + code;
		out.println(code);
	}

	/**
	 * method: setProcedureContext
	 * usage: program.setProcedureContext(context);
	 * sets the procedure context for the specific set of local
	 * variables that are only relevent to this context
	 * these variable values are pushed/popped in the stack
	 * @param context		specific declaration that is the current
	 * 						context for variable fetching
	 * postcondition: context set to given context
	 */
	public void setProcedureContext(ProcedureDeclaration context)
	{
		this.context = context;
		this.excessStackHeight = 0;
	}
	
	/**
	 * method: voidProcedureContext
	 * usage: program.voidProcedureContext
	 * voids the procedure contexts by setting the
	 * context instance field to null indicating
	 * no procedure to fetch local variables
	 * @postcondition: context set to null
	 */
	public void clearProcedureContext()
	{
		context = null;
	}
	
	/**
	 * method: isLocalVariable
	 * returns true if the variable is a local variable
	 * for the specific context or a global variable (false)
	 * @param varName name of the potential local variable
	 * @return	boolean true or false if it is a local variable or a global variable
	 */
	public boolean isLocalVariable(String varName)
	{
		if(context == null)
			return false;
		else
		{
			List<String> params = context.getParms();
			List<String> localVars = context.getLocalVars();
			boolean r = false;
			for (int i = 0; i < params.size(); i++)
				if(params.get(i).equals(varName))
					r = true;
			if(localVars != null)
				for (int i = 0; i < localVars.size(); i++)
					if(localVars.get(i).equals(varName))
						r = true;
			return (r || (varName.equals(context.getId())));
		}
	}
	
	/**
	 * method: getOffset
	 * usage: program.getOffset(localVarName)
	 * returns the offset of the variable in the stack according
	 * to the push/pop method of stack insertion. the index
	 * is determined by the placement of the variables
	 * as parameters in the procedure declaration.
	 * the first is always pushed the farthest
	 * back in the stack (greatest offset).
	 * since the index with given local var name
	 * goes the opposite direction, the reverse index must
	 * be accounted for.
	 * the revIndex is simply multiplied by 4 for the mips code
	 * as 4 bytes.
	 * also adds the excessStackHeight counter to account for other variables added
	 * to the stack
	 * also add one to account for the additional procedure
	 * name at the start of the thing.
	 * precondition: localVarName must actually be a name of a local variable
	 * @param localVarName	name of the local variable to be fetched from stack
	 * @return				the offset for the address of the value of the given variable
	 */
	public int getOffset(String localVarName)
	{
		List<String> params = context.getParms();
		List<String> localVars = context.getLocalVars();
		String[] temp = new String[params.size() + localVars.size()];
		int revIndex = 0;
		
		
		for (int i = 0; i < params.size() + localVars.size(); i++)
			if( (i < params.size() 
					&& params.get(i).equals(localVarName))
					|| (i >= params.size() 
					&& localVars.get(i - params.size()).equals(localVarName)))
				revIndex = params.size() + localVars.size() - i;
		int offset = (revIndex * 4) + this.excessStackHeight;
		System.out.println("height: " + excessStackHeight);
		System.out.println("offset: " + offset);
		return offset;
		
		
		
		
		/*for (int i = 0; i < params.size() + localVars.size(); i++)
			if(i < params.size())
				temp[i] = params.get(i);
			else
				temp[i] = localVars.get(i - params.size());
		for (int i = 0; i < temp.length; i++)
			if(temp[i].equals(localVarName))
				revIndex = temp.length - i;				//take out the minus one to account for first in stack which is method name
		
		System.out.println(revIndex);
		return (revIndex) * 4 + this.excessStackHeight;
		*/
		
		
		
		
		/*
		for (int i = 0; i < localVars.size(); i++)
			if(localVars.get(i).equals(localVarName))
				revIndex = localVars.size() - i - 1;
		if(revIndex > -1)
		{
			System.out.println(revIndex);
			return revIndex * 4;
		}
		else
		{
			for (int i = 0; i < params.size(); i++)
				if(params.get(i).equals(localVarName))
					revIndex = params.size() - i - 1;
			System.out.println(revIndex);
			return (revIndex + 1) * 4 + this.excessStackHeight;		//plus one added for the first stack variable which is just the procedure itself
		}*/
	}
	
	/**
	 * method: push
	 * usage: program.push(reg)
	 * pushes reg to the stack by
	 * setting the $sp to something further in the stack (4 bits)
	 * helpful for binop purposes when there are multiple operations
	 * in a specific order
	 * @param reg variable whose value is put into the stack
	 */
	public void push(String reg)
	{
		this.emit("subu $sp $sp 4");
		this.emit("sw " + reg + " ($sp)");
		this.excessStackHeight += 4;
	}
	
	/**
	 * method: pop
	 * usage: program.pop(reg)
	 * pops value from stack to the reg
	 * setting the $sp to something further in the stack (4 bits)
	 * helpful for binop purposes when there are multiple operations
	 * in a specific order
	 * @param reg	reg is the variable that the value is loaded to
	 */
	public void pop(String reg)
	{
		this.emit("lw " + reg +  " ($sp)");
		this.emit("addu $sp $sp 4");
		this.excessStackHeight -=4;
	}
	
	//closes the file.  should be called after all calls to emit.
	public void close()
	{
		out.close();
	}
	
	/**
	 * method: nextLabelID
	 * usage: program.nextLabelID()
	 * adds one to the label id and returns it
	 * so that each time it is called, the id
	 * is never the same (increments of 1)
	 * @return	the current id + 1
	 */
	public int nextLabelID()
	{
		id = id + 1;
		return id;
	}
	
	
}