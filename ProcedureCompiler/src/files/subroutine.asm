	.text
	.globl main
	main: #QTSPIM will automatically look for main
	subu $sp $sp 4
	sw $ra ($sp)		#save the ra

	li $v0 10
	subu $sp $sp 4
	sw $v0 ($sp)		#load first argument to stack

	li $v0 15
	subu $sp $sp 4
	sw $v0 ($sp)		#load second argument to stack

	jal procmax2		#execute

	lw $v0 ($sp)		#load return value from top of stack

	addu $sp $sp 4
	lw $t0 ($sp)
	addu $sp $sp 4
	lw $t0 ($sp)		#eliminate arguments from stack

	addu $sp $sp 4
	lw $ra ($sp)		#get the return address back

	move $a0 $v0
	li $v0 1
	syscall				#print out the return value

	li $v0 10
	syscall # halt
procmax2:
	li $v0 0
	subu $sp $sp 4
	sw $v0 ($sp)
	#top of the stack is now return variable

	addu $t0 $sp 4 #first value on stack (not return) which is second argument
	lw $v0 ($t0)
	sw $v0 ($sp)	#save second parameter to return value (first on stack ie no height)

	addu $t0 $sp 8
	lw $a0 ($t0)		#load first parameter to a0
	addu $t0 $sp 4
	lw $v0 ($t0)		#load second parameter to v0
	ble $a0 $v0 endif1   #skip to endif1 if not true
	sw $a0 ($sp)
endif1:
	jr $ra
	.data
varignore:
	.word 0
newline:
	.asciiz "\n"
